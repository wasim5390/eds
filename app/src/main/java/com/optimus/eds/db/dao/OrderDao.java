package com.optimus.eds.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.optimus.eds.db.entities.CartonPriceBreakDown;
import com.optimus.eds.db.entities.Order;
import com.optimus.eds.db.entities.OrderDetail;
import com.optimus.eds.db.entities.UnitPriceBreakDown;
import com.optimus.eds.model.OrderModel;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Single;

import static androidx.room.OnConflictStrategy.REPLACE;


@Dao
public interface OrderDao {

    @Query("SELECT * FROM `Order` WHERE oid=:id")
    Order findOrderByOrderId(Long id);

    @Query("SELECT * FROM `Order` WHERE c_outletId=:id")
    Single<Order> findOrderByOutletId(Long id);

    @Query("SELECT * FROM OrderDetail where fk_oid=:orderId")
    Single<List<OrderDetail>> findOrderItemsByOrderId(Long orderId);

    @Query("SELECT * FROM `Order`,Outlet" +
            " where c_outletId=:outletId AND mOutletId=:outletId")
    @Transaction
    Maybe<OrderModel> getOrderWithItems(long outletId);


    @Insert(onConflict = REPLACE)
    void insertOrder(Order order);

    @Insert(onConflict = REPLACE)
    void insertOrderItems(List<OrderDetail> orderItems);

    @Update
    void updateOrder(Order order);

    @Update
    void updateOrderItems(List<OrderDetail> orderItems);

    @Insert(onConflict = REPLACE)
    void insertCartonPriceBreakDown(List<CartonPriceBreakDown> priceBreakDowns);

    @Insert(onConflict = REPLACE)
    void insertUnitPriceBreakDown(List<UnitPriceBreakDown> priceBreakDowns);

    @Query("Delete From 'Order'")
    void deleteAllOrders();

    @Query("Delete From 'OrderDetail' WHERE fk_oid=:orderId AND mProductGroupId=:groupId")
    void deleteOrderItemsByGroup(Long orderId,Long groupId);

    @Query("Delete From 'OrderDetail' WHERE fk_oid=:orderId")
    void deleteOrderItems(Long orderId);


}
