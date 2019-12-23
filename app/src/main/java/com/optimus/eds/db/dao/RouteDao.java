package com.optimus.eds.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.optimus.eds.db.entities.Asset;
import com.optimus.eds.db.entities.OrderStatus;
import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.db.entities.OutletOrderStatus;
import com.optimus.eds.db.entities.Route;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;

import static androidx.room.OnConflictStrategy.IGNORE;
import static androidx.room.OnConflictStrategy.REPLACE;


@Dao
public interface RouteDao extends MerchandiseDao{

    @Query("SELECT * FROM Route ORDER BY mRouteName ASC")
    LiveData<List<Route>> findAllRoutes();

    @Query("SELECT * FROM Route")
    Single<List<Route>> getAllRoutes();

    @Query("SELECT * FROM Route WHERE routeId=:id")
    LiveData<Route> findRouteById(Long id);

    @Query("SELECT * FROM Outlet ORDER BY mOutletName ASC")
    LiveData<List<Outlet>> findAllOutlets();

    @Query("SELECT * FROM Outlet WHERE mRouteId=:routeId AND planned=:planned")
    Maybe<List<Outlet>> findAllOutletsForRoute(Long routeId,int planned);

    @Query("SELECT * FROM Outlet WHERE planned=:planned AND mVisitStatus<=1")
    Flowable<List<Outlet>> findOutletsWithPendingTasks( int planned);


    @Query("SELECT * FROM Outlet WHERE mOutletId in (:outlets)")
    Single<List<Outlet>> findOutletsWithPendingOrderToSync(List<Long> outlets);

    @Query("SELECT * FROM OrderStatus WHERE sync=:synced AND status between 2 AND 7 ")
    Flowable<List<OrderStatus>> findOutletsWithPendingOrderToSync(int synced);

    @Query("SELECT COUNT() FROM Outlet WHERE planned=1")
    int getPjpCount();

    @Query("SELECT Outlet.*, OrderStatus.* FROM Outlet INNER JOIN OrderStatus ON Outlet.mOutletId = OrderStatus.outletId" +
            " WHERE Outlet.planned=1 AND OrderStatus.status between 2 AND 8" )
    List<OutletOrderStatus> getVisitedOutletCount();

   /* @Query("SELECT COUNT(*) FROM Outlet WHERE planned=1 AND mVisitStatus between 2 AND 8 ")
    List<OutletOrderStatus> getVisitedOutletCount();*/

    @Query("SELECT Outlet.*, OrderStatus.* FROM Outlet INNER JOIN OrderStatus ON Outlet.mOutletId = OrderStatus.outletId" +
            " WHERE Outlet.planned=1 AND OrderStatus.status >=7" )
    List<OutletOrderStatus> getProductiveOutletCount();

    @Query("SELECT * FROM Outlet WHERE mOutletId=:id")
    LiveData<Outlet> findOutletById(Long id);


    @Insert(onConflict = REPLACE)
    long insertRoute(Route route);

    @Insert(onConflict = REPLACE)
    void insertRoutes(List<Route> routes);

    @Update
    int updateRoute(Route route);

    @Update
    void updateRoute(List<Route> routes);

    @Delete
    void deleteRoute(Route route);

    @Insert(onConflict = REPLACE)
    long insertOutlet(Outlet outlet);

    @Insert(onConflict = IGNORE)
    void insertOutlets(List<Outlet> outlets);

    @Insert(onConflict = REPLACE)
    void insertAssets(List<Asset> assets);

    @Update
    int updateOutlet(Outlet outlet);

    @Query("Update Outlet SET mVisitStatus=:status, synced=:sync where mOutletId=:outletId")
    int updateOutletVisitStatus(Long outletId,Integer status,Integer sync);

    @Query("Update Outlet SET mobileNumber=:mobile, cnic=:cnic, strn=:strn where mOutletId=:outletId")
    int updateOutletCnic(Long outletId,String mobile,String cnic,String strn);

    @Update
    void updateOutlet(List<Outlet> outlets);

    @Delete
    void deleteOutlet(Outlet outlet);

    @Query("DELETE FROM Outlet")
    void deleteAllOutlets();

    @Query("DELETE FROM Route")
    void deleteAllRoutes();


}
