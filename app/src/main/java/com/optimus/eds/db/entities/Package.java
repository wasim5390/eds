package com.optimus.eds.db.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;


@Entity
public class Package {

    @PrimaryKey
    Long packageId;
    @SerializedName("PackageName")
    String packageName;


    public Package(Long packageId, String packageName) {
        this.packageId = packageId;
        this.packageName = packageName;
    }

    public Long getPackageId() {
        return packageId;
    }

    public String getPackageName() {
        return packageName;
    }


}