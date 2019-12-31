package com.optimus.eds.model;

import com.google.gson.annotations.SerializedName;
import com.optimus.eds.db.entities.Asset;
import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.db.entities.Route;

import java.util.ArrayList;
import java.util.List;

public class RouteOutletResponseModel extends BaseResponse{

    @SerializedName("routes")
    List<Route> routeList;
    @SerializedName("outlets")
    List<Outlet> outletList;
    @SerializedName("assets")
    List<Asset> assetList;

    @SerializedName("distributionId")
    Integer distributionId;
    @SerializedName("employeeName")
    String employeeName;

    public List<Route> getRouteList() {
        return routeList==null?new ArrayList<>():routeList;
    }

    public List<Outlet> getOutletList() {
        return outletList==null?new ArrayList<>():outletList;
    }

    public List<Asset> getAssetList() {
        return assetList;
    }

    public Integer getDistributionId() {
        return distributionId;
    }

    public String getEmployeeName() {
        return employeeName;
    }
}
