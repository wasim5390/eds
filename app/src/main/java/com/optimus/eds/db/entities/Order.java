package com.optimus.eds.db.entities;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.google.gson.annotations.SerializedName;
import com.optimus.eds.db.converters.ProductConverter;

import java.util.List;

@Entity
public class Order {

    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "oid")
    @SerializedName("orderId")
    public Long id;
    @SerializedName("outletId")
    public Long outletId;

    public int orderStatus;

    public Order(Long outletId) {
        this.outletId = outletId;
    }

    public Long getOutletId() {
        return outletId;
    }

    public void setOutletId(Long outletId) {
        this.outletId = outletId;
    }

    public Long getOrderId() {
        return id;
    }

    public void setOrderId(Long mOrderId) {
        this.id = mOrderId;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }


}
