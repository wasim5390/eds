package com.optimus.eds.db.entities;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.optimus.eds.db.converters.ProductConverter;

import java.util.List;

@Entity (foreignKeys =
@ForeignKey(
        entity = Outlet.class,
        parentColumns = "mOutletId",
        childColumns = "c_outletId",
        onDelete = ForeignKey.CASCADE), indices = {@Index(value = "c_outletId")})

public class Order {

    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "pk_oid")
    @SerializedName("mobileOrderId")
    public Long id;
    @SerializedName("orderId")
    public Long serverOrderId;

    @SerializedName("outletId")
    @ColumnInfo(name = "c_outletId")
    public Long outletId;
    @SerializedName("routeId")
    public Long routeId;

    @SerializedName("code")
    public String code;

    @SerializedName("orderStatusId")
    public int orderStatus; // {1. Confirmed, 2. Created, 3. Cancelled}

    @SerializedName("visitDayId")
    public int visitDayId;

    @SerializedName("latitude")
    public double latitude;
    @SerializedName("longitude")
    public double longitude;

    @SerializedName("subTotal")
    public Double subTotal;
    @SerializedName("payable")
    public Double payable;

    @SerializedName("orderDate")
    public Long orderDate;
    @SerializedName("deliveryDate")
    public Long deliveryDate;


    public Order(Long outletId) {
        this.outletId = outletId;
    }

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }
    public Long getOutletId() {
        return outletId;
    }

    public void setOutletId(Long outletId) {
        this.outletId = outletId;
    }

    public Long getLocalOrderId() {
        return id;
    }

    public void setLocalOrderId(Long mOrderId) {
        this.id = mOrderId;
    }

    public Long getOrderId() {
        return serverOrderId;
    }

    public void setOrderId(Long serverOrderId) {
        this.serverOrderId = serverOrderId;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    public int getVisitDayId() {
        return visitDayId;
    }

    public void setVisitDayId(int visitDayId) {
        this.visitDayId = visitDayId;
    }
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    public Double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(Double subTotal) {
        this.subTotal = subTotal;
    }

    public Double getPayable() {
        return payable;
    }

    public void setPayable(Double payable) {
        this.payable = payable;
    }


    public Long getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Long orderDate) {
        this.orderDate = orderDate;
    }

    public Long getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Long deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
