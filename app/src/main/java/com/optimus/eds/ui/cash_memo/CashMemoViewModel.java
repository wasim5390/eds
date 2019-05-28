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
import com.optimus.eds.model.OrderModel;
import com.optimus.eds.ui.route.outlet.detail.OutletDetailRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.MaybeObserver;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CashMemoViewModel extends AndroidViewModel {

    public MutableLiveData<List<OrderDetail>> savedProducts;
    private CashMemoRepository repository;
    private OutletDetailRepository outletDetailRepository;
    private MutableLiveData<OrderModel> orderLiveData;


    public CashMemoViewModel(@NonNull Application application) {
        super(application);
        savedProducts = new MutableLiveData<>();
        orderLiveData = new MutableLiveData<>();
        repository = new CashMemoRepository(application);
        outletDetailRepository = new OutletDetailRepository(application);

    }

    protected LiveData<OrderModel> getOrder(Long outletId){
        repository.findOrder(outletId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new MaybeObserver<OrderModel>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(OrderModel order) {
            orderLiveData.postValue(order);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
        return orderLiveData;
    }


    public LiveData<Outlet> loadOutlet(Long outletId) {
        return outletDetailRepository.getOutletById(outletId);
    }
    LiveData<OrderModel> getOrder(){
        return orderLiveData;
    }
}
