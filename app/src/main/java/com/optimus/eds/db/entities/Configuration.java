
package com.optimus.eds.db.entities;

import com.google.gson.annotations.Expose;

public class Configuration {

    @Expose
    private Boolean endDayOnPjpCompletion;
    @Expose
    private Integer geoFenceMinRadius;
    @Expose
    private Boolean geoFenceRequired;

    public Configuration() {
    }

    public void setEndDayOnPjpCompletion(Boolean endDayOnPjpCompletion) {
        this.endDayOnPjpCompletion = endDayOnPjpCompletion;
    }

    public void setGeoFenceMinRadius(Integer geoFenceMinRadius) {
        this.geoFenceMinRadius = geoFenceMinRadius;
    }

    public void setGeoFenceRequired(Boolean geoFenceRequired) {
        this.geoFenceRequired = geoFenceRequired;
    }

    public Boolean getEndDayOnPjpCompletion() {
        return endDayOnPjpCompletion==null?false:endDayOnPjpCompletion;
    }

    public Integer getGeoFenceMinRadius() {
        return geoFenceMinRadius==null?20:geoFenceMinRadius;
    }

    public Boolean getGeoFenceRequired() {
        return geoFenceRequired==null?false:geoFenceRequired;
    }

}