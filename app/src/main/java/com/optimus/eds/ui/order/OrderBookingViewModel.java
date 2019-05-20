package com.optimus.eds.ui.order;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.optimus.eds.Constant;
import com.optimus.eds.db.entities.Order;
import com.optimus.eds.db.entities.OrderDetail;
import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.db.entities.Package;
import com.optimus.eds.db.entities.Product;
import com.optimus.eds.db.entities.ProductGroup;
import com.optimus.eds.model.OrderModel;
import com.optimus.eds.model.OrderResponseModel;
import com.optimus.eds.model.PackageModel;
import com.optimus.eds.source.API;
import com.optimus.eds.source.RetrofitHelper;
import com.optimus.eds.ui.route.outlet.detail.OutletDetailRepository;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.reactivex.Completable;

import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableObserver;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;


public class OrderBookingViewModel extends AndroidViewModel {

    private CompositeDisposable disposable;
    private OrderBookingRepository repository;
    private OutletDetailRepository outletDetailRepository;
    private MutableLiveData<List<PackageModel>> mutablePkgList;
    private LiveData<List<ProductGroup>> productGroupList;
    private MutableLiveData<Boolean> isSaving;
    private MutableLiveData<String> msg;
    private MutableLiveData<Boolean> orderSaved;
    private LiveData<List<Package>> packages;


    private Long outletId;
    private OrderModel order =null;
    private API webservice;


    public OrderBookingViewModel(@NonNull Application application) {
        super(application);
        disposable = new CompositeDisposable();
        webservice = RetrofitHelper.getInstance().getApi();
        repository = OrderBookingRepository.singleInstance(application, webservice,Executors.newSingleThreadExecutor());
        outletDetailRepository = new OutletDetailRepository(application);
        mutablePkgList = new MutableLiveData<>();
        msg = new MutableLiveData<>();
        isSaving = new MutableLiveData<>();
        orderSaved = new MutableLiveData<>();

        onScreenCreated();
    }

    private void onScreenCreated(){
        isSaving.setValue(true);
        productGroupList = repository.findAllGroups();
        packages = repository.findAllPackages();

    }

    public void setOrder(OrderModel order) {
        this.order = order;
    }

    public void setOutletId(Long outletId) {
        this.outletId = outletId;
        findOrder(outletId);
    }

