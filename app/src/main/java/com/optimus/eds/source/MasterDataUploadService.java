package com.optimus.eds.source;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.PersistableBundle;
import android.util.Log;

import com.optimus.eds.Constant;
import com.optimus.eds.db.entities.OrderStatus;
import com.optimus.eds.model.BaseResponse;
import com.optimus.eds.model.MasterModel;
import com.optimus.eds.ui.route.outlet.detail.OutletDetailRepository;

import java.io.File;
import java.io.IOException;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

public class MasterDataUploadService extends JobService implements Constant {

    private final String iTAG = MasterDataUploadService.class.getSimpleName();
    String token;
    private int jobId;
    private JobParameters parameters;
    private OutletDetailRepository outletDetailRepository;
    private StatusRepository statusRepository;
    public MasterDataUploadService() {

    }

    @Override
    public boolean onStartJob(JobParameters params) {
        parameters = params;
        if (params != null) {
            outletDetailRepository = new OutletDetailRepository(getApplication());
            statusRepository = StatusRepository.singleInstance(getApplication());
            PersistableBundle bundle = params.getExtras();
            jobId=params.getJobId();
            Log.i(iTAG,"JobId: "+jobId);
            final Long outletId = bundle.getLong(EXTRA_PARAM_OUTLET_ID);
            final Integer statusId = bundle.getInt(EXTRA_PARAM_OUTLET_STATUS_ID);
            final String reason = bundle.getString(EXTRA_PARAM_OUTLET_REASON_N_ORDER,"");
            final Double latitude = bundle.getDouble(EXTRA_PARAM_PRESELLER_LAT,0);
            final Double longitude = bundle.getDouble(EXTRA_PARAM_PRESELLER_LNG,0);

            token = bundle.getString(TOKEN);
            MasterModel masterModel = new MasterModel();
            masterModel.setOutletId(outletId);
            masterModel.setOutletStatus(statusId);
            masterModel.setReason(reason);
            masterModel.setLocation(latitude,longitude);
            uploadMasterData(masterModel);
        }
        return true;
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
        MasterDataUploadService.this.stopSelf();
    }


    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void uploadMasterData(MasterModel orderModel) {

       OrderStatus status= statusRepository.findOrderStatus(orderModel.getOutletId())
               .observeOn(Schedulers.io())
               .subscribeOn(Schedulers.io()).blockingGet();
       if(status!=null) {
           orderModel.setOutletVisitTime(status.getOutletVisitStartTime());
           orderModel.setOutletEndTime(status.getOutletVisitEndTime());

       }
        RetrofitHelper.getInstance().getApi().saveOrder(orderModel,token)
                .observeOn(Schedulers.io()).subscribeOn(Schedulers.io()).subscribe(masterModel -> {
            onUpload(masterModel,orderModel);
        },this::error);
    }

    private void onUpload(BaseResponse baseResponse,MasterModel orderModel) {
        if(baseResponse.isSuccess()) {
            Log.i(iTAG, "File Uploaded");
            outletDetailRepository.updateOutletVisitStatus(orderModel.getOutletId(),orderModel.getOutletStatus(),1);
            statusRepository.updateStatus(new OrderStatus(orderModel.getOutletId(),orderModel.getOutletStatus(),1,0.0));
            statusRepository.updateStatusOutletStartTime(orderModel.getOutletVisitTime(),orderModel.getOutletId());
            statusRepository.updateStatusOutletEndTime(orderModel.getOutletEndTime(),orderModel.getOutletId());
            Intent intent = new Intent();
            intent.setAction(Constant.ACTION_ORDER_UPLOAD);
            intent.putExtra("Response", baseResponse);
            LocalBroadcastManager.getInstance(getApplication()).sendBroadcast(intent);
        }
        else
            Log.i(iTAG,baseResponse.getResponseMsg());

        jobFinished(this.parameters,false);
        MasterDataUploadService.this.stopSelf();

    }


}

