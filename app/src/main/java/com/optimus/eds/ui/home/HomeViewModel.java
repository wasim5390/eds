package com.optimus.eds.ui.home;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.optimus.eds.Injection;
import com.optimus.eds.db.AppDatabase;
import com.optimus.eds.db.dao.OutletDao;
import com.optimus.eds.db.dao.RouteDao;
import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.db.entities.Route;
import com.optimus.eds.source.API;
import com.optimus.eds.source.RetrofitHelper;
import com.optimus.eds.ui.route.outlet.OutletListRepository;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class HomeViewModel extends AndroidViewModel {

    RouteDao routeDao;
    OutletDao outletDao;

    private CompositeDisposable compositeDisposable;

    private final OutletListRepository outletListRepository;
    private MutableLiveData<Boolean> isLoading;
    private MutableLiveData<List<Outlet>> outletList;
    private MutableLiveData<List<Route>> routeList;
    private MutableLiveData<String> errorMsg;
    private API api;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        outletListRepository = OutletListRepository.getInstance(application);
        AppDatabase database = AppDatabase.getDatabase(application);
        routeDao = database.routeDao();
        outletDao = database.outletDao();
        isLoading = new MutableLiveData<>();
        outletList = new MutableLiveData<>();
        routeList = new MutableLiveData<>();
        errorMsg = new MutableLiveData<>();
        this.compositeDisposable = new CompositeDisposable();




    }

    public void onScreenCreated(){
        isLoading.setValue(true);
       // Single<List<Outlet>> visitedOutlets = outletListRepository.getOutlets(123L).map(this::visitedOutlets);
        Single<List<Outlet>> todayOutlets = api.getOutlets("223");

        Single<List<Route>> todayRoutes = api.getRoutes("123");



        Disposable routesDisposable = todayRoutes
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::onSuccessRoutes, this::onError);

        Disposable outletDisposable = todayOutlets
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::onSuccessOutlets, this::onError);

        compositeDisposable.add(routesDisposable);
        compositeDisposable.add(outletDisposable);
    }


    private List<Outlet> visitedOutlets(List<Outlet> allOutlets) {
        List<Outlet> visitedOutlets = new ArrayList<>(allOutlets.size());
        for (Outlet outlet : allOutlets) {
          if(outlet.getVisitStatus()==1)
              visitedOutlets.add(outlet);
        }
        return visitedOutlets;
    }

    private void onSuccessOutlets(List<Outlet> outlets) {
        isLoading.setValue(false);
        outletList.setValue(outlets);


    }
    private void onSuccessRoutes(List<Route> routes) {
        isLoading.setValue(false);
        routeList.setValue(routes);
        saveRoutesInDb(routes);

    }
    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }

    public LiveData<Boolean> isLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMsg() {
        return errorMsg;
    }



    public LiveData<List<Outlet>> getOutlets() {
        return outletList;
    }

    public LiveData<List<Route>> getRoutes() {
        return routeList;
    }

    private void onError(Throwable throwable) {
        isLoading.setValue(false);
        errorMsg.setValue(throwable.getMessage());
    }



    public void saveRoutesInDb(List<Route> routes){
        outletListRepository.insertRoutes(routes);
    }


}
