package com.optimus.eds.model;

import com.google.gson.annotations.SerializedName;
import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.db.entities.Route;

import java.util.ArrayList;
import java.util.List;

public class RouteOutletResponseModel extends BaseResponse{

    @SerializedName("routes")
    List<Route> routeList;
    @SerializedName("outlets")
    List<Outlet> outletList;

    public List<Route> getRouteList() {
        return routeList==null?new ArrayList<>():routeList;
    }

    public List<Outlet> getOutletList() {
        return outletList==null?new ArrayList<>():outletList;
    }
}
