package com.optimus.eds.source;

import android.arch.lifecycle.MutableLiveData;

import com.optimus.eds.Constant;
import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.db.entities.Route;
import com.optimus.eds.model.RouteResponseModel;

import java.util.List;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiRepository implements DataSource, Constant {
    private static ApiRepository instance;
    public static ApiRepository getInstance() {
        if (instance == null) {
            instance = new ApiRepository();
        }
        return instance;
    }

    private ApiRepository() {

        }
    @Override
    public Single<List<Route>> getRoutes(String userId) {
        return  RetrofitHelper.getInstance().getApi().getRoutes(userId);
    }

    @Override
    public Single<List<Outlet>> getOutlets(String routeId) {
        return  RetrofitHelper.getInstance().getApi().getOutlets(routeId);
    }
}
