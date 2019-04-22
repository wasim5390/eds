package com.optimus.eds.ui.route.outlet;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import com.optimus.eds.Injection;
import com.optimus.eds.db.AppDatabase;
import com.optimus.eds.db.dao.OutletDao;
import com.optimus.eds.db.dao.RouteDao;
import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.db.entities.Route;
import com.optimus.eds.source.API;
import com.optimus.eds.source.RetrofitHelper;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class OutletListRepository  {

    public List<Outlet> outletList;
    private OutletDao outletDao;
    private RouteDao routeDao;
    private API api;
    static OutletListRepository repository;

    public static OutletListRepository getInstance(Application application){
        if(repository==null)
           repository =  new OutletListRepository(application);
        return repository;
    }

    public OutletListRepository(Application application){
        outletDao = AppDatabase.getDatabase(application).outletDao();
        routeDao = AppDatabase.getDatabase(application).routeDao();
        api= RetrofitHelper.getInstance().getApi();
    }



    public Single<List<Outlet>> getOutlets(Long routeId){
        return outletDao.findAllOutletsForRoute(routeId);
    }
    public LiveData<List<Route>> getRoutes(){
        return routeDao.findAllRoutes();
    }


    public void insertOutlets(List<Outlet> outlets){
        Completable.create(e -> {
            outletDao.insertOutlets(outlets);
            e.onComplete();
        }).subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });

    }


    public void insertRoutes(List<Route> routes){
        Completable.create(e -> {
            routeDao.insertRoutes(routes);
            e.onComplete();
        }).subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });

    }
}
