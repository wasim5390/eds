package com.optimus.eds.ui.route.outlet;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.db.entities.Route;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.reactivex.disposables.CompositeDisposable;


public class OutletListViewModel extends AndroidViewModel {
    private OutletListRepository repository;
    public MutableLiveData<List<Outlet>> outletList;
    public MutableLiveData<List<Route>> routeList;
    private MutableLiveData<Boolean> isLoading;
    private MutableLiveData<String> errorMsg;
    CompositeDisposable disposable;
    Long SELECTED_ROUTE_ID;


    public OutletListViewModel(@NonNull Application application) {
        super(application);
        repository = OutletListRepository.getInstance(application);
        outletList = new MutableLiveData<>();
        routeList = new MutableLiveData<>();
        disposable = new CompositeDisposable();
        errorMsg = new MutableLiveData<>();
        isLoading = new MutableLiveData<>();
        loadRoutesFromDb();

    }



    public void loadRoutesFromDb() {

        repository.getRoutes().observeForever(routes -> routeList.setValue(routes));

   /*    Disposable allRoutesDisposable = repository.getRoutes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onRoutesFetched, this::onError);

        disposable.add(allRoutesDisposable);*/
    }


    public void loadOutletsFromDb(Long routeId){
      repository.getOutlets(routeId).observeForever(outlets -> outletList.setValue(outlets));

    }

    private void onRoutesFetched(List<Route> routes) {
         routeList.setValue(routes);
         isLoading.setValue(false);
    }

    private void onOutletsFetched(List<Outlet> outlets) {
        outletList.setValue(outlets);
        isLoading.setValue(false);
    }



    public LiveData<List<Outlet>> getOutletList(){
        return outletList;
    }

    public LiveData<List<Route>> getRouteList(){

        return routeList;
    }
    private void onError(Throwable throwable) {
        isLoading.setValue(false);
        errorMsg.setValue(throwable.getMessage());
    }
}
