package com.optimus.eds.db.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.support.annotation.NonNull;


@Entity(primaryKeys = {"outletId","orderId"})
public class CustomerInput {

    @NonNull
    private Long outletId;
    @NonNull
    private Long orderId;
    private String deliveryDate;
    private String mobileNumber;
    private String remarks;
    private String signature;

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getRemarks() {
        return remarks;
    }

    public CustomerInput() {
    }

    @Ignore
    public CustomerInput(@NonNull Long outletId,@NonNull Long orderId, String deliveryDate, String mobileNumber, String remarks, String signature) {
        this.outletId = outletId;
        this.orderId = orderId;
        this.deliveryDate = deliveryDate;
        this.mobileNumber = mobileNumber;
        this.remarks = remarks;
        this.signature = signature;
    }




    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
    public Long getOutletId() {
        return outletId;
    }

    public void setOutletId(Long outletId) {
        this.outletId = outletId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }



}
