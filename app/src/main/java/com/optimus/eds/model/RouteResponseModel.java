package com.optimus.eds.model;

import com.google.gson.annotations.SerializedName;
import com.optimus.eds.db.entities.Route;

import java.util.List;

public class RouteResponseModel extends BaseResponse{

    @SerializedName("Route")
    List<Route> routeList;

    public List<Route> getRouteList() {
        return routeList;
    }
}
