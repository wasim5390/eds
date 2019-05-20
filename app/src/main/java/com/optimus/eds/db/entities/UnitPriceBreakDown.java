package com.optimus.eds.db.entities;

import com.google.gson.annotations.SerializedName;

public class UnitPriceBreakDown {

    @SerializedName("id")
    public Long mId;
    @SerializedName("orderId")
    public Long mOrderId;
    @SerializedName("orderDetailId")
    public Long mOrderDetailId;
    @SerializedName("priceCondition") //Will contain label of pricing like Retail Price, Discount
    public String mPriceCondition;
    @SerializedName("unitPrice")
    public Float mUnitPrice;
    @SerializedName("blockPrice")
    public Float mBlockPrice;
    @SerializedName("totalPrice")
    public Float  mTotalPrice;

    public UnitPriceBreakDown(Long id, Long orderId, Long orderDetailId, String priceCondition, Float unitPrice, Float blockPrice, Float totalPrice) {
        mId = id;
        mOrderId = orderId;
        mOrderDetailId = orderDetailId;
        mPriceCondition = priceCondition;
        mUnitPrice = unitPrice;
        mBlockPrice = blockPrice;
        mTotalPrice = totalPrice;
    }


    public Long getId() {
        return mId;
    }

    public Long getOrderId() {
        return mOrderId;
    }

    public Long getOrderDetailId() {
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

}
