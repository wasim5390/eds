package com.optimus.eds.db.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Entity
public class Product implements Serializable {

    @PrimaryKey
    @SerializedName("productId")
    Long id;
    @SerializedName("packageId")
    Long pkgId;
    @SerializedName("productName")
    String name;

    public Product(Long id, Long pkgId, String name) {
        this.id = id;
        this.pkgId = pkgId;
        this.name = name;
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
}
