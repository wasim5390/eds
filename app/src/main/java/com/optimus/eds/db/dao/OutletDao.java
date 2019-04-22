package com.optimus.eds.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.optimus.eds.db.entities.Outlet;


import java.util.List;

import io.reactivex.Single;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;
import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface OutletDao {

    @Query("SELECT * FROM Outlet ORDER BY mOutletName ASC")
    LiveData<List<Outlet>> findAllOutlets();

    @Query("SELECT * FROM Outlet WHERE mRouteId=:routeId")
    Single<List<Outlet>> findAllOutletsForRoute(Long routeId);

    @Query("SELECT * FROM Outlet WHERE mOutletId=:id")
    LiveData<Outlet> findOutletById(Long id);


    @Insert(onConflict = REPLACE)
    long insertOutlet(Outlet outlet);

    @Insert(onConflict = REPLACE)
    void insertOutlets(List<Outlet> outlets);

    @Update
    int updateOutlet(Outlet outlet);

    @Update
    void updateOutlet(List<Outlet> outlets);

    @Delete
    void deleteOutlet(Outlet outlet);

    @Query("DELETE FROM Outlet")
    void deleteAll();
}
