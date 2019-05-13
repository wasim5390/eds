package com.optimus.eds.ui.order;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;


import com.optimus.eds.db.entities.Order;
import com.optimus.eds.db.entities.OrderDetail;
import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.db.entities.Package;
import com.optimus.eds.db.entities.Product;
import com.optimus.eds.db.entities.ProductGroup;
import com.optimus.eds.model.OrderModel;
import com.optimus.eds.model.PackageModel;
import com.optimus.eds.ui.route.outlet.detail.OutletDetailRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.reactivex.Completable;

import io.reactivex.CompletableObserver;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class OrderBookingViewModel extends AndroidViewModel {

    private CompositeDisposable disposable;
    private OrderBookingRepository repository;
    private OutletDetailRepository outletDetailRepository;
    private MutableLiveData<List<PackageModel>> mutablePkgList;
    private LiveData<List<ProductGroup>> productGroupList;
    private LiveData<Boolean> isSaving;
    private LiveData<List<Package>> packages;
    private MutableLiveData<Order> orderLiveData;
    private Long outletId;

    OrderModel order =null;



    public OrderBookingViewModel(@NonNull Application application) {
        super(application);
        disposable = new CompositeDisposable();
        repository = new OrderBookingRepository(application);
        outletDetailRepository = new OutletDetailRepository(application);
        mutablePkgList = new MutableLiveData<>();
        orderLiveData = new MutableLiveData<>();
        isSaving = repository.isSaving();
        onScreenCreated();
    }


    public void setOrder(OrderModel order) {
        this.order = order;
    }

    public void setOutletId(Long outletId) {
        this.outletId = outletId;
        Single<OrderModel> orderSingle = repository.findOrder(outletId);
        Disposable orderDisposable = orderSingle.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(this::setOrder,this::onError);
        disposable.add(orderDisposable);
    }

    private void onScreenCreated(){
        productGroupList = repository.findAllGroups();
        packages = repository.findAllPackages();


    }

    public void filterProductsByGroup(Long groupId){
        Single<List<Product>> allProductsByGroup = repository.findAllProducts(groupId);
        if(order==null)
        {
            allProductsByGroup.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onSuccess);
            return;
        }

        Single<List<OrderDetail>> allAddedProducts = repository.getOrderItems(order.getOrder().getOrderId());
        Single<List<Product>> zippedSingleSource = Single.zip(allProductsByGroup, allAddedProducts, this::updatedOrder);

        Disposable homeDisposable = zippedSingleSource
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::onSuccess, this::onError);
        disposable.add(homeDisposable);


    }

    private void onSuccess(List<Product> products) {
        mutablePkgList.postValue(repository.packageModel(packages.getValue(),products));
    }

    private void onError(Throwable throwable) {

    }


    private List<Product> updatedOrder(List<Product> filteredProducts, List<OrderDetail> addedProducts){

        for(Product product:filteredProducts){
            for(OrderDetail orderDetail:addedProducts){
                if(product.getId()==orderDetail.getProductId()) {
                    product.setQty(orderDetail.getCartonQuantity(), orderDetail.getUnitQuantity());

                }
            }
        }


        return filteredProducts;
    }


    public LiveData<List<PackageModel>> getProductList() {
        return mutablePkgList;
    }

    public LiveData<List<ProductGroup>> getProductGroupList() {
        return productGroupList;
    }

    public LiveData<Outlet> loadOutlet(Long outletId) {
        return outletDetailRepository.getOutletById(outletId);
    }



    public void addOrderProducts(List<Product> orderItems){
        List<OrderDetail> orderDetails = new ArrayList<>(orderItems.size());


        Completable.create(e -> {
            if(order==null) {
               Order order = new Order(outletId);
                order.setOrderStatus(0);
                repository.createOrder(order);
            }
            e.onComplete();
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onComplete() {
                repository.findOrder(outletId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()).
                        subscribe(new SingleObserver<OrderModel>() {

                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onSuccess(OrderModel order) {
                                orderLiveData.postValue(order.getOrder());
                                for(Product product:orderItems) {
                                    OrderDetail orderDetail = new OrderDetail(order.getOrder().getOrderId(), product.getId(),product.getQtyCarton(),product.getQtyUnit());
                                    orderDetails.add(orderDetail);
                                }
                                repository.addOrderItems(orderDetails);
                            }

                            @Override
                            public void onError(Throwable e) {

                            }
                        });


            }

            @Override
            public void onError(Throwable e) {

            }
        });


    }



    protected List<Product> filterOrderProducts(Map<String,Section> sectionHashMap){
        List<Product> productList = new ArrayList<>();

        for (Map.Entry<String, Section> entry : sectionHashMap.entrySet()) {
            PackageSection section =(PackageSection) entry.getValue();
            List<Product> products = section.getList();
            for(Product product:products){
                if(product.isProductSelected()) {
                    productList.add(product);
                }
            }

        }

        return productList;
    }

    public LiveData<Boolean> isSaving() {
        return isSaving;
    }

    public LiveData<Order> getOrder(){
        return orderLiveData;
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}
