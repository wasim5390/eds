package com.optimus.eds.db.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.List;


@Entity(
       /* primaryKeys = {"fk_pid","fk_oid"},*/
        foreignKeys = {
        @ForeignKey(
                entity = Order.class,
                parentColumns = "pk_oid",
                childColumns = "fk_oid",
                onDelete = ForeignKey.CASCADE),

       /* @ForeignKey(
                entity = Product.class,
                parentColumns = "pk_pid",
                childColumns = "fk_pid"
        )*/
}, indices = { @Index(value = "fk_oid")
        ,@Index(unique = true,value = "cartonOrderDetailId")
        ,@Index(unique = true,value = "unitOrderDetailId")})

public class OrderDetail {

    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "pk_modid")
    @SerializedName("mobileOrderDetailId")
    public Long orderDetailId;

    /*@NonNull @ColumnInfo(name = "fk_pid")*/
    @SerializedName("productId")
    public Long mProductId;

    @NonNull @ColumnInfo(name = "fk_oid")
    @SerializedName("mobileOrderId")
    public Long mLocalOrderId;

    @ColumnInfo(name = "orderId")
    @SerializedName("orderId")
    public Long mOrderId;

    @SerializedName("unitOrderDetailId")
    @ColumnInfo(name = "unitOrderDetailId")
    public Long mUnitOrderDetailId;
    @SerializedName("cartonOrderDetailId")
    @ColumnInfo(name = "cartonOrderDetailId")
    public Long mCartonOrderDetailId;
    @SerializedName("productGroupId")
    public Long mProductGroupId;

    @SerializedName("productName")
    public String mProductName;
    @SerializedName("cartonQuantity")
    public Long mCartonQuantity;
    @SerializedName("unitQuantity")
    public Long mUnitQuantity;

    @SerializedName("avlUnitQuantity")
    public Long avlUnitQuantity;
    @SerializedName("avlCartonQuantity")
    public Long avlCartonQuantity;

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

    @SerializedName("payable")
    public Double total;
    @SerializedName("subtotal")
    public Double subtotal;
    @SerializedName("type")
    public String type; // either paid or free product

    @SerializedName("unitFreeQuantityTypeId")
    public Integer unitFreeQuantityTypeId; // 1. Primary ; 2. Optional {If Optional ask for quantity}

    @SerializedName("cartonFreeQuantityTypeId")
    public Integer cartonFreeQuantityTypeId; // 1. Primary ; 2. Optional {If Optional ask for quantity}

    @SerializedName("unitFreeGoodQuantity")
    public Integer unitFreeGoodQuantity; //Only applicable for Optional

    @SerializedName("cartonFreeGoodQuantity")
    public Integer cartonFreeGoodQuantity; //Only applicable for Optional

    @SerializedName("unitSelectedFreeGoodQuantity")
    public Integer unitSelectedFreeGoodQuantity;

    @SerializedName("cartonSelectedFreeGoodQuantity")
    public Integer cartonSelectedFreeGoodQuantity;

    @Ignore
    public Long parentId; //In case of FOC, server will send the FOC row with parentId

    @SerializedName("cartonPriceBreakDown")
    @Ignore
    public List<CartonPriceBreakDown> cartonPriceBreakDown;

    @SerializedName("unitPriceBreakDown")
    @Ignore
    public List<UnitPriceBreakDown> unitPriceBreakDown;

    @Ignore
    @SerializedName("cartonFreeGoods")
    public List<OrderDetail> cartonFreeGoods;
    @Ignore
    @SerializedName("unitFreeGoods")
    public List<OrderDetail> unitFreeGoods;



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
    public Long getMobileOrderDetailId() {
        return orderDetailId;
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


    public Long getAvlUnitQuantity() {
        return avlUnitQuantity;
    }

    public Long getAvlCartonQuantity() {
        return avlCartonQuantity;
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
    public Integer getUnitFreeQuantityTypeId() {
        return unitFreeQuantityTypeId;
    }

    public void setUnitFreeQuantityTypeId(Integer unitFreeQuantityTypeId) {
        this.unitFreeQuantityTypeId = unitFreeQuantityTypeId;
    }

    public Integer getCartonFreeQuantityTypeId() {
        return cartonFreeQuantityTypeId;
    }

    public void setCartonFreeQuantityTypeId(Integer cartonFreeQuantityTypeId) {
        this.cartonFreeQuantityTypeId = cartonFreeQuantityTypeId;
    }

    public Integer getUnitFreeGoodQuantity() {
        return unitFreeGoodQuantity;
    }

    public void setUnitFreeGoodQuantity(Integer freeGoodQuantity) {
        this.unitFreeGoodQuantity = freeGoodQuantity;
    }

    public Integer getCartonFreeGoodQuantity() {
        return cartonFreeGoodQuantity;
    }

    public void setCartonFreeGoodQuantity(Integer freeGoodQuantity) {
        this.cartonFreeGoodQuantity = freeGoodQuantity;
    }

    public Integer getSelectedCartonFreeGoodQuantity() {
        return cartonSelectedFreeGoodQuantity;
    }

    public void setSelectedCartonFreeGoodQuantity(Integer freeGoodQuantity) {
        this.cartonSelectedFreeGoodQuantity = freeGoodQuantity;
    }

    public Integer getSelectedUnitFreeGoodQuantity() {
        return unitSelectedFreeGoodQuantity;
    }

    public void setSelectedUnitFreeGoodQuantity(Integer freeGoodQuantity) {
        this.unitSelectedFreeGoodQuantity = freeGoodQuantity;
    }

    public Double getSubtotal() {
        return subtotal;
    }
    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }


    public void setAvlQty(Long avlCartonQuantity,Long avlUnitQuantity){
        this.avlCartonQuantity=avlCartonQuantity;
        this.avlUnitQuantity = avlUnitQuantity;
    }

    public List<CartonPriceBreakDown> getCartonPriceBreakDown() {
        return cartonPriceBreakDown;
    }

    public List<UnitPriceBreakDown> getUnitPriceBreakDown() {
        return unitPriceBreakDown;
    }

    public List<OrderDetail> getCartonFreeGoods() {
        return cartonFreeGoods;
    }

    public List<OrderDetail> getUnitFreeGoods() {
        return unitFreeGoods;
    }
}
