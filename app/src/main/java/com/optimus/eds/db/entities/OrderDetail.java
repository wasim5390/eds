package com.optimus.eds.db.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

@Entity(
        primaryKeys = {"c_pid","c_oid"},
        foreignKeys = {
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
}, indices = {@Index(value = "c_pid"), @Index(value = "c_oid")})

public class OrderDetail {

    @NonNull @ColumnInfo(name = "c_pid")
    @SerializedName("productId")
    public Long mProductId;
    @NonNull @ColumnInfo(name = "c_oid")
    @SerializedName("mobileOrderId")
    public Long mOrderId;
    @SerializedName("orderDetailId")
    public Long orderDetailId;
    @SerializedName("productName")
    public String mProductName;

    @SerializedName("cartonQuantity")
    public Long mCartonQuantity;
    @SerializedName("unitQuantity")
    public Long mUnitQuantity;
    @SerializedName("cartonCode")
    public String mCartonCode;
    @SerializedName("unitCode")
    public String mUnitCode;
    @SerializedName("totalPrice")
    public Double total;
    @SerializedName("type")
    public String type; // either paid or free product

    @Ignore
    public Long parentId; //In case of FOC, server will send the FOC row with parentId

    public OrderDetail(@NonNull Long mOrderId,@NonNull Long mProductId, Long mCartonQuantity, Long mUnitQuantity) {
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

    public Long getOrderDetailId() {
        return orderDetailId;
    }

    public void setOrderDetailId(Long orderDetailId) {
        this.orderDetailId = orderDetailId;
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

    public String getCartonCode() {
        return mCartonCode;
    }

    public void setCartonCode(String mCartonCode) {
        this.mCartonCode = mCartonCode;
    }

    public String getUnitCode() {
        return mUnitCode;
    }

    public void setUnitCode(String mUnitCode) {
        this.mUnitCode = mUnitCode;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProductName() {
        return mProductName;
    }

    public void setProductName(String mProductName) {
        this.mProductName = mProductName;
    }


}
