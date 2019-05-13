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
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CashMemoViewModel extends AndroidViewModel {

    public MutableLiveData<List<OrderDetail>> cartProducts;
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


    protected void getOrder(Long outletId){

        orderDao.findOrderByOutletId(outletId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new SingleObserver<Order>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(Order order) {
            getOrderItems(order.getOrderId());
            }

            @Override
            public void onError(Throwable e) {

            }
        });
    }

    protected LiveData<List<OrderDetail>> getOrderItems(Long orderId){
        MutableLiveData<List<OrderDetail>> orderItemLiveData = new MutableLiveData<>();
     /*   AsyncTask.execute(() -> {
           List<OrderDetail> orderItems = orderDao.findOrderItemsByOrderId(orderId);
           orderItemLiveData.postValue(orderItems);
            cartProducts.postValue(orderItems);
        });*/
        return orderItemLiveData;
    }


    LiveData<List<OrderDetail>> getCartProducts(){
        return cartProducts;
    }
}
