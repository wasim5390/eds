package com.optimus.eds.source;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.PersistableBundle;
import android.util.Log;

import com.optimus.eds.Constant;
import com.optimus.eds.db.entities.OrderDetail;
import com.optimus.eds.db.entities.Product;
import com.optimus.eds.model.OrderDetailAndPriceBreakdown;

import com.optimus.eds.ui.order.OrderBookingRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

public class ProductUpdateService extends JobService implements Constant {

    private final String iTAG = ProductUpdateService.class.getSimpleName();

    @Override
    public boolean onStartJob(JobParameters params) {
        if (params != null) {
            PersistableBundle bundle = params.getExtras();
            final Long outletId = bundle.getLong(EXTRA_PARAM_OUTLET_ID);

            updateProductCounter(outletId);
        }
        return true;
    }

    private void updateProductCounter(Long outletId){
        OrderBookingRepository.singleInstance(getApplication()).findOrder(outletId)
                .map(orderModel -> {
                    List<OrderDetail> orderDetails = new ArrayList<>();
                    for (OrderDetailAndPriceBreakdown orderDetail : orderModel.getOrderDetailAndCPriceBreakdowns()) {
                        orderDetails.add(orderDetail.getOrderDetail());
                    }
                    orderModel.setOrderDetails(orderDetails);

                    return orderModel;
                }).toObservable()
                .flatMap(orderModel -> getOrderDetailObservable(orderModel.getOrderDetails()))
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(orderDetail -> {
                    findProduct(orderDetail);
                });

    }

    private void findProduct(OrderDetail orderItem){

        Single<Product> productSingle = OrderBookingRepository.singleInstance(getApplication()).findProductById(orderItem.getProductId());
        productSingle.map(product -> {
            Integer cartonQty = orderItem.getCartonQuantity()==null?0:orderItem.getCartonQuantity();
            Integer unitQty = orderItem.getUnitQuantity()==null?0:orderItem.getUnitQuantity();
            Integer productCartonStockInHand = product.getCartonStockInHand()==null?0:product.getCartonStockInHand();
            Integer productUnitStockInHand = product.getUnitStockInHand()==null?0:product.getUnitStockInHand();
            product.setCartonStockInHand(productCartonStockInHand-cartonQty);
            product.setUnitStockInHand(productUnitStockInHand-unitQty);
            return product;
        }).flatMapCompletable(
                product -> OrderBookingRepository.singleInstance(getApplication())
                        .updateProduct(product)).observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io()).subscribe(() -> {
            Log.i(iTAG,"onComplete");
        },this::error);

    }

    private Observable<OrderDetail> getOrderDetailObservable(List<OrderDetail> orderDetail) {

        return Observable
                .create((ObservableOnSubscribe<OrderDetail>) emitter -> {
                    for (OrderDetail mOrderDetail : orderDetail) {
                        if (!emitter.isDisposed()) {
                            emitter.onNext(mOrderDetail);
                        }
                    }

                    if (!emitter.isDisposed()) {
                        emitter.onComplete();
                    }
                }).subscribeOn(Schedulers.io());
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    private void error(Throwable throwable) throws IOException {

        throwable.printStackTrace();
        String errorBody = throwable.getMessage();
        if (throwable instanceof HttpException){
            HttpException error = (HttpException)throwable;
            errorBody = error.response().errorBody().string();
        }
        ProductUpdateService.this.stopSelf();
    }
}
