
package com.optimus.eds.db.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


@Entity(tableName = "Outlet")
public class Outlet implements Serializable {

    @PrimaryKey
    @SerializedName("outletId")
    private Long mOutletId;
    @SerializedName("routeId")
    private Long mRouteId;
    @SerializedName("outletCode")
    private String mOutletCode;
    @SerializedName("outletName")
    private String mOutletName;
    @SerializedName("channelName")
    private String mChannelName;
    @SerializedName("location")
    private String mLocation;
    @SerializedName("visitFrequency")
    private Integer mVisitFrequency;
    @SerializedName("visitDay")
    private Integer mVisitDay;
    @SerializedName("address")
    private String mAddress;
    @SerializedName("latitude")
    private Float mLatitude;
    @SerializedName("longitude")
    private Float mLongitude;
    @SerializedName("lastSaleDate")
    private String mLastSaleDate;
    @SerializedName("lastSaleQuantity")
    private String mLastSaleQuantity;
    @SerializedName("availableCreditLimit")
    private Double mAvailableCreditLimit;
    @SerializedName("outstandingCreditLimit")
    private Double mOutstandingCredit;
    @SerializedName("lastSale")
    private Double mLastSale;
    @SerializedName("visitStatus")
    private Integer mVisitStatus;


    @Ignore
    private Double mTotalAmount;

    public Long getRouteId() {
        return mRouteId;
    }

    public void setRouteId(Long mRouteId) {
        this.mRouteId = mRouteId;
    }
    public void setOutletId(Long mOutletId) {
        this.mOutletId = mOutletId;
    }

    public void setAddress(String mAddress) {
        this.mAddress = mAddress;
    }

    public void setAvailableCreditLimit(Double mAvailableCreditLimit) {
        this.mAvailableCreditLimit = mAvailableCreditLimit;
    }

    public void setLastSale(Double mLastSale) {
        this.mLastSale = mLastSale;
    }

    public void setChannelName(String mChannelName) {
        this.mChannelName = mChannelName;
    }

    public void setLastSaleDate(String mLastSaleDate) {
        this.mLastSaleDate = mLastSaleDate;
    }

    public void setLastSaleQuantity(String mLastSaleQuantity) {
        this.mLastSaleQuantity = mLastSaleQuantity;
    }

    public void setLatitude(Float mLatitude) {
        this.mLatitude = mLatitude;
    }

    public void setLocation(String mLocation) {
        this.mLocation = mLocation;
    }

    public void setLongitude(Float mLongitude) {
        this.mLongitude = mLongitude;
    }

    public void setOutletCode(String mOutletCode) {
        this.mOutletCode = mOutletCode;
    }

    public void setOutletName(String mOutletName) {
        this.mOutletName = mOutletName;
    }

    public void setOutstandingCredit(Double mOutstandingCredit) {
        this.mOutstandingCredit = mOutstandingCredit;
    }

    public void setTotalAmount(Double mTotalAmount) {
        this.mTotalAmount = mTotalAmount;
    }

    public void setVisitDay(Integer mVisitDay) {
        this.mVisitDay = mVisitDay;
    }

    public void setVisitFrequency(Integer mVisitFrequency) {
        this.mVisitFrequency = mVisitFrequency;
    }

    public void setVisitStatus(Integer mVisitStatus) {
        this.mVisitStatus = mVisitStatus;
    }

    public String getAddress() {
        return mAddress;
    }


    public Double getAvailableCreditLimit() {
        return mAvailableCreditLimit;
    }

    public String getChannelName() {
        return mChannelName;
    }

    public String getLastSaleDate() {
        return mLastSaleDate;
    }

    public String getLastSaleQuantity() {
        return mLastSaleQuantity;
    }

    public Double getLastSale() {
        return mLastSale;
    }

    public Float getLatitude() {
        return mLatitude;
    }

    public String getLocation() {
        return mLocation;
    }

    public Float getLongitude() {
        return mLongitude;
    }

    public String getOutletCode() {
        return mOutletCode;
    }

    public Long getOutletId() {
        return mOutletId;
    }

    public String getOutletName() {
        return mOutletName;
    }

    public Double getOutstandingCredit() {
        return mOutstandingCredit;
    }

    public Double getTotalAmount() {
        return mTotalAmount;
    }

    public Integer getVisitDay() {
        return mVisitDay;
    }

    public Integer getVisitFrequency() {
        return mVisitFrequency;
    }

    public Integer getVisitStatus() {
        return mVisitStatus;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Outlet && ((Outlet) obj).getOutletId() == getOutletId();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}
