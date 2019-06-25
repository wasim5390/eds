package com.optimus.eds.ui.cash_memo;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.annotation.NonNull;

import com.optimus.eds.db.entities.OrderDetail;
import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.model.OrderDetailAndPriceBreakdown;
import com.optimus.eds.model.OrderModel;
import com.optimus.eds.ui.route.outlet.detail.OutletDetailRepository;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.MaybeObserver;
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

        repository.findOrder(outletId).map(orderModel -> {
            List<OrderDetail> freeGoods = new ArrayList<>();
            for(OrderDetailAndPriceBreakdown orderWithDetails:orderModel.getOrderDetailAndCPriceBreakdowns()){

                freeGoods.addAll(orderWithDetails.getOrderDetail().getCartonFreeGoods());
                freeGoods.addAll(orderWithDetails.getOrderDetail().getUnitFreeGoods());
                orderWithDetails.getOrderDetail().setCartonPriceBreakDown(orderWithDetails.getCartonPriceBreakDownList());
                orderWithDetails.getOrderDetail().setUnitPriceBreakDown(orderWithDetails.getUnitPriceBreakDownList());
            }

            orderModel.setFreeGoods(freeGoods);
            for(OrderDetail orderDetail:freeGoods)
                orderModel.getOrderDetailAndCPriceBreakdowns().add(new OrderDetailAndPriceBreakdown(orderDetail));
            return orderModel;
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MaybeObserver<OrderModel>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(OrderModel order) {

            orderLiveData.postValue(order);
            }

            @Override
            public void onError(Throwable e) {
            e.printStackTrace();
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

}
