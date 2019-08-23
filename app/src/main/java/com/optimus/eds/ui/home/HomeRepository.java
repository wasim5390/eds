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
import com.optimus.eds.db.dao.ProductsDao;
import com.optimus.eds.db.dao.RouteDao;

import com.optimus.eds.db.entities.Order;
import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.model.BaseResponse;
import com.optimus.eds.model.LogModel;
import com.optimus.eds.model.PackageProductResponseModel;
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

import io.reactivex.CompletableSource;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class HomeRepository {

    private final String TAG=HomeRepository.class.getSimpleName();
    private static HomeRepository repository;
    private final PreferenceUtil preferenceUtil;
    private final OrderDao orderDao;
    private CustomerDao customerDao;
    private MerchandiseDao merchandiseDao;
    private ProductsDao productsDao;
    private RouteDao routeDao;


    private MutableLiveData<Boolean> isLoading;
    private MutableLiveData<String> msg;
    private MutableLiveData<Boolean> onDayStartLiveData;
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
        merchandiseDao = appDatabase.merchandiseDao();
        customerDao = appDatabase.customerDao();
        isLoading = new MutableLiveData<>();
        onDayStartLiveData = new MutableLiveData<>();
        msg = new MutableLiveData<>();
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
        // webService.getToken("password","imran","imranshabrati");
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
                    //routeDao.deleteAllOutlets();
                    routeDao.deleteAllRoutes();
                    routeDao.deleteAllAssets();
                    if(onDayStart) {
                        merchandiseDao.deleteAllMerchandise();
                        customerDao.deleteAllCustomerInput();
                    }
                    routeDao.insertRoutes(response.body().getRouteList());
                    routeDao.insertOutlets(response.body().getOutletList());
                    routeDao.insertAssets(response.body().getAssetList());

                }
                else{
                msg.postValue(Constant.GENERIC_ERROR);
                }

            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG,e.getMessage());
            }


        });

        executor.execute(() -> {
            try {
                Response<PackageProductResponseModel> response = webService.loadTodayPackageProduct().execute();
                if(response.isSuccessful()){
                    if(onDayStart) {
                        orderDao.deleteAllOrders();
                    }

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
                Log.e(TAG,e.getMessage());
                msg.postValue(Constant.GENERIC_ERROR);
            }finally {
                isLoading.postValue(false);
            }


        });


    }

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
                            if(isStart)
                            fetchTodayData(isStart);
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


    public MutableLiveData<Boolean> mLoading() {
        return isLoading;
    }

    public LiveData<Boolean> startDay(){
        return onDayStartLiveData;
    }

    public MutableLiveData<String> getError() {
        return msg;
    }
}
