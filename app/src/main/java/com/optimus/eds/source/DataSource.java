package com.optimus.eds.source;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.telecom.Call;

import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.db.entities.Route;
import com.optimus.eds.model.RouteResponseModel;

import java.util.List;

import io.reactivex.Single;


/**
 * Created by sidhu on 4/11/2018.
 */

public interface DataSource {

   LiveData<List<Route>> getRoutes(String userId);

   LiveData<List<Outlet>> getOutlets(String routeId);

}
