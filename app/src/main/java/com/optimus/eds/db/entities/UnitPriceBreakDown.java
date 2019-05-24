package com.optimus.eds.db.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(
        foreignKeys = {
                @ForeignKey(
                        entity = OrderDetail.class,
                        parentColumns = "unitOrderDetailId",
                        childColumns = "fk_odid",
                        onDelete = ForeignKey.CASCADE),


        }, indices = { @Index(value = "fk_odid")})
public class UnitPriceBreakDown {

    @PrimaryKey
    @ColumnInfo(name = "pk_upbd")
    @SerializedName("id")
    public Integer mId;
    @SerializedName("orderId")
    public Integer mOrderId;
    @ColumnInfo(name = "fk_odid")
    @SerializedName("unitOrderDetailId")
    public Integer mOrderDetailId;
    @SerializedName("priceCondition")
    public String mPriceCondition;
    @SerializedName("priceConditionType") //Will contain label of pricing like Retail Price, Discount
    public String mPriceConditionType;
    @SerializedName("priceConditionClass")
    public String mPriceConditionClass;
    @SerializedName("priceConditionId")
    public Integer mPriceConditionId;
    @SerializedName("priceConditionDetailId")
    public Integer mPriceConditionDetailId;
    @SerializedName("accessSequence")
    public String mAccessSequence;
    @SerializedName("unitPrice")
    public Float mUnitPrice;
    @SerializedName("blockPrice")
    public Float mBlockPrice;
    @SerializedName("totalPrice")
    public Float  mTotalPrice;
    @SerializedName("calculationType")
    public Integer mCalculationType;
    @SerializedName("outletId")
    public Integer outletId;
    @SerializedName("productId")
    public Integer mProductId;
    @SerializedName("productDefinitionId")
    public Integer mProductDefinitionId;
    @SerializedName("isMaxLimitReached")
    public Boolean isMaxLimitReached;
    @SerializedName("maximumLimit")
    public Float maximumLimit;
    @SerializedName("alreadyAvailed")
    public Float mAlreadyAvailed;
    @SerializedName("limitBy")
    public Integer mLimitBy;


    public Integer getId() {
        return mId;
    }

    public Integer getOrderId() {
        return mOrderId;
    }

    public Integer getOrderDetailId() {
        return mOrderDetailId;
    }

    public String getPriceCondition() {
        return mPriceCondition;
    }

    public Float getUnitPrice() {
        return mUnitPrice;
    }

    public Float getBlockPrice() {
        return mBlockPrice;
    }

    public Float getTotalPrice() {
        return mTotalPrice;
    }

    public String getPriceConditionType() {
        return mPriceConditionType;
    }

    public Integer getPriceConditionId() {
        return mPriceConditionId;
    }


}
