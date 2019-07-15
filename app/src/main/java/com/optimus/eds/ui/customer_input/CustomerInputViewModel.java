package com.optimus.eds.ui.customer_input;

import android.app.Application;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.location.Location;
import android.os.PersistableBundle;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.optimus.eds.Constant;
import com.optimus.eds.db.entities.CustomerInput;
import com.optimus.eds.db.entities.Order;
import com.optimus.eds.db.entities.OrderDetail;
import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.model.BaseResponse;
import com.optimus.eds.model.MasterModel;
import com.optimus.eds.model.OrderDetailAndPriceBreakdown;
import com.optimus.eds.model.OrderModel;
import com.optimus.eds.model.OrderResponseModel;
import com.optimus.eds.source.API;
import com.optimus.eds.source.JobIdManager;
import com.optimus.eds.source.MasterDataUploadService;
import com.optimus.eds.source.ProductUpdateService;
import com.optimus.eds.source.RetrofitHelper;
import com.optimus.eds.source.MerchandiseUploadService;
import com.optimus.eds.source.UploadOrdersService;
import com.optimus.eds.ui.merchandize.MerchandiseRepository;
import com.optimus.eds.ui.order.OrderBookingRepository;
import com.optimus.eds.utils.PreferenceUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.CompletableObserver;
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
        customerInputRepository = new CustomerInputRepository(application);
        orderRepository = OrderBookingRepository.singleInstance(application);
    }


    public LiveData<Outlet> loadOutlet(Long outletId) {
        this.outletId = outletId;
        return customerInputRepository.getOutletById(outletId);
    }

    public void findOrder(Long outletId){

        Maybe<OrderModel> orderSingle = orderRepository.findOrder(outletId);
        Disposable orderDisposable = orderSingle
                .map(orderModel -> {
                    List<OrderDetail> orderDetails = new ArrayList<>();
                    for(OrderDetailAndPriceBreakdown orderDetail:orderModel.getOrderDetailAndCPriceBreakdowns()){
                        orderDetail.getOrderDetail().setCartonPriceBreakDown(orderDetail.getCartonPriceBreakDownList());
                        orderDetail.getOrderDetail().setUnitPriceBreakDown(orderDetail.getUnitPriceBreakDownList());
                        orderDetails.add(orderDetail.getOrderDetail());
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
        Order order = orderModel.getOrder();
      /*  Gson gson  = new Gson();
        String json = gson.toJson(order);
        OrderResponseModel responseModel = gson.fromJson(json,OrderResponseModel.class);
        responseModel.setOrderDetails(orderModel.getOrderDetails());*/
        CustomerInput customerInput = new CustomerInput(outletId,order.getLocalOrderId(),deliveryDate,mobileNumber,remarks,base64Sign);

   /*     MasterModel masterModel = new MasterModel();
        masterModel.setLocation(orderModel.getOutlet().getVisitTimeLat(),orderModel.getOutlet().getVisitTimeLng());
        masterModel.setOutletId(order.getOutletId());
        masterModel.setOrderModel(responseModel);
        masterModel.setCustomerInput(customerInput);
        masterModel.setOutletVisitTime(orderModel.getOutlet().getVisitDateTime());*/
        customerInputRepository.saveCustomerInput(customerInput)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(() -> {
                    scheduleMasterJob(getApplication(),outletId,1,orderModel.getOutlet().getVisitTimeLat(),orderModel.getOutlet().getVisitTimeLng(),
                            deliveryDate,"","Bearer "+PreferenceUtil.getInstance(getApplication()).getToken());

                    scheduleMerchandiseJob(getApplication(),outletId, PreferenceUtil.getInstance(getApplication()).getToken());

                    updateStock(getApplication(),outletId);
                });

  /*disposable.add(webservice.saveOrder(masterModel,"Bearer "+PreferenceUtil.getInstance(getApplication()).getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(this::orderSavedSuccess,this::onError));*/

    }



    // schedule
    public void scheduleMasterJob(Context context, Long outletId,Integer outletStatus, Double latitude,Double longitude,Long visitDateTime, String reason, String token) {
        PersistableBundle extras = new PersistableBundle();
        extras.putLong(Constant.EXTRA_PARAM_OUTLET_ID,outletId);
        extras.putInt(Constant.EXTRA_PARAM_OUTLET_STATUS_ID,outletStatus);
        extras.putLong(Constant.EXTRA_PARAM_OUTLET_VISIT_TIME,visitDateTime);
        extras.putDouble(Constant.EXTRA_PARAM_PRESELLER_LAT,latitude);
        extras.putDouble(Constant.EXTRA_PARAM_PRESELLER_LNG,longitude);
        extras.putString(Constant.EXTRA_PARAM_OUTLET_REASON_N_ORDER,reason);
        extras.putString(Constant.TOKEN, "Bearer "+token);
        ComponentName serviceComponent = new ComponentName(context, UploadOrdersService.class);
        JobInfo.Builder builder = new JobInfo.Builder(JobIdManager.getJobId(JobIdManager.JOB_TYPE_MASTER_UPLOAD,outletId.intValue()), serviceComponent);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY); // require any network
        builder.setExtras(extras);
        builder.setPersisted(true);
        JobScheduler jobScheduler = ContextCompat.getSystemService(context,JobScheduler.class);
        jobScheduler.schedule(builder.build());
    }

    // schedule
    public void scheduleMerchandiseJob(Context context,Long outletId,String token) {
        PersistableBundle extras = new PersistableBundle();
        extras.putLong(Constant.EXTRA_PARAM_OUTLET_ID,outletId);
        extras.putString(Constant.TOKEN, "Bearer "+token);
        ComponentName serviceComponent = new ComponentName(context, MerchandiseUploadService.class);
        JobInfo.Builder builder = new JobInfo.Builder(outletId.intValue(), serviceComponent);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY); // require any network
        builder.setExtras(extras);
        JobScheduler jobScheduler = ContextCompat.getSystemService(context,JobScheduler.class);
        jobScheduler.schedule(builder.build());
    }

    public void updateStock(Context context,Long outletId) {
        PersistableBundle extras = new PersistableBundle();
        extras.putLong(Constant.EXTRA_PARAM_OUTLET_ID,outletId);
        ComponentName serviceComponent = new ComponentName(context, ProductUpdateService.class);
        JobInfo.Builder builder = new JobInfo.Builder(JobIdManager.getJobId(JobIdManager.JOB_TYPE_UPDATE_STOCK,outletId.intValue()), serviceComponent);
        builder.setOverrideDeadline(0);
        builder.setExtras(extras);
        JobScheduler jobScheduler = ContextCompat.getSystemService(context,JobScheduler.class);
        jobScheduler.schedule(builder.build());
    }

    public void orderSavedSuccess(BaseResponse order) {
        isSaving.postValue(false);
        if(order!=null)
            orderSaved.postValue(order.isSuccess());
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
