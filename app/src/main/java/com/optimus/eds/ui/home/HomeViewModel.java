package com.optimus.eds.ui.home;

import android.app.Application;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.PersistableBundle;
import android.util.Log;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.annotation.NonNull;
import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Flowable;

import io.reactivex.Observable;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;
import retrofit2.Response;

import com.google.gson.Gson;
import com.optimus.eds.Constant;
import com.optimus.eds.db.AppDatabase;
import com.optimus.eds.db.dao.PricingDao;
import com.optimus.eds.db.entities.OrderStatus;
import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.db.entities.pricing.PriceAccessSequence;
import com.optimus.eds.db.entities.pricing.PriceBundle;
import com.optimus.eds.db.entities.pricing.PriceCondition;
import com.optimus.eds.db.entities.pricing.PriceConditionClass;
import com.optimus.eds.db.entities.pricing.PriceConditionDetail;
import com.optimus.eds.db.entities.pricing.PriceConditionEntities;
import com.optimus.eds.db.entities.pricing.PriceConditionScale;
import com.optimus.eds.db.entities.pricing.PriceConditionType;
import com.optimus.eds.db.entities.pricing_models.PcClassWithPcType;
import com.optimus.eds.model.AppUpdateModel;
import com.optimus.eds.model.OrderModel;
import com.optimus.eds.model.PricingModel;
import com.optimus.eds.model.WorkStatus;
import com.optimus.eds.source.JobIdManager;
import com.optimus.eds.source.MasterDataUploadService;
import com.optimus.eds.source.RetrofitHelper;
import com.optimus.eds.source.UploadOrdersService;
import com.optimus.eds.ui.order.OrderBookingRepository;
import com.optimus.eds.ui.route.outlet.OutletListActivity;
import com.optimus.eds.ui.route.outlet.OutletListRepository;
import com.optimus.eds.utils.PreferenceUtil;
import com.optimus.eds.utils.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class HomeViewModel extends AndroidViewModel {

    private final String TAG = HomeViewModel.class.getSimpleName();
    private final HomeRepository repository;
    private final CompositeDisposable disposable;

    private MutableLiveData<Boolean> endDayLiveData;
    private MutableLiveData<AppUpdateModel> appUpdateLiveData;

    private PricingDao pricingDao;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        ExecutorService executors = Executors.newSingleThreadExecutor();
        repository = HomeRepository.singleInstance(application,RetrofitHelper.getInstance().getApi(),executors);
        disposable = new CompositeDisposable();
        pricingDao = AppDatabase.getDatabase(application).pricingDao();
        endDayLiveData = new MutableLiveData<>();
        appUpdateLiveData = new MutableLiveData<>();

    }

    public MutableLiveData<Boolean> onStartDay(){
        return repository.startDay();
    }

    public void download(){
        isLoading().postValue(true);
        repository.fetchTodayData(false);
    }

    public void startDay(){
        // PreferenceUtil.getInstance(getApplication()).clearAllPreferences();
        repository.getToken();
    }

    public void updateDayEndStatus(){
        isLoading().postValue(true);
        repository.updateWorkStatus(false);
    }

    public MutableLiveData<Boolean> getEndDayLiveData() {
        return endDayLiveData;
    }



    public Observable<List<Outlet>> findOutletsWithPendingTasks() {
        return OutletListRepository.getInstance(getApplication()).getOutletsWithNoVisits()
                .toObservable().subscribeOn(Schedulers.computation());
    }
