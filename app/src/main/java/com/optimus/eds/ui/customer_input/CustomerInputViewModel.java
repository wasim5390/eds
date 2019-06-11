package com.optimus.eds.ui.customer_input;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.provider.LiveFolders;
import android.support.annotation.NonNull;

import com.optimus.eds.db.entities.OrderDetail;
import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.model.OrderDetailAndPriceBreakdown;
import com.optimus.eds.model.OrderModel;
import com.optimus.eds.ui.order.OrderBookingRepository;
import com.optimus.eds.ui.route.outlet.detail.OutletDetailRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import io.reactivex.Maybe;
import io.reactivex.MaybeObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

public class CustomerInputViewModel extends AndroidViewModel {

    private final MutableLiveData<Boolean> isSaving;
    private final MutableLiveData<String> msg;
    private final MutableLiveData<Boolean> orderSaved;
    private final OrderBookingRepository orderRepository;
    private OutletDetailRepository outletDetailRepository;
    private final CompositeDisposable disposable;
    private Long outletId;
    private MutableLiveData<OrderModel> orderModelLiveData;

    public CustomerInputViewModel(@NonNull Application application) {
        super(application);
        disposable = new CompositeDisposable();
        isSaving = new MutableLiveData<>();
        msg = new MutableLiveData<>();
        orderModelLiveData = new MutableLiveData<>();
        orderSaved = new MutableLiveData<>();
        outletDetailRepository = new OutletDetailRepository(application);
        orderRepository = OrderBookingRepository.singleInstance(application);
    }


    public LiveData<Outlet> loadOutlet(Long outletId) {
        return outletDetailRepository.getOutletById(outletId);
    }

    public void findOrder(Long outletId){
        Maybe<OrderModel> orderSingle = orderRepository.findOrder(outletId);
        Disposable orderDisposable = orderSingle
                .map(orderModel -> {
                    for(OrderDetailAndPriceBreakdown orderDetail:orderModel.getOrderDetailAndCPriceBreakdowns()){
                        orderDetail.getOrderDetail().setCartonPriceBreakDown(orderDetail.getCartonPriceBreakDownList());
                        orderDetail.getOrderDetail().setUnitPriceBreakDown(orderDetail.getUnitPriceBreakDownList());
                    }

                    return orderModel;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribe(this::onOrderLoadSuccess,this::onError);
        disposable.add(orderDisposable);
    }

    private void onOrderLoadSuccess(OrderModel order){
        orderModelLiveData.postValue(order);
    }


    private void onError(Throwable throwable) throws IOException {
        throwable.printStackTrace();
        String errorBody = throwable.getMessage();
        if (throwable instanceof HttpException){
            HttpException error = (HttpException)throwable;
            errorBody = error.response().errorBody().string();
        }
        msg.postValue(errorBody);
        isSaving.postValue(false);

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

    public LiveData<OrderModel> order(){
        return orderModelLiveData;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();

    }

}
