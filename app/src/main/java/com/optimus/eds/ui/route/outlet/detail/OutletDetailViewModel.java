package com.optimus.eds.ui.route.outlet.detail;

import android.app.Application;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.PersistableBundle;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.optimus.eds.Constant;
import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.db.entities.Route;
import com.optimus.eds.source.JobIdManager;
import com.optimus.eds.source.MasterDataUploadService;
import com.optimus.eds.source.MerchandiseUploadService;

import java.util.List;


public class OutletDetailViewModel extends AndroidViewModel {

    private OutletDetailRepository repository;
    private LiveData<Outlet> outletLiveData;
    private MutableLiveData<Integer> statusLiveData;

    private MutableLiveData<Boolean> uploadStatus;

    private int outletStatus=1;



    public OutletDetailViewModel(@NonNull Application application) {
        super(application);
        repository = new OutletDetailRepository(application);
        statusLiveData = new MutableLiveData();
        uploadStatus = new MutableLiveData<>();

    }


    public LiveData<Outlet> findOutlet(Long outletId){
        return repository.getOutletById(outletId);
    }



    // schedule
    public void scheduleMasterJob(Context context, Long outletId, String token) {
        PersistableBundle extras = new PersistableBundle();
        extras.putLong(Constant.EXTRA_PARAM_OUTLET_ID,outletId);
        extras.putInt(Constant.EXTRA_PARAM_OUTLET_STATUS_ID,outletStatus);
        extras.putString(Constant.TOKEN, "Bearer "+token);
        ComponentName serviceComponent = new ComponentName(context, MasterDataUploadService.class);
        JobInfo.Builder builder = new JobInfo.Builder(JobIdManager.getJobId(JobIdManager.JOB_TYPE_MASTER_UPLOAD,outletId.intValue()), serviceComponent);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY); // require any network
        builder.setExtras(extras);
        builder.setPersisted(true);
        JobScheduler jobScheduler = ContextCompat.getSystemService(context,JobScheduler.class);
        jobScheduler.schedule(builder.build());
    }


    public void updateOutletStatusCode(int code){
        outletStatus = code;
        statusLiveData.postValue(code);
    }

    public LiveData<Integer> getStatusLiveData() {
        return statusLiveData;
    }

    public void onNextClick() {
        uploadStatus.postValue(statusLiveData.getValue()==1?false:true);
    }

    public LiveData<Boolean> getUploadStatus() {
        return uploadStatus;
    }
}
