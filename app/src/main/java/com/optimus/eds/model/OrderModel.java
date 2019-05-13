package com.optimus.eds.model;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import com.optimus.eds.db.entities.Order;
import com.optimus.eds.db.entities.OrderDetail;

import java.util.List;

public class OrderModel {

    @Embedded
    public Order order;
    @Relation(parentColumn = "oid", entityColumn = "c_oid")
    List<OrderDetail> orderDetails;


    public List<OrderDetail> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }


}