// --Commented out by Inspection START (8/21/2019 12:04 PM):
//    private List<Outlet> visitedOutlets(List<Outlet> allOutlets) {
//        List<Outlet> visitedOutlets = new ArrayList<>(allOutlets.size());
//        for (Outlet outlet : allOutlets) {
//            if(outlet.getVisitStatus()==1)
//                visitedOutlets.add(outlet);
//        }
//        return visitedOutlets;
//    }
// --Commented out by Inspection STOP (8/21/2019 12:04 PM)

    public void pushOrdersToServer(){


        List<OrderStatus> count =  OutletListRepository.getInstance(getApplication()).getOrderStatus().blockingFirst();
        if(count.size()<1) {
            getErrorMsg().postValue("Updated!");
            return;
        }

        disposable.add(findPendingOrders(count)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.computation())
                .subscribe(outlet -> {
                    Log.i(TAG,"OnNext");
                    if(outlet.getVisitStatus()>=7)
                        scheduleMasterJob(getApplication(),outlet,"Bearer "+PreferenceUtil.getInstance(getApplication()).getToken());
                    else
                        emptyCheckoutOrderJob(getApplication(),outlet,"Bearer "+PreferenceUtil.getInstance(getApplication()).getToken());
                },this::onError,()->{ Log.i(TAG,"OnComplete");
                    isLoading().postValue(false);
                }));

    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
        disposable.dispose();
    }

    private Observable<Outlet> findPendingOrders(List<OrderStatus> nonSyncedOutlets) {
        List<Long> outlets = new ArrayList(nonSyncedOutlets.size());

        for(OrderStatus status:nonSyncedOutlets){
            outlets.add(status.getOutletId());
        }

        return OutletListRepository.getInstance(getApplication()).getUnsyncedOutlets(outlets)
                .concatMap(Flowable::fromIterable).toObservable().subscribeOn(Schedulers.computation());
    }

    // schedule
    private void scheduleMasterJob(Context context, Outlet outlet, String token) {
        PersistableBundle extras = new PersistableBundle();
        extras.putLong(Constant.EXTRA_PARAM_OUTLET_ID,outlet.getOutletId());
        extras.putString(Constant.TOKEN, token);
        ComponentName serviceComponent = new ComponentName(context, UploadOrdersService.class);
        JobInfo.Builder builder = new JobInfo.Builder(JobIdManager.getJobId(JobIdManager.JOB_TYPE_MASTER_UPLOAD,outlet.getOutletId().intValue()), serviceComponent);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY); // require any network
        builder.setMinimumLatency(1);
        builder.setOverrideDeadline(1);
        builder.setExtras(extras);
        builder.setPersisted(false);
        JobScheduler jobScheduler = ContextCompat.getSystemService(context,JobScheduler.class);
        Objects.requireNonNull(jobScheduler).schedule(builder.build());
    }

    private void emptyCheckoutOrderJob(Context context,Outlet outlet,String token){
        PersistableBundle extras = new PersistableBundle();
        extras.putLong(Constant.EXTRA_PARAM_OUTLET_ID,outlet.getOutletId());
        extras.putInt(Constant.EXTRA_PARAM_OUTLET_STATUS_ID,outlet.getVisitStatus());
        extras.putDouble(Constant.EXTRA_PARAM_PRESELLER_LAT,outlet.getVisitTimeLat());
        extras.putDouble(Constant.EXTRA_PARAM_PRESELLER_LNG,outlet.getVisitTimeLng());
        extras.putString(Constant.TOKEN, token);
        ComponentName serviceComponent = new ComponentName(context, MasterDataUploadService.class);
        JobInfo.Builder builder = new JobInfo.Builder(JobIdManager.getJobId(JobIdManager.JOB_TYPE_MASTER_UPLOAD,outlet.getOutletId().intValue()), serviceComponent);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY); // require any network
        builder.setMinimumLatency(1);
        builder.setOverrideDeadline(1);
        builder.setExtras(extras);
        builder.setPersisted(false);
        JobScheduler jobScheduler = ContextCompat.getSystemService(context,JobScheduler.class);
        Objects.requireNonNull(jobScheduler).schedule(builder.build());
    }

    public MutableLiveData<Boolean> isLoading() {
        return repository.mLoading();
    }

    public MutableLiveData<String> getErrorMsg() {
        return repository.getError();
    }




    public LiveData<Boolean> dayStarted(){
        MutableLiveData<Boolean> when = new MutableLiveData<>();
        WorkStatus syncDate = PreferenceUtil.getInstance(getApplication()).getWorkSyncData();
        when.postValue(syncDate.getDayStarted()!=0);
        return when;
    }

    public LiveData<Boolean> dayEnded(){
        MutableLiveData<Boolean> when = new MutableLiveData<>();
        WorkStatus syncDate = PreferenceUtil.getInstance(getApplication()).getWorkSyncData();
        when.postValue(syncDate.getDayStarted()==2);
        //when.postValue(DateUtils.isToday(syncDate));
        return when;
    }

    public LiveData<AppUpdateModel> appUpdateLiveData(){
        return appUpdateLiveData;
    }

    public void checkDayEnd(){
        Long lastSyncDate = PreferenceUtil.getInstance(getApplication()).getWorkSyncData().getSyncDate();
        if( !Util.isDateToday(lastSyncDate)){
            onStartDay().postValue(false);

        }
    }

    public void checkAppUpdate(){

        repository.updateApp().observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(appUpdateModel -> {
                    if(appUpdateModel.getSuccess()){
                        appUpdateLiveData.postValue(appUpdateModel);
                        // isLoading().postValue(false);
                    }else{
                        getErrorMsg().postValue(appUpdateModel.getMsg());
                    }
                },this::onError);

    }

    private void onError(Throwable throwable) throws IOException {
        throwable.printStackTrace();
        String errorBody = throwable.getMessage();
        if (throwable instanceof HttpException){
            HttpException error = (HttpException)throwable;
            errorBody = error.response().errorBody().string();
        }
        if (throwable instanceof IOException){
            errorBody = "Please check your internet connection";
        }

        getErrorMsg().postValue(errorBody);
        isLoading().postValue(false);

    }



    public void deleteAllPricing(){
        repository.deleteAllPricing()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(() -> {
                    getErrorMsg().postValue("All pricing Deleted Successfully");
                });
    }

    public void loadPricingFromAssets() {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                String data= Util.loadJSONFromAsset(getApplication(),"PricingJson.TXT");
                Gson gson = new Gson();
                PricingModel pricingModel = gson.fromJson(data, PricingModel.class);
                if(pricingModel==null)
                    return;

                insertPriceConditionClasses(pricingModel.getPriceConditionClasses())
                        .andThen(insertConditionTypes(pricingModel.getPriceConditionTypes()))
                        .andThen(insertAccessSequence(pricingModel.getPriceAccessSequences()))
                        .andThen(insertConditions(pricingModel.getPriceConditions()))
                        .andThen(insertPriceBundle(pricingModel.getPriceBundles()))
                        .andThen(insertConditionDetails(pricingModel.getPriceConditionDetails()))
                        .andThen(insertPriceConditionEntities(pricingModel.getPriceConditionEntities()))
                        .andThen(insertPriceConditionScale(pricingModel.getPriceConditionScales()))

                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new CompletableObserver() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onComplete() {
                                getErrorMsg().postValue("Inserted Successfully!");

                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, e.getMessage());
                                getErrorMsg().postValue("Insert db Error");
                                e.printStackTrace();
                            }
                        });

            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, e.getMessage());
                getErrorMsg().postValue("Parse Error");

            }

        });
    }

    Completable insertPriceConditionClasses(List<PriceConditionClass> priceConditionClasses){
        return   Completable.fromAction(() -> {
            pricingDao.insertPriceConditionClasses(priceConditionClasses);
        });
    }
    Completable insertConditionTypes(List<PriceConditionType> priceConditionTypes){
        return   Completable.fromAction(() -> {
            pricingDao.insertPriceConditionType(priceConditionTypes);
        });
    }

    Completable insertConditions(List<PriceCondition> priceConditions){
        return   Completable.fromAction(() -> {
            pricingDao.insertPriceCondition(priceConditions);
        });
    }

    Completable insertAccessSequence(List<PriceAccessSequence> priceAccessSequences){
        return   Completable.fromAction(() -> {
            pricingDao.insertPriceAccessSequence(priceAccessSequences);
        });
    }

    Completable insertConditionDetails(List<PriceConditionDetail> priceConditionDetails){
        return   Completable.fromAction(() -> {
            pricingDao.insertPriceConditionDetail(priceConditionDetails);
        });
    }

    Completable insertPriceBundle(List<PriceBundle> bundles){
        return   Completable.fromAction(() -> {
            pricingDao.insertPriceBundles(bundles);
        });
    }

    Completable insertPriceConditionEntities(List<PriceConditionEntities> entities){
        return   Completable.fromAction(() -> {
            pricingDao.insertPriceConditionEntities(entities);
        });
    }

    Completable insertPriceConditionScale(List<PriceConditionScale> scales){
        return   Completable.fromAction(() -> {
            pricingDao.insertPriceConditionScales(scales);
        });
    }
}
