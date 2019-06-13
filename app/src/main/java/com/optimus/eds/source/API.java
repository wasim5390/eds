package com.optimus.eds.source;

import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.db.entities.Route;
import com.optimus.eds.model.MasterModel;
import com.optimus.eds.model.OrderResponseModel;
import com.optimus.eds.model.PackageProductResponseModel;
import com.optimus.eds.model.RouteOutletResponseModel;


import java.util.List;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface API {

    @FormUrlEncoded
    @POST("token")
    Single<TokenResponse> getToken(@Field("grant_type") String type , @Field("username") String username, @Field("password") String password);

    @FormUrlEncoded
    @POST("token")
    Call<TokenResponse> refreshToken(@Field("grant_type") String type , @Field("username") String username, @Field("password") String password);


    @GET("route/routes")
    Call<RouteOutletResponseModel> loadTodayRouteOutlets();

    @GET("route/products")
    Call<PackageProductResponseModel> loadTodayPackageProduct();

    @POST("api/order/calculateprice")
    Single<OrderResponseModel> calculatePricing(@Body OrderResponseModel order);

    @POST("api/order/PostOrder")
    Single<OrderResponseModel> saveOrder(@Body MasterModel order);

    @GET("routes")
    Call<List<Route>> getRoutes(@Query("id") String userId);

    @GET("outlets")
    Call<List<Outlet>> getOutlets(@Query("id") String routeId);
}
