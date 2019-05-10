package com.optimus.eds.db.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(foreignKeys = {
        @ForeignKey(
                entity = Order.class,
                parentColumns = "oid",
                childColumns = "c_oid",
                onDelete = ForeignKey.CASCADE),

        @ForeignKey(
                entity = Product.class,
                parentColumns = "pid",
                childColumns = "c_pid"
        )
}, indices = {@Index(value = "c_pid", unique = true), @Index(value = "c_oid")})

public class OrderDetail {
    @PrimaryKey
    public int id;
    @ColumnInfo(name = "c_pid")
    public Long mProductId;
    @ColumnInfo(name = "c_oid")
    public Long mOrderId;
    public Long mCartonQuantity;
    public Long mUnitQuantity;

    public OrderDetail(Long mOrderId, Long mProductId, Long mCartonQuantity, Long mUnitQuantity) {
        this.mOrderId = mOrderId;
        this.mProductId = mProductId;
        this.mCartonQuantity = mCartonQuantity;
        this.mUnitQuantity = mUnitQuantity;
    }
    public Long getOrderId() {
        return mOrderId;
    }

    public void setOrderId(Long mOrderId) {
        this.mOrderId = mOrderId;
    }

    public Long getProductId() {
        return mProductId;
    }

    public void setProductId(Long mProductId) {
        this.mProductId = mProductId;
    }

    public Long getCartonQuantity() {
        return mCartonQuantity;
    }

    public void setCartonQuantity(Long mCartonQuantity) {
        this.mCartonQuantity = mCartonQuantity;
    }

    public Long getUnitQuantity() {
        return mUnitQuantity;
    }

    public void setUnitQuantity(Long mUnitQuantity) {
        this.mUnitQuantity = mUnitQuantity;
    }



}
