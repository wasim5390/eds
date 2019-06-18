package com.optimus.eds.ui.route.outlet.detail;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;

import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.db.entities.Route;

import java.util.List;


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
