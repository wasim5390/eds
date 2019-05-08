package com.optimus.eds.source;

import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.db.entities.Route;
import com.optimus.eds.model.BaseResponse;
import com.optimus.eds.model.PackageProductResponseModel;
import com.optimus.eds.model.RouteOutletResponseModel;


import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface API {

    @FormUrlEncoded
    @POST("token")
    Call<TokenResponse> getToken(@Field("grant_type") String type , @Field("username") String username, @Field("password") String password);

    @GET("api/route/routes")
    Call<RouteOutletResponseModel> loadTodayRouteOutlets();

    @GET("api/route/products")
    Call<PackageProductResponseModel> loadTodayPackageProduct();

    @GET("routes")
    Call<List<Route>> getRoutes(@Query("id") String userId);

    @GET("outlets")
    Call<List<Outlet>> getOutlets(@Query("id") String routeId);
}
