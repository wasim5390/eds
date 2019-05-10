package com.optimus.eds.ui.order;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.Transformations;
import android.arch.persistence.room.PrimaryKey;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.optimus.eds.db.entities.Order;
import com.optimus.eds.db.entities.OrderDetail;
import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.db.entities.Package;
import com.optimus.eds.db.entities.Product;
import com.optimus.eds.db.entities.ProductGroup;
import com.optimus.eds.model.PackageModel;
import com.optimus.eds.ui.route.outlet.detail.OutletDetailRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;



public class OrderBookingViewModel extends AndroidViewModel {


    private OrderBookingRepository repository;
    private OutletDetailRepository outletDetailRepository;
    private MutableLiveData<List<PackageModel>> mutablePkgList;
    private LiveData<List<ProductGroup>> productGroupList;
    private LiveData<Boolean> isSaving;
    private LiveData<List<Package>> packages;
    private List<Product> orderProducts;
    private Order order;
    private LiveData<Order> orderLiveData;
    private Long outletId;
    private MutableLiveData<Outlet> outlet;


    public OrderBookingViewModel(@NonNull Application application) {
        super(application);

        repository = new OrderBookingRepository(application);
        outletDetailRepository = new OutletDetailRepository(application);
        mutablePkgList = new MutableLiveData<>();
        outlet = new MutableLiveData<>();
        orderProducts = new ArrayList<>();
        isSaving = repository.isSaving();
        onScreenCreated();
    }

    public void setOutletId(Long outletId) {
        this.outletId = outletId;
        orderLiveData = repository.findOrder(outletId);

    }

    private void onScreenCreated(){
        productGroupList = repository.findAllGroups();
        packages = repository.findAllPackages();

    }

    public void filterProductsByGroup(Long groupId){

        repository.findAllProducts(groupId).observeForever(products1 -> {
            mutablePkgList.postValue(repository.packageModel(packages.getValue(),products1));
        });

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
        Order order  = orderLiveData.getValue();
        if(order==null) {
            order = new Order(outletId);
            order.setOrderStatus(0);
            repository.addOrder(order);

        }
        List<OrderDetail> orderDetails = new ArrayList<>(orderItems.size());
        for(Product product:orderItems) {
            OrderDetail orderDetail = new OrderDetail(order.getOrderId(), product.getId(),product.getQtyCarton(),product.getQtyUnit());
            orderDetails.add(orderDetail);
        }

        repository.addOrderItems(orderDetails);
/*        order = orderLiveData.getValue();
        if(order==null) {
            orderProducts.addAll(orderItems);
            order = new Order(outletId, orderProducts);
            repository.addOrder(order);
        }else{
            order.getProducts().addAll(orderItems);
            repository.updateOrder(order);
        }*/

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

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
