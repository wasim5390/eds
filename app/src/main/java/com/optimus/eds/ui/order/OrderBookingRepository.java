package com.optimus.eds.ui.order;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.optimus.eds.db.AppDatabase;
import com.optimus.eds.db.dao.OrderDao;
import com.optimus.eds.db.dao.ProductsDao;
import com.optimus.eds.db.dao.RouteDao;
import com.optimus.eds.db.entities.Order;
import com.optimus.eds.db.entities.OrderDetail;
import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.db.entities.Package;
import com.optimus.eds.db.entities.Product;
import com.optimus.eds.db.entities.ProductGroup;
import com.optimus.eds.model.PackageModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class OrderBookingRepository {

    private OrderDao orderDao;
    private ProductsDao productsDao;
    private RouteDao routeDao;
    private MutableLiveData<List<ProductGroup>> allGroups;
    private MutableLiveData<List<Product>> allProducts;


    public LiveData<Boolean> isSaving() {
        return isSaving;
    }

    private MutableLiveData<Boolean> isSaving;

    public OrderBookingRepository(Application application) {
        AppDatabase appDatabase = AppDatabase.getDatabase(application);
        productsDao = appDatabase.productsDao();
        routeDao= appDatabase.routeDao();
        orderDao = appDatabase.orderDao();
        isSaving = new MutableLiveData<>();
        allGroups = new MutableLiveData<>();
        allProducts = new MutableLiveData<>();
    }


    public void addOrder(Order order){

        Completable.create(e -> {
            orderDao.insertOrder(order);
            e.onComplete();
        }).subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onComplete() {
              //  isSaving.postValue(true);
            }

            @Override
            public void onError(Throwable e) {
                isSaving.postValue(false);
            }
        });

    }

    public void addOrderItems(List<OrderDetail> orderDetail){

        Completable.create(e -> {
            orderDao.insertOrderItems(orderDetail);
            e.onComplete();
        }).subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onComplete() {
                isSaving.postValue(true);
            }

            @Override
            public void onError(Throwable e) {
                isSaving.postValue(false);
            }
        });

    }


    protected LiveData<Order> findOrder(Long outletId){
        MutableLiveData<Order> order = new MutableLiveData<>();
        AsyncTask.execute(() -> {
            Order ordr = orderDao.findOrderByOutletId(outletId);
            order.postValue(ordr);
        });
        return  order;
    }


    protected LiveData<List<ProductGroup>> findAllGroups(){
        AsyncTask.execute(() -> allGroups.postValue(productsDao.findAllProductGroups()));
        return allGroups;
    }


    protected LiveData<List<Package>> findAllPackages(){
        MutableLiveData<List<Package>> packages = new MutableLiveData<>();
        AsyncTask.execute(() -> {
           List<Package> packageList= productsDao.findAllPackages();
           packages.postValue(packageList);
        });
       return packages;
    }


    protected LiveData<List<Product>> findAllProducts(Long groupId){
        AsyncTask.execute(() -> allProducts.postValue(productsDao.findAllProductsByGroupId(groupId)));
        return allProducts;
    }


    protected List<PackageModel> packageModel(List<Package> packages, List<Product> _products) {
        List<PackageModel> packageModels = new ArrayList<>(packages.size());

        for(Package _package: packages)
        {
            List<Product> products = getProductsById(_package.getPackageId(),_products);
            if(!products.isEmpty()) {
                PackageModel model = new PackageModel(_package.getPackageId(), _package.getPackageName(), products);
                packageModels.add(model);
            }
        }
        return packageModels;

    }

    protected List<Product> getProductsById(Long packageId,List<Product> products){
        List<Product> filteredList = new ArrayList<>();
        for(Product product:products){
            if(product.getPkgId()==packageId)
                filteredList.add(product);
        }

        return filteredList;
    }

}
