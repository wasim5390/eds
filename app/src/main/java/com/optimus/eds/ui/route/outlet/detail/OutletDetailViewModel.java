package com.optimus.eds.ui.route.outlet.detail;

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
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;

import com.optimus.eds.Constant;
import com.optimus.eds.db.entities.Order;
import com.optimus.eds.db.entities.OrderStatus;
import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.source.JobIdManager;
import com.optimus.eds.source.MasterDataUploadService;
import com.optimus.eds.source.StatusRepository;

import java.util.Calendar;
import java.util.List;
import java.util.Objects;


public class OutletDetailViewModel extends AndroidViewModel {

    private final OutletDetailRepository repository;
    private final StatusRepository statusRepository;

    private final MutableLiveData<Integer> statusLiveData;

    public LiveData<Location> getOutletNearbyPos() {
        return outletNearbyPos;
    }

    private final MutableLiveData<Location> outletNearbyPos;
    private final MutableLiveData<Boolean> uploadStatus;


    private int outletStatus=1;
    private Outlet outlet;


    public OutletDetailViewModel(@NonNull Application application) {
        super(application);
        repository = new OutletDetailRepository(application);
        statusRepository = StatusRepository.singleInstance(application);
        statusLiveData = new MutableLiveData<>();
        uploadStatus = new MutableLiveData<>();
        outletNearbyPos = new MutableLiveData<>();

    }


    public LiveData<Outlet> findOutlet(Long outletId){
        return repository.getOutletById(outletId);
    }

    public void setOutlet(Outlet outlet){
        this.outlet = outlet;
    }


    // schedule
    public void scheduleMasterJob(Context context, Long outletId,Location location, Long visitDateTime,Long visitEndTime,String reason,String token) {

        PersistableBundle extras = new PersistableBundle();
        extras.putLong(Constant.EXTRA_PARAM_OUTLET_ID,outletId);
        extras.putInt(Constant.EXTRA_PARAM_OUTLET_STATUS_ID,outletStatus);
        extras.putLong(Constant.EXTRA_PARAM_OUTLET_VISIT_TIME,visitDateTime);
        extras.putLong(Constant.EXTRA_PARAM_OUTLET_VISIT_END_TIME,visitEndTime);
        extras.putDouble(Constant.EXTRA_PARAM_PRESELLER_LAT,location.getLatitude());
        extras.putDouble(Constant.EXTRA_PARAM_PRESELLER_LNG,location.getLongitude());
        extras.putString(Constant.EXTRA_PARAM_OUTLET_REASON_N_ORDER,reason);
        extras.putString(Constant.TOKEN, "Bearer "+token);
        ComponentName serviceComponent = new ComponentName(context, MasterDataUploadService.class);
        int jobId = JobIdManager.getJobId(JobIdManager.JOB_TYPE_MASTER_UPLOAD,outletId.intValue());
        boolean jobFound = false;
        JobInfo.Builder builder = new JobInfo.Builder(jobId, serviceComponent);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY); // require any network
        builder.setExtras(extras);
        builder.setMinimumLatency(1000);
        builder.setOverrideDeadline(2000);
        builder.setPersisted(true);
        JobScheduler jobScheduler = ContextCompat.getSystemService(context,JobScheduler.class);
        List<JobInfo> scheduledJobs =jobScheduler.getAllPendingJobs();
        for(JobInfo jobInfo:scheduledJobs){
            if (jobInfo.getId() != jobId) {
                continue; }
            jobFound=true;
            break;
        }
        if(jobFound)
            jobScheduler.cancel(jobId);
        Objects.requireNonNull(jobScheduler).schedule(builder.build());
    }


    public void updateOutletStatusCode(int code){
        outletStatus = code;
        statusLiveData.postValue(code);
    }

    public void updateOutletVisitEndTime(Long outletId,Long time){

        statusRepository.updateStatusOutletEndTime(time,outletId);
    }

    public LiveData<Integer> getStatusLiveData() {
        return statusLiveData;
    }

    public void onNextClick(Location currentLocation,Long outletVisitStartTime) {

        Location outletLocation = new Location("Outlet Location");
        outletLocation.setLatitude(outlet.getLatitude());
        outletLocation.setLongitude(outlet.getLongitude());
        double distance = currentLocation.distanceTo(outletLocation);
        // TODO enable this distance calculation check for live build
        /*if(distance>30 && outletStatus<=2)
            outletNearbyPos.postValue(outletLocation);
        else*/
            {
            outlet.setVisitTimeLat(currentLocation.getLatitude());
            outlet.setVisitTimeLng(currentLocation.getLongitude());
            outlet.setVisitStatus(outletStatus);
            outlet.setSynced(0);
            if(statusLiveData.getValue()!=null) {
                OrderStatus orderStatus = new OrderStatus(outlet.getOutletId(),outletStatus,0,0.0);
                orderStatus.setOutletVisitStartTime(outletVisitStartTime);
                statusRepository.insertStatus(orderStatus);
                repository.updateOutlet(outlet);
                uploadStatus.postValue(statusLiveData.getValue() != 1);
            }
        }


    }

    public void postEmptyCheckout(boolean noOrderFromBooking,Long outletVisitStartTime,Long outletVisitEndTime){
        if(noOrderFromBooking) {
            outletStatus = Constant.STATUS_NO_ORDER_FROM_BOOKING; // 6 means no order from booking view
            outlet.setSynced(0);
            outlet.setVisitStatus(outletStatus);
            repository.updateOutlet(outlet); // TODO remove this if below successful
            OrderStatus status = new OrderStatus(outlet.getOutletId(),outletStatus,0,0.0);
            status.setOutletVisitStartTime(outletVisitStartTime);
            status.setOutletVisitEndTime(outletVisitEndTime);
            statusRepository.insertStatus(status);
            uploadStatus.postValue(true);

        }
    }

    public LiveData<Boolean> getUploadStatus() {
        return uploadStatus;
    }

    public LiveData<Boolean> loadProducts(){
        return repository.loadProductsFromServer();
    }

}
