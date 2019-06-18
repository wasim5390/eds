package com.optimus.eds.ui.customer_input;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.optimus.eds.Constant;
import com.optimus.eds.db.entities.CustomerInput;
import com.optimus.eds.db.entities.Merchandise;
import com.optimus.eds.db.entities.Order;
import com.optimus.eds.db.entities.OrderDetail;
import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.model.MasterModel;
import com.optimus.eds.model.OrderDetailAndPriceBreakdown;
import com.optimus.eds.model.OrderModel;
import com.optimus.eds.model.OrderResponseModel;
import com.optimus.eds.source.API;
import com.optimus.eds.source.MerchandiseWorker;
import com.optimus.eds.source.RetrofitHelper;
import com.optimus.eds.ui.merchandize.MerchandiseImage;
import com.optimus.eds.ui.merchandize.MerchandiseRepository;
import com.optimus.eds.ui.order.OrderBookingRepository;
import com.optimus.eds.utils.Util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import io.reactivex.Maybe;
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
    private CustomerInputRepository customerInputRepository;
    private MerchandiseRepository merchandiseRepository;
    private final CompositeDisposable disposable;
    private Long outletId;
    private MutableLiveData<OrderModel> orderModelLiveData;
    private MutableLiveData<Merchandise> merchandiseMutableLiveData;
    private WorkManager mWorkManager;
    private final API webservice;

    public CustomerInputViewModel(@NonNull Application application) {
        super(application);
        disposable = new CompositeDisposable();
        webservice = RetrofitHelper.getInstance().getApi();
        isSaving = new MutableLiveData<>();
        msg = new MutableLiveData<>();
        orderModelLiveData = new MutableLiveData<>();
        merchandiseMutableLiveData = new MutableLiveData<>();
        orderSaved = new MutableLiveData<>();
        customerInputRepository = new CustomerInputRepository(application);
        orderRepository = OrderBookingRepository.singleInstance(application);
        merchandiseRepository = new MerchandiseRepository(application);
        mWorkManager  = WorkManager.getInstance();
    }

    private void loadMerchandise(Long outletId){
        Disposable merchandiseDisposable = merchandiseRepository.findMerchandise(outletId)
                .map(merchandise -> {
                    for(MerchandiseImage merchandiseImage: merchandise.getMerchandiseImages()){
                        merchandiseImage.setBase64Image(Util.imageFileToBase64(new File(merchandiseImage.getPath())));
                    }
                    return merchandise;
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribe(this::onMerchandiseLoaded,this::onError);
        disposable.add(merchandiseDisposable);
    }

    private void onMerchandiseLoaded(Merchandise merchandise) {
        merchandiseMutableLiveData.postValue(merchandise);
    }


    public LiveData<Outlet> loadOutlet(Long outletId) {
        this.outletId = outletId;
        return customerInputRepository.getOutletById(outletId);
    }

    public void findOrder(Long outletId){
        loadMerchandise(outletId);
        Maybe<OrderModel> orderSingle = orderRepository.findOrder(outletId);
        Disposable orderDisposable = orderSingle
                .map(orderModel -> {
                    //orderModel.getOrderDetails().clear();
                    List<OrderDetail> orderDetails = new ArrayList<>();
                    for(OrderDetailAndPriceBreakdown orderDetail:orderModel.getOrderDetailAndCPriceBreakdowns()){
                        orderDetail.getOrderDetail().setCartonPriceBreakDown(orderDetail.getCartonPriceBreakDownList());
                        orderDetail.getOrderDetail().setUnitPriceBreakDown(orderDetail.getUnitPriceBreakDownList());
                        orderDetails.add(orderDetail.getOrderDetail());
                        //orderModel.getOrderDetails().add(orderDetail.getOrderDetail());
                    }
                    orderModel.setOrderDetails(orderDetails);

                    return orderModel;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribe(this::onOrderLoadSuccess,this::onError);
        disposable.add(orderDisposable);
    }

    public void saveOrder(String mobileNumber,String remarks,String base64Sign,long deliveryDate){
        isSaving.postValue(true);
        OrderModel orderModel = orderModelLiveData.getValue();
        Merchandise merchandise = merchandiseMutableLiveData.getValue();
       // MerchandiseModel merchandiseModel = new MerchandiseModel(merchandise);
        Order order = orderModel.getOrder();
        Gson gson  = new Gson();
        String json = gson.toJson(order);
        OrderResponseModel responseModel = gson.fromJson(json,OrderResponseModel.class);
        responseModel.setOrderDetails(orderModel.getOrderDetails());
        CustomerInput customerInput = new CustomerInput(outletId,order.getLocalOrderId(),deliveryDate,mobileNumber,remarks,base64Sign);
        customerInputRepository.saveCustomerInput(customerInput);
        MasterModel masterModel = new MasterModel();
        masterModel.setOutletId(order.getOutletId());
        masterModel.setOrderModel(responseModel);
        masterModel.setCustomerInput(customerInput);


        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest merchandiseUploadReq =
                new OneTimeWorkRequest.Builder(MerchandiseWorker.class)
                        .setConstraints(constraints)
                        .setInputData(createInputDataForUri(outletId))
                        .build();
        mWorkManager.enqueue(merchandiseUploadReq);


        isSaving.postValue(false);
        orderSaved.postValue(true);
      //  disposable.add(webservice.postMerchandise(merchandiseModel).subscribeOn(Schedulers.io())
      //          .observeOn(Schedulers.io()).subscribe(baseResponse -> {},this::onError));
      //  UploadFileService.uploadMedia(getApplication(),String.valueOf(outletId));



      //  disposable.add(webservice.saveOrder(masterModel).subscribeOn(Schedulers.io())
      //          .observeOn(AndroidSchedulers.mainThread()).subscribe(this::orderSavedSuccess,this::onError));


    }

    /**
     * Creates the input data bundle which includes the Uri to operate on
     * @return Data which contains the OutletId as a Long
     */
    private Data createInputDataForUri(Long outletId) {
        Data.Builder builder = new Data.Builder();
        if (outletId != null) {
            builder.putLong(Constant.EXTRA_PARAM_OUTLET_ID, outletId);
        }
        return builder.build();
    }

    private void orderSavedSuccess(OrderResponseModel order) {
        isSaving.postValue(false);
        orderSaved.postValue(true);
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
