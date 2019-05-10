package com.optimus.eds.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;
import android.arch.persistence.room.Update;
import android.icu.text.Replaceable;

import com.optimus.eds.db.entities.Order;
import com.optimus.eds.db.entities.OrderDetail;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;


@Dao
public interface OrderDao {

    @Query("SELECT * FROM `Order` WHERE oid=:id")
    Order findOrderByOrderId(Long id);

    @Query("SELECT * FROM `Order` WHERE outletId=:id")
    Order findOrderByOutletId(Long id);

    @Query("SELECT * FROM OrderDetail where c_oid=:orderId")
    List<OrderDetail> findOrderItemsByOrderId(Long orderId);

    @Insert(onConflict = REPLACE)
    void insertOrder(Order order);

    @Insert(onConflict = REPLACE)
    void insertOrderItems(List<OrderDetail> orderItems);

    @Update
    void updateOrder(Order order);

    @Update
    void updateOrderItems(List<OrderDetail> orderItems);

}
