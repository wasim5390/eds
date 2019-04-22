
package com.optimus.eds.db.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.google.gson.annotations.SerializedName;
import com.optimus.eds.db.converters.OutletConverter;


import java.io.Serializable;
import java.util.List;

@Entity(tableName = "Route")
public class Route implements Serializable {

    @SerializedName("RouteId")
    @PrimaryKey
    private Long mRouteId;

    @SerializedName("Outlet")
    private List<Outlet> mOutlet;

    @SerializedName("RouteName")
    private String mRouteName;
    @SerializedName("TotalAmount")
    private Double mTotalAmount;
    @SerializedName("TotalOrders")
    private Integer mTotalOrders;
    @SerializedName("TotalOutlets")
    private Integer mTotalOutlets;
    @SerializedName("TotalQuantity")
    private String mTotalQuantity;

    /*******************Setters*********************/
    public void setRouteId(Long mRouteId) {
        this.mRouteId = mRouteId;
    }

    public void setOutlet(List<Outlet> mOutlet) {
        this.mOutlet = mOutlet;
    }

    public void setRouteName(String mRouteName) {
        this.mRouteName = mRouteName;
    }

    public void setTotalAmount(Double mTotalAmount) {
        this.mTotalAmount = mTotalAmount;
    }

    public void setTotalOrders(Integer mTotalOrders) {
        this.mTotalOrders = mTotalOrders;
    }

    public void setTotalOutlets(Integer mTotalOutlets) {
        this.mTotalOutlets = mTotalOutlets;
    }

    public void setTotalQuantity(String mTotalQuantity) {
        this.mTotalQuantity = mTotalQuantity;
    }



    /******************Getters**********************/

    public List<Outlet> getOutlet() {
        return mOutlet;
    }

    public Long getRouteId() {
        return mRouteId;
    }

    public String getRouteName() {
        return mRouteName;
    }

    public Double getTotalAmount() {
        return mTotalAmount;
    }

    public Integer getTotalOrders() {
        return mTotalOrders;
    }

    public Integer getTotalOutlets() {
        return mTotalOutlets;
    }

    public String getTotalQuantity() {
        return mTotalQuantity;
    }

    @Override
    public String toString() {
        return this.getRouteName();            // What to display in the Spinner list.
    }

}
