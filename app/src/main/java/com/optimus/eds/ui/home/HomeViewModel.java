package com.optimus.eds.ui.home;

import android.app.Application;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.PersistableBundle;
import android.text.format.DateUtils;
import android.util.Log;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.annotation.NonNull;
import io.reactivex.Flowable;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import com.optimus.eds.Constant;
import com.optimus.eds.model.OrderModel;
import com.optimus.eds.model.WorkStatus;
import com.optimus.eds.source.JobIdManager;
import com.optimus.eds.source.RetrofitHelper;
import com.optimus.eds.source.UploadOrdersService;
import com.optimus.eds.ui.order.OrderBookingRepository;
import com.optimus.eds.utils.PreferenceUtil;
import com.optimus.eds.utils.Util;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class HomeViewModel extends AndroidViewModel {

    private final String TAG = HomeViewModel.class.getSimpleName();
    private final HomeRepository repository;
    private final CompositeDisposable disposable;
    private PreferenceUtil preferenceUtil;

    public HomeViewModel(@NonNull Application application) {
        super(application);
     /*   int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                NUMBER_OF_CORES*2,
                NUMBER_OF_CORES*2,
                60L,
                TimeUnit.SECONDS, new LinkedBlockingQueue<>());*/
        ExecutorService executors = Executors.newSingleThreadExecutor();
        repository = HomeRepository.singleInstance(application,RetrofitHelper.getInstance().getApi(),executors);
        disposable = new CompositeDisposable();
        preferenceUtil = PreferenceUtil.getInstance(application);

        WorkStatus status  = preferenceUtil.getWorkSyncData();

        // Starting new Day if previous is ended
        if(status.getDayStarted()==2 && status.getSyncDate()!=null
                && Util.isPastDate(status.getSyncDate())){
            status.setDayStarted(0);
            status.setSyncDate(null);status.setEndDate(null);
            preferenceUtil.saveWorkSyncData(status);
        }

    }

    public LiveData<Boolean> onStartDay(){
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

    public void dayEnd(){
        isLoading().postValue(true);
        repository.updateWorkStatus(false);
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

        List<OrderModel> count =  OrderBookingRepository.singleInstance(getApplication()).findPendingOrder().blockingFirst();
        if(count.size()<1) {
            getErrorMsg().postValue("Already Uploaded");
            return;
        }
        disposable.add(findPendingOrders()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.computation())
                .subscribe(orderModel -> {
                    Log.i(TAG,"OnNext");
                    scheduleMasterJob(getApplication(),orderModel.getOrder().getOutletId(),"Bearer "+PreferenceUtil.getInstance(getApplication()).getToken());
                },this::onError,() -> Log.i(TAG,"OnComplete")));

    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
        disposable.dispose();
    }

    private Observable<OrderModel> findPendingOrders() {
        return OrderBookingRepository.singleInstance(getApplication()).findPendingOrder()
                .concatMap(Flowable::fromIterable).toObservable().subscribeOn(Schedulers.computation());
    }

    // schedule
    private void scheduleMasterJob(Context context, Long outletId, String token) {
        PersistableBundle extras = new PersistableBundle();
        extras.putLong(Constant.EXTRA_PARAM_OUTLET_ID,outletId);
        extras.putString(Constant.TOKEN, token);
        ComponentName serviceComponent = new ComponentName(context, UploadOrdersService.class);
        JobInfo.Builder builder = new JobInfo.Builder(JobIdManager.getJobId(JobIdManager.JOB_TYPE_MASTER_UPLOAD,outletId.intValue()), serviceComponent);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY); // require any network
        builder.setExtras(extras);
        builder.setPersisted(true);
        JobScheduler jobScheduler = ContextCompat.getSystemService(context,JobScheduler.class);
        Objects.requireNonNull(jobScheduler).schedule(builder.build());
    }

    public MutableLiveData<Boolean> isLoading() {
        return repository.mLoading();
    }

    public MutableLiveData<String> getErrorMsg() {
        return repository.getError();
    }



    private void onError(Throwable throwable) {
        isLoading().postValue(false);
        getErrorMsg().postValue(throwable.getMessage());
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
}
