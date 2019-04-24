
package com.optimus.eds.db.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.google.gson.annotations.SerializedName;
import com.optimus.eds.ui.route.Asset;

import java.io.Serializable;
import java.util.List;


@Entity(tableName = "Outlet")
public class Outlet implements Serializable {

    @PrimaryKey
    @SerializedName("OutletId")
    private Long mOutletId;
    @SerializedName("RouteId")
    private Long mRouteId;
    @SerializedName("Address")
    private String mAddress;
    @SerializedName("Asset")
    private List<Asset> mAsset;
    @SerializedName("AvailableCreditLimit")
    private Double mAvailableCreditLimit;
    @SerializedName("LastSale")
    private Double mLastSale;
    @SerializedName("ChannelName")
    private String mChannelName;
    @SerializedName("LastSaleDate")
    private Long mLastSaleDate;
    @SerializedName("LastSaleQuantity")
    private String mLastSaleQuantity;
    @SerializedName("Latitude")
    private Float mLatitude;
    @SerializedName("Location")
    private String mLocation;
    @SerializedName("Longitude")
    private Float mLongitude;
    @SerializedName("OutletCode")
    private String mOutletCode;

    @SerializedName("OutletName")
    private String mOutletName;
    @SerializedName("OutstandingCredit")
    private Double mOutstandingCredit;
    @SerializedName("TotalAmount")
    private Double mTotalAmount;
    @SerializedName("TotalQuantity")
    private String mTotalQuantity;
    @SerializedName("VisitDay")
    private Integer mVisitDay;
    @SerializedName("VisitFrequency")
    private Integer mVisitFrequency;
    @SerializedName("VisitStatus")
    private Integer mVisitStatus;


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

    public void setAsset(List<Asset> mAsset) {
        this.mAsset = mAsset;
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

    public void setLastSaleDate(Long mLastSaleDate) {
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

    public void setTotalQuantity(String mTotalQuantity) {
        this.mTotalQuantity = mTotalQuantity;
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

    public List<Asset> getAsset() {
        return mAsset;
    }

    public Double getAvailableCreditLimit() {
        return mAvailableCreditLimit;
    }

    public String getChannelName() {
        return mChannelName;
    }

    public Long getLastSaleDate() {
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

    public String getTotalQuantity() {
        return mTotalQuantity;
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
