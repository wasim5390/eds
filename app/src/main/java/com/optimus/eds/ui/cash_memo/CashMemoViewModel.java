package com.optimus.eds.ui.cash_memo;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.optimus.eds.db.AppDatabase;
import com.optimus.eds.db.dao.OrderDao;
import com.optimus.eds.db.dao.ProductsDao;
import com.optimus.eds.db.entities.Order;
import com.optimus.eds.db.entities.OrderDetail;
import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.db.entities.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;

public class CashMemoViewModel extends AndroidViewModel {

    public MutableLiveData<List<Product>> cartProducts;
    private MutableLiveData<HashMap<Order,OrderDetail>> order;
    private List<Order> allOrders;

    ProductsDao dao;
    OrderDao orderDao;
    private AppDatabase database;

    public CashMemoViewModel(@NonNull Application application) {
        super(application);
        cartProducts = new MutableLiveData<>();
        order = new MutableLiveData<>();
        database = AppDatabase.getDatabase(application);
        dao = database.productsDao();
        orderDao = database.orderDao();

    }


    protected LiveData<Order> getOrder(Long outletId){
        MutableLiveData<Order> order = new MutableLiveData<>();

        AsyncTask.execute(() -> {
            Order ordr = orderDao.findOrderByOutletId(outletId);
            order.postValue(ordr);
        });
        return  order;
    }

    protected LiveData<List<OrderDetail>> getOrderItems(Long orderId){
        MutableLiveData<List<OrderDetail>> orderItemLiveData = new MutableLiveData<>();
        AsyncTask.execute(() -> {
           List<OrderDetail> orderItems = orderDao.findOrderItemsByOrderId(orderId);
           orderItemLiveData.postValue(orderItems);
        });
        return orderItemLiveData;
    }


    LiveData<List<Product>> getCartProducts(){
        return cartProducts;
    }
}
