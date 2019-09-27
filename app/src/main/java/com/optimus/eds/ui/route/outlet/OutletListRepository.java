package com.optimus.eds.ui.route.outlet;

import android.app.Application;
import androidx.lifecycle.LiveData;
import io.reactivex.Flowable;
import io.reactivex.Maybe;

import com.optimus.eds.Constant;
import com.optimus.eds.db.AppDatabase;

import com.optimus.eds.db.dao.RouteDao;
import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.db.entities.Route;
import com.optimus.eds.ui.order.OrderBookingRepository;

import java.util.List;

public class OutletListRepository  extends OrderBookingRepository {

    private final RouteDao routeDao;

    private static OutletListRepository repository;

    public static OutletListRepository getInstance(Application application){
        if(repository==null)
            repository =  new OutletListRepository(application);
        return repository;
    }

    private OutletListRepository(Application application){
        super(application);
        routeDao = AppDatabase.getDatabase(application).routeDao();
    }



    public Maybe<List<Outlet>> getOutlets(Long routeId,int outletType){
        //1 for get All planned outlet calls
        return routeDao.findAllOutletsForRoute(routeId,outletType);
    }

    public Flowable<List<Outlet>> getOutletsWithNoVisits(){
        // get All planned outlet calls
        return routeDao.findOutletsWithPendingTasks(1);
    }

    public Flowable<List<Outlet>> getUnsyncedOutlets(){
        // get All planned outlet calls
        return routeDao.findOutletsWithPendingOrderToSync(false);
    }

    public LiveData<List<Route>> getRoutes(){
        return routeDao.findAllRoutes();
    }

    public int getPjpCount() {
        return routeDao.getPjpCount(1);
    }

    public int getCompletedCount() {
        return routeDao.getVisitedOutletCount();
    }
    public int getProductiveCount() {
        return routeDao.getProductiveOutletCount();
    }


}
