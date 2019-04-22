package com.optimus.eds.ui.route.outlet.detail;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.db.entities.Route;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class OutletDetailViewModel extends AndroidViewModel {

    private OutletDetailRepository repository;
    private LiveData<Outlet> outletLiveData;

    private int outletStatus=1;

    public OutletDetailViewModel(@NonNull Application application) {
        super(application);
        repository = new OutletDetailRepository(application);

    }


    public LiveData<Route> fetchRoute(Long routeId){
       return repository.getRouteById(routeId);
    }

    public LiveData<Outlet> findOutlet(Long outletId){
       return repository.getOutletById(outletId);
    }

    public Outlet findOutlet(Long outletId,List<Outlet> outlets){
        for(Outlet outlet:outlets){
            if(outlet.getOutletId().equals(outletId))
                return outlet;
        }
        return null;
    }


}
