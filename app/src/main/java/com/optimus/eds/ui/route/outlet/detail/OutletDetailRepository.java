package com.optimus.eds.ui.route.outlet.detail;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import com.optimus.eds.db.AppDatabase;

import com.optimus.eds.db.dao.RouteDao;
import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.db.entities.Route;

import java.util.List;

import io.reactivex.Single;


public class OutletDetailRepository {
    private RouteDao routeDao;

    private Single<List<Route>> allRoutes;

    public OutletDetailRepository(Application application) {
        AppDatabase appDatabase = AppDatabase.getDatabase(application);

        routeDao = appDatabase.routeDao();
        allRoutes = routeDao.getAllRoutes();
    }


    public LiveData<Route> getRouteById(Long routeId){
        return routeDao.findRouteById(routeId);
    }

    public LiveData<Outlet> getOutletById(Long outletId){
        return routeDao.findOutletById(outletId);
    }

    public Single<List<Route>> getAllRoutes(){
        return allRoutes;
    }


}
