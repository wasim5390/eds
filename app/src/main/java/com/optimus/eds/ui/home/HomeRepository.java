package com.optimus.eds.ui.home;

import android.app.Application;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import android.util.Log;

import com.optimus.eds.db.AppDatabase;
import com.optimus.eds.db.dao.OrderDao;
import com.optimus.eds.db.dao.ProductsDao;
import com.optimus.eds.db.dao.RouteDao;

import com.optimus.eds.model.PackageProductResponseModel;
import com.optimus.eds.model.RouteOutletResponseModel;
import com.optimus.eds.source.API;
import com.optimus.eds.source.TokenResponse;
import com.optimus.eds.utils.PreferenceUtil;


import java.io.IOException;
import java.util.concurrent.Executor;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeRepository {

    private final String TAG=HomeRepository.class.getSimpleName();
    private static HomeRepository repository;
    private PreferenceUtil preferenceUtil;
    private OrderDao orderDao;
    private ProductsDao productsDao;
    private RouteDao routeDao;


    private MutableLiveData<Boolean> isLoading;
    private API webService;
    private Executor executor;

    public static HomeRepository singleInstance(Application application, API api, Executor executor){
        if(repository==null)
            repository = new HomeRepository(application,api,executor);
        return repository;
    }

    public HomeRepository(Application application,API api,Executor executor) {
        AppDatabase appDatabase = AppDatabase.getDatabase(application);
        preferenceUtil = PreferenceUtil.getInstance(application);
        productsDao = appDatabase.productsDao();
        orderDao = appDatabase.orderDao();
        routeDao = appDatabase.routeDao();
        isLoading = new MutableLiveData<>();
        webService = api;
        this.executor = executor;

    }

    public void getToken(){
        isLoading.setValue(true);
        executor.execute(()->{

            webService.getToken("password","imran","imranshabrati").enqueue(new Callback<TokenResponse>() {
                @Override
                public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                    if(response.isSuccessful()) {
                        preferenceUtil.saveToken(response.body().getAccessToken());
                        fetchTodayData();
                    }
                }

                @Override
                public void onFailure(Call<TokenResponse> call, Throwable t) {

                }
            });
        });
    }

    public void fetchTodayData(){
        //@TODO this needs to be done
        // boolean ifDataAlreadyExist = dao.hasRefreshedData();
        executor.execute(() -> {
            try {
                Response<RouteOutletResponseModel> response = webService.loadTodayRouteOutlets().execute();
                if(response.isSuccessful()){
                    routeDao.deleteAllRoutes();
                    routeDao.deleteAllOutlets();
                    routeDao.insertRoutes(response.body().getRouteList());
                    routeDao.insertOutlets(response.body().getOutletList());
                }
                else{

                }

            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG,e.getMessage());
            }


        });

        executor.execute(() -> {
            //@TODO this needs to be done
            // boolean ifDataAlreadyExist = dao.hasRefreshedData();
            try {
                Response<PackageProductResponseModel> response = webService.loadTodayPackageProduct().execute();
                if(response.isSuccessful()){
                    orderDao.deleteAllOrders();

                    productsDao.deleteAllPackages();
                    productsDao.deleteAllProductGroups();
                    productsDao.deleteAllProducts();
                    productsDao.insertProductGroups(response.body().getProductGroups());
                    productsDao.insertPackages(response.body().getPackageList());
                    productsDao.insertProducts(response.body().getProductList());
                }
                else{

                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG,e.getMessage());
            }finally {
                isLoading.postValue(false);
            }


        });


    }

    public LiveData<Boolean> mLoading() {
        return isLoading;
    }
}
