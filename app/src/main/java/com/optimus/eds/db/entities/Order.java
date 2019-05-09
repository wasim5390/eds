package com.optimus.eds.db.entities;


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

    @PrimaryKey(autoGenerate = true)
    @SerializedName("orderId")
    public Long mOrderId;

    @SerializedName("outletId")
    public Long outletId;

    public Order(Long outletId, List<Product> products) {
        this.outletId = outletId;
        this.products = products;
    }

    public Long getOutletId() {
        return outletId;
    }

    public void setOutletId(Long outletId) {
        this.outletId = outletId;
    }



    @TypeConverters(ProductConverter.class)
    public List<Product> products;



    public Long getOrderId() {
        return mOrderId;
    }

    public void setOrderId(Long mOrderId) {
        this.mOrderId = mOrderId;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

}
