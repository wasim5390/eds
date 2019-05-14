package com.optimus.eds.ui.order;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.optimus.eds.Constant;
import com.optimus.eds.db.entities.Order;
import com.optimus.eds.db.entities.OrderDetail;
import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.db.entities.Package;
import com.optimus.eds.db.entities.Product;
import com.optimus.eds.db.entities.ProductGroup;
import com.optimus.eds.model.OrderModel;
import com.optimus.eds.model.PackageModel;
import com.optimus.eds.source.API;
import com.optimus.eds.source.RetrofitHelper;
import com.optimus.eds.ui.route.outlet.detail.OutletDetailRepository;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    private MutableLiveData<String> msg;
    private LiveData<List<Package>> packages;

    private Long outletId;
    private OrderModel order =null;
    private API webservice;


    public OrderBookingViewModel(@NonNull Application application) {
        super(application);
        disposable = new CompositeDisposable();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        webservice = RetrofitHelper.getInstance().getApi();
        repository = OrderBookingRepository.singleInstance(application, webservice,executor);
        outletDetailRepository = new OutletDetailRepository(application);
        mutablePkgList = new MutableLiveData<>();
        msg = new MutableLiveData<>();
        isSaving = repository.isSaving();
        onScreenCreated();
    }


    public void setOrder(OrderModel order) {
        this.order = order;
    }

    public void setOutletId(Long outletId) {
        this.outletId = outletId;
        Single<OrderModel> orderSingle = repository.findOrder(outletId);
        Disposable orderDisposable = orderSingle.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribe(this::setOrder,this::onError);
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
            disposable.add(allProductsByGroup.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onSuccess));
            return;
        }

        Single<List<OrderDetail>> allAddedProducts = repository.getOrderItems(order.getOrder().getLocalOrderId());
        Single<List<Product>> zippedSingleSource = Single.zip(allProductsByGroup, allAddedProducts, this::updatedProductListing);

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
    msg.postValue(throwable.getMessage());

    }


    private List<Product> updatedProductListing(List<Product> filteredProducts, List<OrderDetail> addedProducts){

        for(Product product:filteredProducts){
            for(OrderDetail orderDetail:addedProducts){
                if(product.getId()==orderDetail.getProductId()) {
                    product.setQty(orderDetail.getCartonQuantity(), orderDetail.getUnitQuantity());

                }
            }
        }


        return filteredProducts;
    }






    public void addOrderProducts(List<Product> orderItems){

        Completable.create(e -> {
            if(order==null) {
                Order order = new Order(outletId);
                order.setOrderStatus(0);
                repository.createOrder(order);
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

            }
        });


    }

    public void addOrderItems(List<Product> orderItems){
        List<OrderDetail> orderDetails = new ArrayList<>(orderItems.size());
        repository.findOrder(outletId)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new SingleObserver<OrderModel>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(OrderModel order) {

                        for(Product product:orderItems) {
                            OrderDetail orderDetail = new OrderDetail(order.getOrder().getLocalOrderId(), product.getId(),product.getQtyCarton(),product.getQtyUnit());
                            orderDetail.setCartonCode(product.getCartonCode());
                            orderDetail.setUnitCode(product.getUnitCode());
                            orderDetail.setProductName(product.getName());
                            orderDetail.setType(Constant.ProductType.PAID);
                            orderDetails.add(orderDetail);
                        }
                        repository.addOrderItems(orderDetails);
                        order.setOrderDetails(orderDetails);
                        setOrder(order);
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

    public void composeOrderForServer(){
        if(order!=null){
            Order mOrder = new Order(order.getOrder().getOutletId());
            mOrder.setRouteId(order.getOutlet().getRouteId());
            mOrder.setVisitDayId(order.getOutlet().getVisitDay());
            mOrder.setOrderStatus(2);
            mOrder.setLocalOrderId(order.getOrder().getLocalOrderId());
            mOrder.setLatitude(order.getOutlet().getLatitude());
            mOrder.setLongitude(order.getOutlet().getLongitude());
            order.setOrder(mOrder);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            order.setOutlet(null);
            String json = gson.toJson(order);
            Log.i("JSON: ",json);

        }
        disposable
                .add(webservice.calculatePricing(order).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe(this::setOrder,this::onError));

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


    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}
