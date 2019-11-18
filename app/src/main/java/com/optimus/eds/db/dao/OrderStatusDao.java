package com.optimus.eds.db.dao;

import com.optimus.eds.db.entities.OrderStatus;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import io.reactivex.Maybe;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface OrderStatusDao {

    @Query("SELECT * FROM OrderStatus WHERE outletId=:id")
    Maybe<OrderStatus> findOutletOrderStatus(Long id);

    @Insert(onConflict = REPLACE)
    void insertStatus(OrderStatus status);

    @Query("Update OrderStatus Set status=:status, sync=:sync,orderAmount=:amount WHERE outletId=:outletId")
    void updateStatus(Integer status, Long outletId,int sync,Double amount);

    @Query("DELETE FROM OrderStatus")
    void deleteAllStatus();

}
