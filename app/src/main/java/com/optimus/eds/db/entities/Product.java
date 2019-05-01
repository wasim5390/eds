package com.optimus.eds.db.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Entity
public class Product implements Serializable {

    @PrimaryKey
    @SerializedName("productId")
    public Long id;

    @SerializedName("productPackageId")
    public Long pkgId;

    @SerializedName("productName")
    public String name;

    @Ignore
    public double qty;


    public Product(Long id, Long pkgId, String name) {
        this.id = id;
        this.pkgId = pkgId;
        this.name = name;
    }

    public Product(Long id, Long pkgId, String name, Double qty) {
        this.id = id;
        this.pkgId = pkgId;
        this.name = name;
        this.qty = qty;
    }
    public double getQty() {
        return qty;
    }

    public void setQty(double qty) {
        this.qty = qty;
    }
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }


    public Long getPkgId() {
        return pkgId;
    }

    @Override
    public String toString() {
        return String.valueOf(getQty());
    }
}
