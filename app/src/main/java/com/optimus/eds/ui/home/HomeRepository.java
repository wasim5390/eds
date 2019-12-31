package com.optimus.eds.ui.home;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import android.util.Log;

import com.optimus.eds.Constant;
import com.optimus.eds.db.AppDatabase;
import com.optimus.eds.db.dao.CustomerDao;
import com.optimus.eds.db.dao.MerchandiseDao;
import com.optimus.eds.db.dao.OrderDao;
import com.optimus.eds.db.dao.OrderStatusDao;
import com.optimus.eds.db.dao.PriceConditionEntitiesDao;
import com.optimus.eds.db.dao.PricingDao;
import com.optimus.eds.db.dao.ProductsDao;
import com.optimus.eds.db.dao.RouteDao;

import com.optimus.eds.db.entities.Order;
import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.db.entities.pricing.PriceAccessSequence;
import com.optimus.eds.db.entities.pricing.PriceBundle;
import com.optimus.eds.db.entities.pricing.PriceCondition;
import com.optimus.eds.db.entities.pricing.PriceConditionClass;
import com.optimus.eds.db.entities.pricing.PriceConditionDetail;
import com.optimus.eds.db.entities.pricing.PriceConditionEntities;
import com.optimus.eds.db.entities.pricing.PriceConditionScale;
import com.optimus.eds.db.entities.pricing.PriceConditionType;
import com.optimus.eds.model.AppUpdateModel;
import com.optimus.eds.model.BaseResponse;
import com.optimus.eds.model.LogModel;
import com.optimus.eds.model.PackageProductResponseModel;
import com.optimus.eds.model.PricingModel;
import com.optimus.eds.model.RouteOutletResponseModel;
import com.optimus.eds.model.WorkStatus;
import com.optimus.eds.source.API;
import com.optimus.eds.source.TokenResponse;
import com.optimus.eds.ui.route.outlet.OutletListRepository;
import com.optimus.eds.utils.PreferenceUtil;


