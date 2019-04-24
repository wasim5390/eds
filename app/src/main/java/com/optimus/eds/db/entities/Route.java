
package com.optimus.eds.db.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.google.gson.annotations.SerializedName;


import java.io.Serializable;
import java.util.List;

@Entity(tableName = "Route")
public class Route implements Serializable {

    @SerializedName("routeId")
    @PrimaryKey
    private Long mRouteId;

    @SerializedName("routeName")
    private String mRouteName;

    @SerializedName("employeeId")
    private Integer mEmployeeId;


    /*******************Setters*********************/
    public void setRouteId(Long mRouteId) {
        this.mRouteId = mRouteId;
    }

    public void setRouteName(String mRouteName) {
        this.mRouteName = mRouteName;
    }
        public void setEmployeeId(Integer employeeId){
        this.mEmployeeId = employeeId;
    }

    /******************Getters**********************/


    public Long getRouteId() {
        return mRouteId;
    }

    public String getRouteName() {
        return mRouteName;
    }

    public Integer getEmployeeId() {
        return mEmployeeId;
    }



    @Override
    public String toString() {
        return this.getRouteName();            // What to display in the Spinner list.
    }

}
