package com.optimus.eds.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;
import android.arch.persistence.room.Update;
import android.icu.text.Replaceable;

import com.optimus.eds.db.entities.Order;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;


@Dao
public interface OrderDao {

    @Transaction @Query("SELECT * FROM `Order` WHERE mOrderId=:id")
     LiveData<Order> findOrderByOrderId(Long id);

    @Insert(onConflict = REPLACE)
    void insertOrder(Order order);

    @Update
    void updateOrder(Order order);

}
