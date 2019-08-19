package com.optimus.eds.ui.home;

import android.app.Application;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.PersistableBundle;
import android.text.format.DateUtils;
import android.util.Log;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.annotation.NonNull;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import com.optimus.eds.Constant;
import com.optimus.eds.db.entities.Order;
import com.optimus.eds.db.entities.OrderDetail;
import com.optimus.eds.db.entities.Outlet;

import com.optimus.eds.model.OrderDetailAndPriceBreakdown;
import com.optimus.eds.model.OrderModel;
import com.optimus.eds.source.JobIdManager;
import com.optimus.eds.source.RetrofitHelper;
import com.optimus.eds.source.UploadOrdersService;
import com.optimus.eds.ui.order.OrderBookingRepository;
import com.optimus.eds.ui.order.OrderManager;
import com.optimus.eds.utils.PreferenceUtil;
import com.optimus.eds.utils.Util;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class HomeViewModel extends AndroidViewModel {

    private HomeRepository repository;




    public HomeViewModel(@NonNull Application application) {
        super(application);
        int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                NUMBER_OF_CORES*2,
                NUMBER_OF_CORES*2,
                60L,
                TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        ExecutorService executors = Executors.newSingleThreadExecutor();
        repository = HomeRepository.singleInstance(application,RetrofitHelper.getInstance().getApi(),executors);


    }

    public LiveData<Boolean> onStartDay(){
        return repository.startDay();
    }

    public void download(){
        isLoading().postValue(true);
        repository.fetchTodayData(false);
    }

    public void startDay(){
       // PreferenceUtil.getInstance(getApplication()).clearAllPreferences();
        repository.getToken();
    }

    public void dayEnd(){
        isLoading().postValue(true);
        repository.updateWorkStatus(false);
    }


    private List<Outlet> visitedOutlets(List<Outlet> allOutlets) {
        List<Outlet> visitedOutlets = new ArrayList<>(allOutlets.size());
        for (Outlet outlet : allOutlets) {
            if(outlet.getVisitStatus()==1)
                visitedOutlets.add(outlet);
        }
        return visitedOutlets;
    }

    public void pushOrdersToServer(){

       List<OrderModel> count =  OrderBookingRepository.singleInstance(getApplication()).findPendingOrder().blockingFirst();
       if(count.size()<1) {
           getErrorMsg().postValue("Already Uploaded");
           return;
       }
        findPendingOrders()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.computation())
                .subscribe(orderModel -> {
                    Log.i("HomeViewModel","OnNext");
                    scheduleMasterJob(getApplication(),orderModel.getOrder().getOutletId(),"Bearer "+PreferenceUtil.getInstance(getApplication()).getToken());
                },this::onError,() -> {
                    Log.i("HomeViewModel","OnComplete");
                });

    }


    private Observable<OrderModel> findPendingOrders() {
        return OrderBookingRepository.singleInstance(getApplication()).findPendingOrder()
                .concatMap(Flowable::fromIterable).toObservable().subscribeOn(Schedulers.computation());
    }

    // schedule
    public void scheduleMasterJob(Context context, Long outletId,String token) {
        PersistableBundle extras = new PersistableBundle();
        extras.putLong(Constant.EXTRA_PARAM_OUTLET_ID,outletId);
        extras.putString(Constant.TOKEN, token);
        ComponentName serviceComponent = new ComponentName(context, UploadOrdersService.class);
        JobInfo.Builder builder = new JobInfo.Builder(JobIdManager.getJobId(JobIdManager.JOB_TYPE_MASTER_UPLOAD,outletId.intValue()), serviceComponent);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY); // require any network
        builder.setExtras(extras);
        builder.setPersisted(true);
        JobScheduler jobScheduler = ContextCompat.getSystemService(context,JobScheduler.class);
        jobScheduler.schedule(builder.build());
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    public MutableLiveData<Boolean> isLoading() {
        return repository.mLoading();
    }

    public MutableLiveData<String> getErrorMsg() {
        return repository.getError();
    }



    private void onError(Throwable throwable) {
        isLoading().postValue(false);
        getErrorMsg().postValue(throwable.getMessage());
    }



    public LiveData<Boolean> syncedToday(){
        MutableLiveData<Boolean> when = new MutableLiveData<>();
        Long syncDate = PreferenceUtil.getInstance(getApplication()).getSyncDate();
        when.postValue(DateUtils.isToday(syncDate));
        return when;
    }

    public LiveData<Boolean> dayEnded(){
        MutableLiveData<Boolean> when = new MutableLiveData<>();
        Long syncDate = PreferenceUtil.getInstance(getApplication()).getEndDay();
        when.postValue(DateUtils.isToday(syncDate));
        return when;

    }
}
