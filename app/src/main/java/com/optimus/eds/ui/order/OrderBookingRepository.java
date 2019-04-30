package com.optimus.eds.ui.order;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;

import com.optimus.eds.db.AppDatabase;
import com.optimus.eds.db.dao.OrderDao;
import com.optimus.eds.db.dao.ProductsDao;
import com.optimus.eds.db.entities.Order;
import com.optimus.eds.db.entities.Package;
import com.optimus.eds.db.entities.Product;
import com.optimus.eds.model.PackageModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class OrderBookingRepository {

    private OrderDao orderDao;
    private ProductsDao productsDao;
    private MutableLiveData<List<Package>> packages;
    private MutableLiveData<List<Product>> allProducts;
    private MutableLiveData<Boolean> isSaving;

    public OrderBookingRepository(Application application) {
        AppDatabase appDatabase = AppDatabase.getDatabase(application);
        productsDao = appDatabase.productsDao();
        orderDao = appDatabase.orderDao();
        isSaving = new MutableLiveData<>();
        packages = new MutableLiveData<>();
        allProducts = new MutableLiveData<>();
    }

    public LiveData<Boolean> addOrder(Order order){

        Completable.create(e -> {
            orderDao.insertOrder(order);
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
        return isSaving;

    }



    protected LiveData<List<Package>> findAllPackages(){
        productsDao.findAllPackages().observeForever(packages1 -> {
            packages.postValue(packages1);
        });
        return packages;
    }

    protected LiveData<List<Product>> findAllProducts(){
        productsDao.findAllProduct().observeForever(products -> allProducts.postValue(products));
        return allProducts;
    }


    protected List<PackageModel> packageModel(List<Package> packages, List<Product> _products) {
        List<PackageModel> packageModels = new ArrayList<>(packages.size());

        for(Package _package: packages)
        {
            List<Product> products = getProductsById(_package.getPackageId(),_products);
            PackageModel model = new PackageModel(_package.getPackageId(),_package.getPackageName(),products);
            packageModels.add(model);
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
