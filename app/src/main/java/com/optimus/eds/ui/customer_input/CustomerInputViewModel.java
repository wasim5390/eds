package com.optimus.eds.ui.customer_input;

import android.app.Application;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.Toast;

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
import com.optimus.eds.db.entities.OrderStatus;
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
import com.optimus.eds.source.StatusRepository;
import com.optimus.eds.source.UploadOrdersService;
import com.optimus.eds.ui.merchandize.MerchandiseRepository;
import com.optimus.eds.ui.order.OrderBookingRepository;
import com.optimus.eds.ui.route.outlet.detail.OutletDetailRepository;
import com.optimus.eds.utils.NetworkManager;
import com.optimus.eds.utils.PreferenceUtil;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeoutException;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import io.reactivex.Completable;
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
    private OutletDetailRepository outletDetailRepository;
    private StatusRepository statusRepository;
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
        outletDetailRepository = new OutletDetailRepository(application);
        statusRepository=StatusRepository.singleInstance(application);
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
                .subscribeOn(Schedulers.io()).subscribe(this::onOrderLoadSuccess,this::error);
        disposable.add(orderDisposable);
    }


    public void saveOrder(String mobileNumber,String remarks,String cnic,String strn,String base64Sign,long outletVisitTime){
        isSaving.postValue(true);
        OrderModel orderModel = orderModelLiveData.getValue();
        Order order = orderModel.getOrder();

        CustomerInput customerInput = new CustomerInput(outletId,order.getLocalOrderId(),mobileNumber,cnic,strn,remarks,base64Sign);

        customerInputRepository.saveCustomerInput(customerInput)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(() -> {
                    postData(orderModel,customerInput);
                    scheduleMerchandiseJob(getApplication(),outletId, PreferenceUtil.getInstance(getApplication()).getToken());
                    updateStock(getApplication(),outletId);
                });


    }



    public void postData(OrderModel orderModel,CustomerInput customerInput){
        statusRepository.updateStatusOutletEndTime(Calendar.getInstance().getTimeInMillis(),outletId);
        updateOutletTaskStatus(outletId,Constant.STATUS_PENDING_TO_SYNC,0,orderModel.getOrder().getPayable());
        outletDetailRepository.updateOutletCnic(outletId,customerInput.getMobileNumber(),customerInput.getCnic(),customerInput.getStrn());
        NetworkManager.getInstance().isOnline().subscribe((aBoolean, throwable) -> {
            if (!aBoolean){
                 scheduleMasterJob(getApplication(),outletId,Constant.STATUS_COMPLETED,orderModel.getOutlet().getVisitTimeLat(),orderModel.getOutlet().getVisitTimeLng(),
                         "",PreferenceUtil.getInstance(getApplication()).getToken());
                isSaving.postValue(false);
                orderSaved.postValue(true);
            }else {
                uploadMasterData(generateOrder(orderModel,customerInput));
            }
    });
    }

    //**************** Post Order ****************/
    public MasterModel generateOrder(OrderModel orderModel,CustomerInput customerInput){

       OrderStatus status= statusRepository.findOrderStatus(outletId).subscribeOn(Schedulers.io()).blockingGet();

        MasterModel masterModel = new MasterModel();

        Order order = orderModel.getOrder();
        Gson gson  = new Gson();
        String json = gson.toJson(order);
        OrderResponseModel responseModel = gson.fromJson(json,OrderResponseModel.class);
        responseModel.setOrderDetails(orderModel.getOrderDetails());

        masterModel.setCustomerInput(customerInput);
        masterModel.setOrderModel(responseModel);
        masterModel.setLocation(orderModel.getOutlet().getVisitTimeLat(),orderModel.getOutlet().getVisitTimeLng());
        masterModel.setOutletId(order.getOutletId());
        masterModel.setOutletStatus(Constant.STATUS_CONTINUE); // 8 for order complete
        if(status!=null)
        masterModel.setOutletVisitTime(status.getOutletVisitStartTime()>0?status.getOutletVisitStartTime():null);
        masterModel.setOutletEndTime(Calendar.getInstance().getTimeInMillis());
        return masterModel;

    }

    private void uploadMasterData(MasterModel masterModel) {

        RetrofitHelper.getInstance().getApi().saveOrder(masterModel,"Bearer "+PreferenceUtil.getInstance(getApplication()).getToken())
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(this::onUpload,this::error);
    }

    private void onUpload(MasterModel orderResponseModel) throws IOException{
        if(!orderResponseModel.isSuccess()){
            error(orderResponseModel);
            return;
        }
        if(orderResponseModel!=null) {
            orderResponseModel.setCustomerInput(null);
            orderResponseModel.getOrderModel().setOrderDetails(null);
        }


        if(orderResponseModel !=null && orderResponseModel.getOrderModel()!=null)
            OrderBookingRepository.singleInstance(getApplication())
                    .findOrderById(orderResponseModel.getOrderModel().getMobileOrderId()).map(order -> {

                order.setOrderStatus(orderResponseModel.getOrderModel().getOrderStatusId());
                return order;
            }).flatMapCompletable(order -> OrderBookingRepository.singleInstance(getApplication())
                    .updateOrder(order)).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> {
                        Log.i("UploadOrdersService", "Order Status Updated");
                        updateOutletTaskStatus(orderResponseModel.getOutletId(),Constant.STATUS_COMPLETED,1,orderResponseModel.getOrderModel().getPayable());
                    },this::error);
        msg.postValue("Order Uploaded Successfully!");
        orderSavedSuccess(orderResponseModel);

    }

    private void updateOutletTaskStatus(Long outletId,int status,int sync,Double amount){
        outletDetailRepository.updateOutletVisitStatus(outletId,status,sync); // 7 for completed task
        statusRepository.updateStatus(new OrderStatus(outletId,status,sync,amount));
    }


    // ************************************/



    // schedule
    public void scheduleMasterJob(Context context, Long outletId,Integer outletStatus, Double latitude,Double longitude, String reason, String token) {
        PersistableBundle extras = new PersistableBundle();
        extras.putLong(Constant.EXTRA_PARAM_OUTLET_ID,outletId);
        extras.putInt(Constant.EXTRA_PARAM_OUTLET_STATUS_ID,outletStatus);
        extras.putDouble(Constant.EXTRA_PARAM_PRESELLER_LAT,latitude);
        extras.putDouble(Constant.EXTRA_PARAM_PRESELLER_LNG,longitude);
        extras.putString(Constant.EXTRA_PARAM_OUTLET_REASON_N_ORDER,reason);
        extras.putString(Constant.TOKEN, "Bearer "+token);
        ComponentName serviceComponent = new ComponentName(context, UploadOrdersService.class);
        JobInfo.Builder builder = new JobInfo.Builder(JobIdManager.getJobId(JobIdManager.JOB_TYPE_MASTER_UPLOAD,outletId.intValue()), serviceComponent);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY); // require any network

        builder.setMinimumLatency(1);
        builder.setOverrideDeadline(1);
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
        JobInfo.Builder builder = new JobInfo.Builder(JobIdManager.getJobId(JobIdManager.JOB_TYPE_MERCHANDISE_UPLOAD,outletId.intValue()), serviceComponent);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY); // require any network
        builder.setOverrideDeadline(1);
        builder.setMinimumLatency(1);
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




    private void error(Object throwable) throws IOException {
        String errorBody = Constant.GENERIC_ERROR;
        if (throwable instanceof IOException || throwable instanceof TimeoutException
        || throwable instanceof SocketTimeoutException
        ){
            errorBody = Constant.NETWORK_ERROR;
            msg.postValue(errorBody);
            isSaving.postValue(false); return;
        }else if(throwable instanceof Throwable) {
            Throwable mThrowable = (Throwable) throwable;
            mThrowable.printStackTrace();
            errorBody = mThrowable.getMessage();
            if (throwable instanceof HttpException) {
                HttpException error = (HttpException) throwable;
               // errorBody = error.response().errorBody().string();
            }
        }
        else{
            if(((MasterModel)throwable).getErrorCode()==2)
            errorBody =((MasterModel)throwable).getResponseMsg();

        }
        MasterModel baseResponse = new MasterModel();
        baseResponse.setResponseMsg(errorBody);
        baseResponse.setSuccess(false);
        msg.postValue(errorBody);
        orderSavedSuccess(baseResponse);
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