import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.CompletableSource;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class HomeRepository {

    private final String TAG=HomeRepository.class.getSimpleName();
    private static HomeRepository repository;
    private final PreferenceUtil preferenceUtil;
    private final OrderDao orderDao;
    private final OrderStatusDao statusDao;
    private CustomerDao customerDao;
    private MerchandiseDao merchandiseDao;
    private ProductsDao productsDao;
    private RouteDao routeDao;
    private PriceConditionEntitiesDao pricingDao;


    private MutableLiveData<Boolean> isLoading;
    private MutableLiveData<String> msg;
    private MutableLiveData<Boolean> onDayStartLiveData;
    private MutableLiveData<AppUpdateModel> onUpdateReq;
    private API webService;
    private Executor executor;

    public static HomeRepository singleInstance(Application application, API api, Executor executor){
        if(repository==null)
            repository = new HomeRepository(application,api,executor);
        return repository;
    }

    private HomeRepository(Application application, API api, Executor executor) {
        AppDatabase appDatabase = AppDatabase.getDatabase(application);
        preferenceUtil = PreferenceUtil.getInstance(application);
        productsDao = appDatabase.productsDao();
        orderDao = appDatabase.orderDao();
        routeDao = appDatabase.routeDao();
        statusDao = appDatabase.orderStatusDao();
        pricingDao = appDatabase.priceConditionEntitiesDao();
        merchandiseDao = appDatabase.merchandiseDao();
        customerDao = appDatabase.customerDao();
        isLoading = new MutableLiveData<>();
        onDayStartLiveData = new MutableLiveData<>();
        msg = new MutableLiveData<>();
        onUpdateReq = new MutableLiveData<>();
        webService = api;
        this.executor = executor;

    }

    /**
     * Get User Token from server
     */
    public void getToken(){
        isLoading.setValue(true);
        String username = preferenceUtil.getUsername();
        String password = preferenceUtil.getPassword();
        webService.getToken("password",username,password)
                .observeOn(Schedulers.io()).subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableSingleObserver<TokenResponse>() {
                    @Override
                    public void onSuccess(TokenResponse tokenResponse) {
                        preferenceUtil.saveToken(tokenResponse.getAccessToken());
                        updateWorkStatus(true);
                    }

                    @Override
                    public void onError(Throwable e) {
                        isLoading.postValue(false);
                        msg.postValue(e.getMessage());
                    }
                });

    }

    /**
     * Fetch current day Routes/Outlets
     * @param onDayStart {True for onDayStart, False for Download click}
     */
    public void fetchTodayData(boolean onDayStart){

        executor.execute(() -> {
            try {
                Response<RouteOutletResponseModel> response = webService.loadTodayRouteOutlets().execute();
                if(response.isSuccessful()){
                    //
                    preferenceUtil.saveDistributionId(response.body().getDistributionId());
                    deleteAllRoutesAssets()
                            .andThen(deleteAllOutlets(onDayStart))
                            .andThen(Completable.fromAction(() -> {
                                if(onDayStart)
                                {
                                    routeDao.deleteAllMerchandise();
                                    customerDao.deleteAllCustomerInput();
                                }
                            }))

                            .andThen(Completable.fromAction(() -> {
                        routeDao.insertRoutes(response.body().getRouteList());
                    })).andThen(Completable.fromAction(() ->  routeDao.insertOutlets(response.body().getOutletList())))
                            .andThen(Completable.fromAction(() -> { routeDao.insertAssets(response.body().getAssetList());}))
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.single()).subscribe(new CompletableObserver() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onComplete() {
                            // if(onDayStart)
                            //     loadPricing();
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e(TAG,e.getMessage());
                            e.printStackTrace();
                        }
                    });

                }
                else{
                    msg.postValue(Constant.GENERIC_ERROR);
                }

            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG,e.getMessage()+"");
                msg.postValue(Constant.GENERIC_ERROR);
            }


        });



        executor.execute(() -> {
            try {
                Response<PackageProductResponseModel> response = webService.loadTodayPackageProduct().execute();
                if(response.isSuccessful()){

                    productsDao.deleteAllPackages();
                    productsDao.deleteAllProductGroups();
                    productsDao.deleteAllProducts();
                    productsDao.insertProductGroups(response.body().getProductGroups());
                    productsDao.insertPackages(response.body().getPackageList());
                    productsDao.insertProducts(response.body().getProductList());

                }
                else{
                    msg.postValue(response.errorBody().string());
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG,e.getMessage()+"");
                msg.postValue(Constant.GENERIC_ERROR);
            }finally {
                isLoading.postValue(false);
            }


        });
        loadPricing();

    }




    public void loadPricing() {
        executor.execute(() -> {
            try {
                Response<PricingModel> response = webService.loadPricing().execute();
                if(response.isSuccessful()){
                    PricingModel pricingModel = response.body();
                    deleteAllPricing()
                            .andThen(insertPriceConditionClasses(pricingModel.getPriceConditionClasses()))
                            .andThen(insertConditionTypes(pricingModel.getPriceConditionTypes()).delay(200, TimeUnit.MILLISECONDS))
                            .andThen(insertAccessSequence(pricingModel.getPriceAccessSequences()).delay(200, TimeUnit.MILLISECONDS))
                            .andThen(insertConditions(pricingModel.getPriceConditions()))
                            .andThen(insertPriceBundle(pricingModel.getPriceBundles()))
                            .andThen(insertConditionDetails(pricingModel.getPriceConditionDetails()))
                            .andThen(insertPriceConditionEntities(pricingModel.getPriceConditionEntities()))
                            .andThen(insertPriceConditionScale(pricingModel.getPriceConditionScales()))
                            .observeOn(Schedulers.io())
                            .subscribeOn(Schedulers.io())
                            .subscribe(new CompletableObserver() {
                                @Override
                                public void onSubscribe(Disposable d) {

                                }

                                @Override
                                public void onComplete() {
                                    msg.postValue("Pricing Loaded Successfully!");
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Log.e(TAG,e.getMessage());
                                    msg.postValue(Constant.GENERIC_ERROR);
                                }
                            });

                }
                else{
                    msg.postValue(response.errorBody().string());
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG,e.getMessage()+"");
                msg.postValue(Constant.GENERIC_ERROR);
            }finally {
                isLoading.postValue(false);
            }

        });
    }


    public Completable deleteAllRoutesAssets(){
        return Completable.fromAction(()->{
            routeDao.deleteAllRoutes();
            routeDao.deleteAllAssets();

        });
    }
    public Completable deleteAllPricing(){
        return   Completable.fromAction(()-> pricingDao.deleteAllPriceConditionClasses())
                .andThen(Completable.fromAction(()-> pricingDao.deleteAllPriceBundles()));
               // .andThen(Completable.fromAction(()->pricingDao.deleteAllPriceConditionEntities()));

    }

    public Completable deleteAllOutlets(boolean onStartDay){
        if(onStartDay)
        return Completable.fromAction(()->routeDao.deleteAllOutlets());
        return Completable.complete();
    }

    public Completable deleteAllMerchandise(){
        return Completable.fromAction(()->routeDao.deleteAllMerchandise());
    }

    public Completable deleteAllCustomerInput(){
        return Completable.fromAction(() -> customerDao.deleteAllCustomerInput());
    }

    /**
     * Pricing Start
     */
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
    /**** End Pricing ***/

    /**
     * Save Work status on server {Day started/ Day end}
     * @param isStart {True for dayStart, False for dayEnd}
     */
    public void updateWorkStatus(boolean isStart){
        HashMap<String, Integer> map = new HashMap<>();
        map.put("operationTypeId",isStart?1:2);
        webService.updateStartEndStatus(map).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new SingleObserver<LogModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(LogModel logModel) {
                        isLoading.postValue(false);
                        if(logModel.isSuccess()){
                            WorkStatus status = preferenceUtil.getWorkSyncData();
                            status.setDayStarted(1);
                            status.setSyncDate(logModel.getStartDay());
                            preferenceUtil.saveWorkSyncData(status);
                            onDayStartLiveData.postValue(isStart);
                            if(isStart) {
                                fetchTodayData(isStart);
                            }
                        }else {
                            msg.postValue(logModel.getErrorCode()==2?logModel.getResponseMsg(): Constant.GENERIC_ERROR);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        isLoading.postValue(false);
                        msg.postValue(e.getMessage());
                    }
                });
    }

    public Single<AppUpdateModel> updateApp(){
        return webService.checkAppUpdate();
    }


    public MutableLiveData<Boolean> mLoading() {
        return isLoading;
    }

    public MutableLiveData<Boolean> startDay(){
        return onDayStartLiveData;
    }

    public MutableLiveData<String> getError() {
        return msg;
    }
}
