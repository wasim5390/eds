package com.optimus.eds.db.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.Observable;

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
    public Long mLocalOrderId;

    @NonNull @ColumnInfo(name = "orderId")
    @SerializedName("orderId")
    public Long mOrderId;

    @SerializedName("unitOrderDetailId")
    public Long mUnitOrderDetailId;
    @SerializedName("cartonOrderDetailId")
    public Long mCartonOrderDetailId;
    @SerializedName("productGroupId")
    public Long mProductGroupId;

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

    @SerializedName("unitPrice")
    public Double unitPrice;
    @SerializedName("cartonPrice")
    public Double cartonPrice;
    @SerializedName("unitTotalPrice")
    public Double unitTotalPrice;
    @SerializedName("cartonTotalPrice")
    public Double cartonTotalPrice;

    @SerializedName("totalPrice")
    public Double total;
    @SerializedName("type")
    public String type; // either paid or free product

    @SerializedName("freeQuantityTypeId")
    public Integer freeQuantityTypeId; // 1. Primary ; 2. Optional {If Optional ask for quantity}
    @SerializedName("freeGoodQuantity")
    public Integer freeGoodQuantity; //Only applicable for Optional

    @Ignore
    public Long parentId; //In case of FOC, server will send the FOC row with parentId

    public OrderDetail(@NonNull Long mOrderId,@NonNull Long mProductId, Long mCartonQuantity, Long mUnitQuantity) {
        this.mLocalOrderId = mOrderId;
        this.mProductId = mProductId;
        this.mCartonQuantity = mCartonQuantity;
        this.mUnitQuantity = mUnitQuantity;
    }
    public void setLocalOrderId(Long mLocalOrderId){
        this.mLocalOrderId = mLocalOrderId;
    }
    public Long getLocalOrderId() {
        return mLocalOrderId;
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

    public Long getProductGroupId() {
        return mProductGroupId;
    }

    public void setProductGroupId(Long mProductGroupId) {
        this.mProductGroupId = mProductGroupId;
    }

    public Long getUnitOrderDetailId() {
        return mUnitOrderDetailId;
    }

    public void setUnitOrderDetailId(Long mUnitOrderDetailId) {
        this.mUnitOrderDetailId = mUnitOrderDetailId;
    }

    public Long getCartonOrderDetailId() {
        return mCartonOrderDetailId;
    }

    public void setCartonOrderDetailId(Long mCartonOrderDetailId) {
        this.mCartonOrderDetailId = mCartonOrderDetailId;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Double getCartonPrice() {
        return cartonPrice;
    }

    public void setCartonPrice(Double cartonPrice) {
        this.cartonPrice = cartonPrice;
    }

    public Double getUnitTotalPrice() {
        return unitTotalPrice;
    }

    public void setUnitTotalPrice(Double unitTotalPrice) {
        this.unitTotalPrice = unitTotalPrice;
    }

    public Double getCartonTotalPrice() {
        return cartonTotalPrice;
    }

    public void setCartonTotalPrice(Double cartonTotalPrice) {
        this.cartonTotalPrice = cartonTotalPrice;
    }
    public Integer getFreeQuantityTypeId() {
        return freeQuantityTypeId;
    }

    public void setFreeQuantityTypeId(Integer freeQuantityTypeId) {
        this.freeQuantityTypeId = freeQuantityTypeId;
    }

    public Integer getFreeGoodQuantity() {
        return freeGoodQuantity;
    }

    public void setFreeGoodQuantity(Integer freeGoodQuantity) {
        this.freeGoodQuantity = freeGoodQuantity;
    }

}
