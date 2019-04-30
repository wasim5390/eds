package com.optimus.eds.ui.order;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.optimus.eds.db.AppDatabase;
import com.optimus.eds.db.dao.OrderDao;
import com.optimus.eds.db.entities.Order;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class OrderBookingRepository {

    private OrderDao orderDao;
    private MutableLiveData<Boolean> isSaving;

    public OrderBookingRepository(Application application) {
        AppDatabase appDatabase = AppDatabase.getDatabase(application);
        orderDao = appDatabase.orderDao();
        isSaving = new MutableLiveData<>();
    }

    public LiveData<Boolean> addOrder(Order order){
        isSaving.setValue(true);
        Completable.create(e -> {
            orderDao.insertOrder(order);
            e.onComplete();
        }).subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onComplete() {
                isSaving.setValue(false);
            }

            @Override
            public void onError(Throwable e) {

            }
        });
        return isSaving;

    }

    public LiveData<Boolean> isSaving() {
        return isSaving;
    }

}
