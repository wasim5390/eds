package com.optimus.eds.ui.route.outlet;

import android.app.Application;
import androidx.lifecycle.LiveData;
import io.reactivex.Maybe;

import com.optimus.eds.db.AppDatabase;

import com.optimus.eds.db.dao.RouteDao;
import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.db.entities.Route;
import com.optimus.eds.source.API;
import com.optimus.eds.source.RetrofitHelper;
import com.optimus.eds.ui.order.OrderBookingRepository;

import java.util.List;

public class OutletListRepository  extends OrderBookingRepository {

    public List<Outlet> outletList;
    private RouteDao routeDao;

    static OutletListRepository repository;

    public static OutletListRepository getInstance(Application application){
        if(repository==null)
           repository =  new OutletListRepository(application);
        return repository;
    }

    public OutletListRepository(Application application){
        super(application);
        routeDao = AppDatabase.getDatabase(application).routeDao();
    }



    public Maybe<List<Outlet>> getOutlets(Long routeId){
        return routeDao.findAllOutletsForRoute(routeId);
    }
    public LiveData<List<Route>> getRoutes(){
        return routeDao.findAllRoutes();
    }





}