    public void findOrder(Long outletId){
        Maybe<OrderModel> orderSingle = repository.findOrder(outletId);
        Disposable orderDisposable = orderSingle.observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io()).subscribe(this::setOrder,this::onError);
        disposable.add(orderDisposable);
    }


    public void filterProductsByGroup(Long groupId){
        Single<List<Product>> allProductsByGroup = repository.findAllProductsByGroup(groupId);
        if(order==null)
        {
            disposable.add(allProductsByGroup.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onSuccess));
            return;
        }

        Single<List<OrderDetail>> allAddedProducts = repository.getOrderItems(order.getOrder().getLocalOrderId());
        Single<List<Product>> zippedSingleSource = Single.zip(allProductsByGroup, allAddedProducts, this::updatedProducts);

        Disposable homeDisposable = zippedSingleSource
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::onSuccess, this::onError);
        disposable.add(homeDisposable);


    }


    private List<Product> updatedProducts(List<Product> filteredProducts, List<OrderDetail> addedProducts){

        for(Product product:filteredProducts){
            for(OrderDetail orderDetail:addedProducts){
                if(product.getId()==orderDetail.getProductId()) {
                    product.setQty(orderDetail.getCartonQuantity(), orderDetail.getUnitQuantity());

                }
            }
        }


        return filteredProducts;
    }


    public void addOrder(List<Product> orderItems,Long groupId){
        Completable.create(e -> {
            if(order==null) {
                Order order = new Order(outletId);
                repository.createOrder(order);
            }else{
                repository.deleteOrderItems(order.getOrder().getLocalOrderId(),groupId);
            }
            e.onComplete();
        }).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {
                disposable.add(d);
            }

            @Override
            public void onComplete() {
                addOrderItems(orderItems);
            }

            @Override
            public void onError(Throwable e) {
                msg.postValue(e.getMessage());
            }
        });


    }

    public void addOrderItems(List<Product> orderItems){


        repository.findOrder(outletId).map(orderModel -> modifyOrderDetails(orderModel,orderItems))
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(this::onAddOrderSuccess,this::onError);
    }

    public void updateOrder(OrderModel order) {
        if(order==null) return;
        setOrder(order);
        Completable.create(e -> {
            repository.updateOrder(order.getOrder());
            e.onComplete();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onComplete() {
                        updateOrderWithPricingItems(order.getOrderDetails());
                    }

                    @Override
                    public void onError(Throwable e) {
                        msg.postValue(e.getMessage());
                    }
                });

    }

    public void updateOrderWithPricingItems(List<OrderDetail> orderItems){
        Completable.create(e -> {
            repository.updateOrderItems(orderItems);
            e.onComplete();
        }).observeOn(Schedulers.io()).subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {
                disposable.add(d);
            }

            @Override
            public void onComplete() {
                orderSaved.postValue(true);
                isSaving.postValue(false);
                msg.postValue("Order Saved Successfully");
            }

            @Override
            public void onError(Throwable e) {
                orderSaved.postValue(false);
                msg.postValue(e.getMessage());
            }
        });

    }

    private void onAddOrderSuccess(OrderModel orderModel) {
        setOrder(orderModel);
    }

    private void onSuccess(List<Product> products) {
        mutablePkgList.postValue(repository.packageModel(packages.getValue(),products));
        isSaving.postValue(false);
    }

    private void onError(Throwable throwable) throws IOException {
        String errorBody = throwable.getMessage();
        if (throwable instanceof HttpException){
            HttpException error = (HttpException)throwable;
            errorBody = error.response().errorBody().string();
        }
        msg.postValue(errorBody);
        isSaving.postValue(false);

    }

    private OrderModel modifyOrderDetails(OrderModel order,List<Product> orderProducts) {
        List<OrderDetail> orderDetails = new ArrayList<>(orderProducts.size());
        for(Product product:orderProducts) {
            OrderDetail orderDetail = new OrderDetail(order.getOrder().getLocalOrderId(), product.getId(),product.getQtyCarton(),product.getQtyUnit());
            orderDetail.setCartonCode(product.getCartonCode());
            orderDetail.setUnitCode(product.getUnitCode());
            orderDetail.setProductName(product.getName());
            orderDetail.setProductGroupId(product.getProductGroupId());
            orderDetail.setType(Constant.ProductType.PAID);
            orderDetails.add(orderDetail);
        }


        repository.addOrderItems(orderDetails);
        order.setOrderDetails(orderDetails);
        return order;
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

    public void composeOrderForServer(){

        if(order!=null){
            isSaving.postValue(true);
            Order mOrder = new Order(order.getOrder().getOutletId());
            mOrder.setRouteId(order.getOutlet().getRouteId());
            mOrder.setVisitDayId(order.getOutlet().getVisitDay());
            mOrder.setOrderStatus(2);
            mOrder.setLocalOrderId(order.getOrder().getLocalOrderId());
            mOrder.setLatitude(order.getOutlet().getLatitude());
            mOrder.setLongitude(order.getOutlet().getLongitude());
            order.setOrder(mOrder);

            Gson gson  = new Gson();
            String json = gson.toJson(mOrder);
            OrderResponseModel responseModel = gson.fromJson(json,OrderResponseModel.class);
            responseModel.setOrderDetails(order.getOrderDetails());

            disposable
                    .add(webservice.saveOrder(responseModel).map(orderResponseModel -> {

                        OrderModel orderModel = new OrderModel();
                        String orderString = new Gson().toJson(orderResponseModel);
                        Order order = new Gson().fromJson(orderString,Order.class);
                        orderModel.setOrderDetails(orderResponseModel.getOrderDetails());
                        orderModel.setOrder(order);
                        orderModel.setOutlet(this.order.getOutlet());
                        return orderModel;
                    }).observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe(this::updateOrder,this::onError));
        }


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

    public LiveData<Boolean> isSaving() {
        return isSaving;
    }

    public LiveData<String> showMessage(){
        return msg;
    }

    public LiveData<Boolean> orderSaved(){
        return orderSaved;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();

    }
}
