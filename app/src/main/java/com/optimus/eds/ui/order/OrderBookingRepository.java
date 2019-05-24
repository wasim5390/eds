package com.optimus.eds.ui.order;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import com.optimus.eds.db.AppDatabase;
import com.optimus.eds.db.dao.OrderDao;
import com.optimus.eds.db.dao.ProductsDao;
import com.optimus.eds.db.dao.RouteDao;
import com.optimus.eds.db.entities.Order;
import com.optimus.eds.db.entities.OrderDetail;

import com.optimus.eds.db.entities.Package;
import com.optimus.eds.db.entities.Product;
import com.optimus.eds.db.entities.ProductGroup;
import com.optimus.eds.model.OrderModel;
import com.optimus.eds.model.PackageModel;
import com.optimus.eds.source.API;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class OrderBookingRepository {
    private final String TAG=OrderBookingRepository.class.getSimpleName();
    private static OrderBookingRepository repository;
    private OrderDao orderDao;
    private ProductsDao productsDao;
    private MutableLiveData<List<ProductGroup>> allGroups;

    private API webService;
    private Executor executor;

    public static OrderBookingRepository singleInstance(Application application, API api, Executor executor){
        if(repository==null)
            repository = new OrderBookingRepository(application,api,executor);
        return repository;
    }

    public OrderBookingRepository(Application application, API api, Executor executor) {
        webService = api;
        this.executor = executor;
        AppDatabase appDatabase = AppDatabase.getDatabase(application);
        productsDao = appDatabase.productsDao();
        orderDao = appDatabase.orderDao();
        allGroups = new MutableLiveData<>();
    }


    public void createOrder(Order order){
            orderDao.insertOrder(order);
    }

    public void addOrderItems(List<OrderDetail> orderDetail){
        orderDao.insertOrderItems(orderDetail);
    }

    public void updateOrderItems(List<OrderDetail> orderDetails){
        orderDao.updateOrderItems(orderDetails);
    }

    public void deleteOrderItems(Long orderId,Long groupId){
        orderDao.deleteOrderItems(orderId,groupId);
    }

    public void deleteOrder(Long orderId){
        orderDao.deleteOrder(orderId);
    }


    protected Maybe<OrderModel> findOrder(Long outletId){
        return orderDao.getOrderWithItems(outletId);
    }

    public void updateOrder(Order order){
        orderDao.updateOrder(order);
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


    protected Single<List<Product>> findAllProductsByGroup(Long groupId){
        return productsDao.findAllProductsByGroupId(groupId);

    }


    protected List<PackageModel> packageModel(List<Package> packages, List<Product> _products) {
        List<PackageModel> packageModels = new ArrayList<>(packages.size());

        for(Package _package: packages)
        {
            List<Product> products = getProductsByPkgId(_package.getPackageId(),_products);
            if(!products.isEmpty()) {
                PackageModel model = new PackageModel(_package.getPackageId(), _package.getPackageName(), products);
                packageModels.add(model);
            }
        }
        return packageModels;

    }

    protected List<Product> getProductsByPkgId(Long packageId,List<Product> products){
        List<Product> filteredList = new ArrayList<>();
        for(Product product:products){
            if(product.getPkgId()==packageId)
                filteredList.add(product);
        }

        return filteredList;
    }

    protected Single<List<OrderDetail>> getOrderItems(Long orderId){
        return orderDao.findOrderItemsByOrderId(orderId);
    }


}
