package com.optimus.eds.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.optimus.eds.db.entities.Asset;
import com.optimus.eds.db.entities.Merchandise;

import java.util.List;

import io.reactivex.Single;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Created By apple on 4/23/19
 */

@Dao
public interface MerchandiseDao {


    @Query("SELECT * FROM Merchandise WHERE outletId=:outletId")
    LiveData<List<Merchandise>> findMerchandiseByOutletId(Long outletId);


    @Insert(onConflict = REPLACE)
    void insertMerchandise(Merchandise merchandise);

    @Insert(onConflict = REPLACE)
    void insertMerchandise(List<Merchandise> merchandises);

    @Query("SELECT * FROM Asset WHERE outletId=:outletId")
    Single<List<Asset>> findAllAssetsForOutlet(Long outletId);

    @Update
    int updateMerchandise(Merchandise merchandise);

    @Update
    void updateMerchandise(List<Merchandise> merchandises);

    @Update
    void updateAsset(Asset asset);


    @Query("DELETE FROM Asset")
    void deleteAllAssets();
}
