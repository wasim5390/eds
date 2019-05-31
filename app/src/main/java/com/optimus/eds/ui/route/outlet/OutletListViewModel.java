package com.optimus.eds.ui.route.outlet;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.db.entities.Route;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.reactivex.disposables.CompositeDisposable;


public class OutletListViewModel extends AndroidViewModel {
    private OutletListRepository repository;
    public MutableLiveData<List<Outlet>> outletList;
    public MutableLiveData<List<Route>> routeList;
    private MutableLiveData<Boolean> isLoading;
    private MutableLiveData<String> errorMsg;
    CompositeDisposable disposable;
    Long SELECTED_ROUTE_ID;


    public OutletListViewModel(@NonNull Application application) {
        super(application);
        repository = OutletListRepository.getInstance(application);
        outletList = new MutableLiveData<>();
        routeList = new MutableLiveData<>();
        disposable = new CompositeDisposable();
        errorMsg = new MutableLiveData<>();
        isLoading = new MutableLiveData<>();
        loadRoutesFromDb();

    }



    public void loadRoutesFromDb() {

        repository.getRoutes().observeForever(routes -> routeList.setValue(routes));

   /*    Disposable allRoutesDisposable = repository.getRoutes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onRoutesFetched, this::onError);

        disposable.add(allRoutesDisposable);*/
    }


    public void loadOutletsFromDb(Long routeId){
      repository.getOutlets(routeId).observeForever(outlets -> outletList.setValue(outlets));

    }

    private void onRoutesFetched(List<Route> routes) {
         routeList.setValue(routes);
         isLoading.setValue(false);
    }

    private void onOutletsFetched(List<Outlet> outlets) {
        outletList.setValue(outlets);
        isLoading.setValue(false);
    }



    public LiveData<List<Outlet>> getOutletList(){
        return outletList;
    }

    public LiveData<List<Route>> getRouteList(){

        return routeList;
    }

    public void insertIntoDb() {
        List<Outlet> outlets = new ArrayList<>();
        List<Route> routes = new ArrayList<>();
        Outlet outlet;
        for(int i=0; i<10;i++){
            outlet = new Outlet();
            outlet.setRouteId(1023L);
            outlet.setOutletCode("Bkt"+String.valueOf(new Random(126*i).nextInt()));
            outlet.setOutletName("Shop "+ i);
            outlet.setAddress("Main boulevard Iqbal town, lhr");
            outlet.setOutletId((Long.valueOf(new Random(16*i).nextInt())));
           // outlet.setLastSaleDate(new Date().getTime());
            outlet.setVisitFrequency((i+1));
            outlet.setTotalAmount(12030*i/1.00);
            outlets.add(outlet);
        }

        repository.insertOutlets(outlets);

        Route route = new Route();
        route.setRouteId(1023L);
        route.setRouteName("Barkat Market");

        routes.add(route);

        /******************/
        for(int i=0; i<10;i++){
            outlet = new Outlet();
            outlet.setRouteId(101023L);
            outlet.setOutletCode("IQB"+String.valueOf(new Random(136*i).nextInt()));
            outlet.setOutletName("Shop_IQB "+ i);
            outlet.setAddress("Main boulevard Iqbal town, lhr");
            outlet.setOutletId((Long.valueOf(new Random(36*i).nextInt())));
           // outlet.setLastSaleDate(new Date().getTime());
            outlet.setVisitFrequency((i+1));
            outlet.setTotalAmount(12030*i/1.00);
            outlets.add(outlet);
        }
        repository.insertOutlets(outlets);

         route = new Route();
        route.setRouteId(101023L);
        route.setRouteName("Moon Market");

        routes.add(route);
        repository.insertRoutes(routes);



    }


    private void onError(Throwable throwable) {
        isLoading.setValue(false);
        errorMsg.setValue(throwable.getMessage());
    }
}
