package com.optimus.eds.ui.order;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.Transformations;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.optimus.eds.db.AppDatabase;
import com.optimus.eds.db.dao.OrderDao;
import com.optimus.eds.db.dao.ProductsDao;
import com.optimus.eds.db.entities.Order;
import com.optimus.eds.db.entities.Package;
import com.optimus.eds.db.entities.Product;
import com.optimus.eds.model.PackageModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableObserver;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.http.PATCH;


public class OrderBookingViewModel extends AndroidViewModel {

    private ProductsDao productsDao;
    private OrderBookingRepository repository;
    private MutableLiveData<List<PackageModel>> mutablePkgList;
    private MutableLiveData<Boolean> isSaving;
    private List<Package> packages;
    private List<Product> products;
    private CompositeDisposable compositeDisposable;

    public OrderBookingViewModel(@NonNull Application application) {
        super(application);
        compositeDisposable = new CompositeDisposable();
        mutablePkgList = new MutableLiveData<>();
        isSaving = new MutableLiveData<>();
        packages = new ArrayList<>();
        productsDao = AppDatabase.getDatabase(application).productsDao();
        repository = new OrderBookingRepository(application);
        onScreenCreated();
    }

    private void onScreenCreated(){


        repository.findAllPackages().observeForever(packages -> {
            this.packages = packages;
            repository.findAllProducts().observeForever(products -> {
                this.products = products;
                mutablePkgList.setValue(repository.packageModel(packages,products));
            });
        });


    }





    private void onError(Throwable throwable) {

    }



    private void onSuccess(List<PackageModel> packageModels) {
        mutablePkgList.setValue(packageModels);
    }




    public LiveData<List<PackageModel>> getProductList() {
        return mutablePkgList;
    }



    public void addOrder(Order order){
        repository.addOrder(order).observeForever(aBoolean -> {
            isSaving.setValue(aBoolean);
        });
    }

    protected List<Product> filterOrderProducts(Map<String,Section> sectionHashMap){
        List<Product> productList = new ArrayList<>();

        for (Map.Entry<String, Section> entry : sectionHashMap.entrySet()) {
            String key = entry.getKey();
            PackageSection section =(PackageSection) entry.getValue();
            List<Product> products = section.getList();
            for(Product product:products){
                if(product.getQty()>0)
                    productList.add(product);
            }

        }

        return productList;
    }

    public LiveData<Boolean> isSaving() {
        return isSaving;
    }
    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
