package com.optimus.eds.ui.route.outlet;

import android.app.Application;
import androidx.lifecycle.LiveData;

import com.optimus.eds.db.AppDatabase;

import com.optimus.eds.db.dao.RouteDao;
import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.db.entities.Route;
import com.optimus.eds.source.API;
import com.optimus.eds.source.RetrofitHelper;

import java.util.List;

public class OutletListRepository  {

    public List<Outlet> outletList;
    private RouteDao routeDao;
    private API api;
    static OutletListRepository repository;

    public static OutletListRepository getInstance(Application application){
        if(repository==null)
           repository =  new OutletListRepository(application);
        return repository;
    }

    public OutletListRepository(Application application){
        routeDao = AppDatabase.getDatabase(application).routeDao();
        api= RetrofitHelper.getInstance().getApi();
    }



    public LiveData<List<Outlet>> getOutlets(Long routeId){
        return routeDao.findAllOutletsForRoute(routeId);
    }
    public LiveData<List<Route>> getRoutes(){
        return routeDao.findAllRoutes();
    }



}
