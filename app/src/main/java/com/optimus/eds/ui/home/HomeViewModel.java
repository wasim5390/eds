package com.optimus.eds.ui.home;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.optimus.eds.Injection;
import com.optimus.eds.db.AppDatabase;
import com.optimus.eds.db.dao.OutletDao;
import com.optimus.eds.db.dao.ProductsDao;
import com.optimus.eds.db.dao.RouteDao;
import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.db.entities.Package;
import com.optimus.eds.db.entities.Product;
import com.optimus.eds.db.entities.Route;
import com.optimus.eds.source.API;
import com.optimus.eds.source.ApiRepository;
import com.optimus.eds.source.RetrofitHelper;
import com.optimus.eds.ui.route.outlet.OutletListRepository;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class HomeViewModel extends AndroidViewModel {

    RouteDao routeDao;
    OutletDao outletDao;
    ProductsDao productsDao;



    private final OutletListRepository outletListRepository;
    private MutableLiveData<Boolean> isLoading;
    private MutableLiveData<List<Outlet>> outletList;
    private MutableLiveData<List<Route>> routeList;
    private MutableLiveData<String> errorMsg;
    private ApiRepository api;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        outletListRepository = OutletListRepository.getInstance(application);
        api = ApiRepository.getInstance();
        AppDatabase database = AppDatabase.getDatabase(application);
        routeDao = database.routeDao();
        outletDao = database.outletDao();
        productsDao = database.productsDao();
        isLoading = new MutableLiveData<>();
        outletList = new MutableLiveData<>();
        routeList = new MutableLiveData<>();
        errorMsg = new MutableLiveData<>();

        onScreenCreated();


    }

    public void onScreenCreated(){
        isLoading.setValue(true);
       // Single<List<Outlet>> visitedOutlets = outletListRepository.getOutlets(123L).map(this::visitedOutlets);
      /*  api.getOutlets("223").observeForever(outlets -> outletList.setValue(outlets));

         api.getRoutes("123").observeForever(routes -> {
             routeList.setValue(routes);
         });*/
         routeDao.findAllRoutes().observeForever(routes -> {
             routeList.setValue(routes);
         });
         outletDao.findAllOutletsForRoute(123L).observeForever(outlets -> {
             outletList.setValue(outlets);
         });

            addData();
      /*  Disposable routesDisposable = todayRoutes
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::onSuccessRoutes, this::onError);

        Disposable outletDisposable = todayOutlets
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::onSuccessOutlets, this::onError);

        compositeDisposable.add(routesDisposable);
        compositeDisposable.add(outletDisposable);*/
    }

    private void addData(){

        List<Package> packageList = new ArrayList<>();
        List<Product> itemsList = new ArrayList<>();

        Package _package = new Package(1L,"SSRB");
        Package _package1 = new Package(2L,"SSRE");
        packageList.add(_package);
        packageList.add(_package1);

        itemsList.add(new Product(123L,_package.getPackageId(),"AXL"));
        itemsList.add(new Product(124L,_package.getPackageId(),"M"));
        itemsList.add(new Product(125L,_package.getPackageId(),"P"));
        itemsList.add(new Product(126L,_package.getPackageId(),"7UP"));

        itemsList.add(new Product(127L,_package1.getPackageId(),"DW"));
        itemsList.add(new Product(128L,_package1.getPackageId(),"RR"));
        itemsList.add(new Product(129L,_package1.getPackageId(),"2D"));


        addPackages(packageList);
        addProducts(itemsList);

    }

    private void addPackages(List<Package> packages){

            Completable.create(e -> {
                productsDao.insertPackages(packages);
                e.onComplete();
            }).subscribeOn(Schedulers.io())
                    .subscribe(new CompletableObserver() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onComplete() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }
                    });

    }


    private void addProducts(List<Product> products){

        Completable.create(e -> {
            productsDao.insertProducts(products);
            e.onComplete();
        }).subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });

    }


    private List<Outlet> visitedOutlets(List<Outlet> allOutlets) {
        List<Outlet> visitedOutlets = new ArrayList<>(allOutlets.size());
        for (Outlet outlet : allOutlets) {
          if(outlet.getVisitStatus()==1)
              visitedOutlets.add(outlet);
        }
        return visitedOutlets;
    }

    private void onSuccessOutlets(List<Outlet> outlets) {
        isLoading.setValue(false);
        outletList.setValue(outlets);


    }
    private void onSuccessRoutes(List<Route> routes) {
        isLoading.setValue(false);
        routeList.setValue(routes);
        saveRoutesInDb(routes);

    }
    @Override
    protected void onCleared() {
        super.onCleared();
    }

    public LiveData<Boolean> isLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMsg() {
        return errorMsg;
    }



    public LiveData<List<Outlet>> getOutlets() {
        return outletList;
    }

    public LiveData<List<Route>> getRoutes() {
        return routeList;
    }

    private void onError(Throwable throwable) {
        isLoading.setValue(false);
        errorMsg.setValue(throwable.getMessage());
    }



    public void saveRoutesInDb(List<Route> routes){
        outletListRepository.insertRoutes(routes);
    }


}
