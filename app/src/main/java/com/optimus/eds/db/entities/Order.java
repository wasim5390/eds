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

    public Order( String userId, List<Product> products) {
        this.userId = userId;
        this.products = products;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @SerializedName("userId")
    public String userId;

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
