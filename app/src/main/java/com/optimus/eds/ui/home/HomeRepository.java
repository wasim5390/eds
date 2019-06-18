package com.optimus.eds.ui.home;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import android.util.Log;

import com.optimus.eds.db.AppDatabase;
import com.optimus.eds.db.dao.CustomerDao;
import com.optimus.eds.db.dao.MerchandiseDao;
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

import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class HomeRepository {

    private final String TAG=HomeRepository.class.getSimpleName();
    private static HomeRepository repository;
    private PreferenceUtil preferenceUtil;
    private OrderDao orderDao;
    private CustomerDao customerDao;
    MerchandiseDao merchandiseDao;
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
        merchandiseDao = appDatabase.merchandiseDao();
        customerDao = appDatabase.customerDao();
        isLoading = new MutableLiveData<>();
        webService = api;
        this.executor = executor;

    }

    public void getToken(){
        isLoading.setValue(true);
        webService.getToken("password","imran","imranshabrati").observeOn(Schedulers.io()).subscribeOn(Schedulers.io()).subscribeWith(new DisposableSingleObserver<TokenResponse>() {
            @Override
            public void onSuccess(TokenResponse tokenResponse) {
                preferenceUtil.saveToken(tokenResponse.getAccessToken());
                fetchTodayData();
            }

            @Override
            public void onError(Throwable e) {
                isLoading.postValue(false);
            }
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
                    routeDao.deleteAllAssets();
                    merchandiseDao.deleteAllMerchandise();
                    customerDao.deleteAllCustomerInput();
                    routeDao.insertRoutes(response.body().getRouteList());
                    routeDao.insertOutlets(response.body().getOutletList());
                    routeDao.insertAssets(response.body().getAssetList());

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
