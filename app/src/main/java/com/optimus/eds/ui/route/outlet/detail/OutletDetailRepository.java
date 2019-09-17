package com.optimus.eds.ui.route.outlet.detail;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.optimus.eds.db.AppDatabase;

import com.optimus.eds.db.dao.ProductsDao;
import com.optimus.eds.db.dao.RouteDao;
import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.model.PackageProductResponseModel;
import com.optimus.eds.source.API;
import com.optimus.eds.source.RetrofitHelper;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.lifecycle.MutableLiveData;
import io.reactivex.Single;
import retrofit2.Response;


public class OutletDetailRepository {
    private final  static  String TAG = OutletDetailRepository.class.getName();
    private final RouteDao routeDao;
    private final ProductsDao productsDao;

    private final API webservice;

    public OutletDetailRepository(Application application) {
        AppDatabase appDatabase = AppDatabase.getDatabase(application);
        webservice = RetrofitHelper.getInstance().getApi();
        routeDao = appDatabase.routeDao();
        productsDao = appDatabase.productsDao();
    }


    public LiveData<Outlet> getOutletById(Long outletId){
        return routeDao.findOutletById(outletId);
    }

    public void  updateOutletVisitStatus(Long outletId, Integer visitStatus,int synced){
        AsyncTask.execute(() -> routeDao.updateOutletVisitStatus(outletId,visitStatus,synced));

    }

    public void updateOutlet(Outlet outlet){
        AsyncTask.execute(() -> routeDao.updateOutlet(outlet));
    }



    public LiveData<Boolean> loadProductsFromServer(){

        Executor executor =Executors.newSingleThreadExecutor();
        MutableLiveData<Boolean> loaded = new MutableLiveData<>();
        executor.execute(() -> {
        try {

            Response<PackageProductResponseModel> response = webservice.loadTodayPackageProduct().execute();
            if(response.isSuccessful()){

                productsDao.deleteAllProducts();
                productsDao.deleteAllProductGroups();
                productsDao.insertProducts(response.body().getProductList());
                productsDao.insertProductGroups(response.body().getProductGroups());

            }

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG,e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG,e.getMessage());
        }
        finally {
            loaded.postValue(true);
        }

        });
        return loaded;
    }



}
