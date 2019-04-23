package com.optimus.eds.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.optimus.eds.db.entities.Merchandise;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Created By apple on 4/23/19
 */

@Dao
public interface MerchandiseDao {


    @Query("SELECT * FROM Merchandise WHERE mOutletId=:outletId")
    LiveData<List<Merchandise>> findMerchandiseByOutletId(Long outletId);


    @Insert(onConflict = REPLACE)
    void insertMerchandise(Merchandise merchandise);

    @Insert(onConflict = REPLACE)
    void insertMerchandise(List<Merchandise> merchandises);

    @Update
    int updateOutlet(Merchandise merchandise);

    @Update
    void updateOutlet(List<Merchandise> merchandises);

    @Delete
    void deleteOutlet(Merchandise outlet);

    @Query("DELETE FROM Outlet")
    void deleteAll();
}
