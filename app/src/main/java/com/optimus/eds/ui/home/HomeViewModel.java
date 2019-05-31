package com.optimus.eds.ui.home;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

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

    public void startDay(){
        PreferenceUtil.getInstance(getApplication()).clearAllPreferences();
        repository.getToken();
        //repository.fetchTodayData();
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
        return errorMsg;
    }



    private void onError(Throwable throwable) {
        isLoading.setValue(false);
        errorMsg.setValue(throwable.getMessage());
    }




}
