package com.optimus.eds.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.db.entities.Route;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;
import static android.arch.persistence.room.OnConflictStrategy.REPLACE;


@Dao
public interface RouteDao {
    @Query("SELECT * FROM Route ORDER BY mRouteName ASC")
    LiveData<List<Route>> findAllRoutes();

    @Query("SELECT * FROM Route")
    Single<List<Route>> getAllRoutes();

    @Query("SELECT * FROM Route WHERE mRouteId=:id")
    LiveData<Route> findRouteById(Long id);


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

    @Query("DELETE FROM Route")
    void deleteAll();
}
