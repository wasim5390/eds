package com.optimus.eds.ui.route.outlet;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.db.entities.Route;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


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
        Disposable allOutletsDisposable= repository.getOutlets(routeId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onOutletsFetched, this::onError);
        disposable.add(allOutletsDisposable);
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
            outlet.setLastSaleDate(new Date().getTime());
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
            outlet.setLastSaleDate(new Date().getTime());
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



/*
        Route route = new Route();
        route.setRouteId(101023L);
        route.setOutlet(outletList);
        route.setRouteName("Barkat Market");

        routeList.add(route);
        List<Outlet> joharTownList = new ArrayList<>(outletList);
        joharTownList.remove(0);
        joharTownList.remove(8);
        route = new Route();
        route.setRouteId(203653L);
        route.setOutlet(joharTownList);
        route.setRouteName("Johar Town");
        routeList.add(route);
       Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter e) throws Exception {
                repository.insertRoutes(routeList);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onComplete() {
                fetchOutletsFromDb();
            }

            @Override
            public void onError(Throwable e) {

            }
        });*/



/*    repository.getRoutes("23", new DataSource.GetDataCallback<RouteResponseModel>() {
        @Override
        public void onDataReceived(RouteResponseModel data) {
            routeList = data.getRouteList();
            routeDao.insertRoutes(routeList);
            view.onRouteListLoaded(routeList);
        }

        @Override
        public void onFailed(int code, String message) {

        }
    });*/
    }


    private void onError(Throwable throwable) {
        isLoading.setValue(false);
        errorMsg.setValue(throwable.getMessage());
    }
}
