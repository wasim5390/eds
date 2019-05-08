package com.optimus.eds.source;


import android.arch.lifecycle.LiveData;

import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.db.entities.Route;

import java.util.List;


/**
 * Created by sidhu on 4/11/2018.
 */

public interface DataSource {

   LiveData<List<Route>> getRoutes(String userId);

   LiveData<List<Outlet>> getOutlets(String routeId);

}
