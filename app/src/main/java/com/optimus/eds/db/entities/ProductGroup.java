package com.optimus.eds.db.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity
public class ProductGroup {

    @SerializedName("productGroupId") @PrimaryKey
    public Long productGroupId;

    @SerializedName("productGroupName")
    public String productGroupName;


    public Long getProductGroupId() {
        return productGroupId;
    }

    public String getProductGroupName() {
        return productGroupName;
    }

    @Override
    public String toString() {
        return getProductGroupName();
    }
}
