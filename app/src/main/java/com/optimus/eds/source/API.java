package com.optimus.eds.source;

import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.db.entities.Route;
import com.optimus.eds.model.RouteResponseModel;

import java.util.List;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface API {

    @GET("routes")
    Call<List<Route>> getRoutes(@Query("id") String userId);

    @GET("outlets")
    Call<List<Outlet>> getOutlets(@Query("id") String routeId);
}
