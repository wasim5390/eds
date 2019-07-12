package com.optimus.eds.ui.home;

import android.app.Application;
import android.text.format.DateUtils;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.annotation.NonNull;

import com.optimus.eds.db.entities.Outlet;

import com.optimus.eds.source.RetrofitHelper;
import com.optimus.eds.utils.PreferenceUtil;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class HomeViewModel extends AndroidViewModel {

    private HomeRepository repository;


    private MutableLiveData<Boolean> isLoading;
    private MutableLiveData<String> errorMsg;


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
        isLoading = new MutableLiveData<>();
        errorMsg = new MutableLiveData<>();
        repository.mLoading().observeForever(aBoolean -> {
            isLoading.postValue(aBoolean);
        });

    }

    public LiveData<Boolean> onStartDay(){
        return repository.startDay();
    }

    public void download(){
        isLoading.postValue(true);
        repository.fetchTodayData(false);
    }

    public void startDay(){
        PreferenceUtil.getInstance(getApplication()).clearAllPreferences();
        repository.getToken();
    }

    public void dayEnd(){
        isLoading.postValue(true);
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

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    public LiveData<Boolean> isLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMsg() {
        return repository.getError();
    }



    private void onError(Throwable throwable) {
        isLoading.setValue(false);
        errorMsg.setValue(throwable.getMessage());
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
