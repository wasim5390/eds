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
    @SerializedName("productName")
    public String name;
    @SerializedName("productDescription")
    public String productDescription;
    @SerializedName("productCode")
    public String productCode;
    @SerializedName("productGroupId")
    public Long productGroupId;
    @SerializedName("productGroupName")
    public String productGroupName;
    @SerializedName("productPackageId")
    public Long pkgId;
    @SerializedName("packageName")
    public String packageName;
    @SerializedName("productBrandId")
    public Long productBrandId;
    @SerializedName("brandName")
    public String brandName;
    @SerializedName("productFlavorId")
    public Long productFlavorId;
    @SerializedName("flavorName")
    public String flavorName;
    @SerializedName("unitCode")
    public String unitCode;
    @SerializedName("cartonCode")
    public String cartonCode;
    @SerializedName("unitQuantity")
    public Integer unitQuantity;
    @SerializedName("cartonQuantity")
    public Integer cartonQuantity;
    @SerializedName("unitSizeForDisplay")
    public String unitSizeForDisplay;
    @SerializedName("cartonSizeForDisplay")
    public String cartonSizeForDisplay;
    @SerializedName("unitStockInHand")
    public Long unitStockInHand;
    @SerializedName("cartonStockInHand")
    public Long cartonStockInHand;


    @Ignore
    public double qtyCarton,qtyUnit;


    public Product(Long id, Long pkgId, String name) {
        this.id = id;
        this.pkgId = pkgId;
        this.name = name;
    }

    public Product(Long id, Long pkgId, String name, Double qtyUnit) {
        this.id = id;
        this.pkgId = pkgId;
        this.name = name;
        this.qtyUnit = qtyUnit;
    }
    public double getQtyUnit() {
        return qtyUnit;
    }

    public double getQtyCarton() {
        return qtyCarton;
    }

    public void setQty(double carton,double unit) {
        this.qtyCarton = carton;
        this.qtyUnit = unit;
    }

    public void setUnit(double unit) {
        this.qtyUnit = unit;
    }

    public void setCarton(double carton) {
        this.qtyCarton = carton;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getProductDescription() {
        return productDescription;
    }
    public Long getPkgId() {
        return pkgId;
    }

    public String getProductCode() {
        return productCode;
    }

    public Long getProductGroupId() {
        return productGroupId;
    }

    public String getProductGroupName() {
        return productGroupName;
    }

    public String getPackageName() {
        return packageName;
    }

    public Long getProductBrandId() {
        return productBrandId;
    }

    public String getBrandName() {
        return brandName;
    }

    public Long getProductFlavorId() {
        return productFlavorId;
    }

    public String getFlavorName() {
        return flavorName;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public String getCartonCode() {
        return cartonCode;
    }

    public Integer getUnitQuantity() {
        return unitQuantity;
    }

    public Integer getCartonQuantity() {
        return cartonQuantity;
    }

    public String getUnitSizeForDisplay() {
        return unitSizeForDisplay;
    }

    public String getCartonSizeForDisplay() {
        return cartonSizeForDisplay;
    }

    public Long getUnitStockInHand() {
        return unitStockInHand;
    }

    public Long getCartonStockInHand() {
        return cartonStockInHand;
    }

    @Override
    public String toString() {
        return String.valueOf(getQtyCarton());
    }
}
