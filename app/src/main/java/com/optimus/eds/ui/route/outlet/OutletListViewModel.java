package com.optimus.eds.ui.route.outlet;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.annotation.NonNull;

import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.db.entities.Route;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;


public class OutletListViewModel extends AndroidViewModel {
    private final OutletListRepository repository;
    private final MutableLiveData<List<Outlet>> outletList;
    private final MutableLiveData<List<Route>> routeList;
    private final MutableLiveData<Boolean> isLoading;
    private final MutableLiveData<String> errorMsg;
    private final List<Outlet> allOutlets;
    private final CompositeDisposable disposable;


    public OutletListViewModel(@NonNull Application application) {
        super(application);
        repository = OutletListRepository.getInstance(application);
        outletList = new MutableLiveData<>();
        routeList = new MutableLiveData<>();
        disposable = new CompositeDisposable();
        errorMsg = new MutableLiveData<>();
        isLoading = new MutableLiveData<>();
        allOutlets = new ArrayList<>();
        loadRoutesFromDb();

    }



    private void loadRoutesFromDb() {
        repository.getRoutes().observeForever(routes -> routeList.setValue(routes));
    }


    public void loadOutletsFromDb(Long routeId,boolean isPjp){
        isLoading.postValue(true);
        ConnectableObservable<List<Outlet>> outletObservable = getOutlets(routeId,isPjp).replay();
        disposable.add(
                outletObservable
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<List<Outlet>>() {

                            @Override
                            public void onNext(List<Outlet> outlets) {
                                // Refreshing list
                                allOutlets.clear();
                                allOutlets.addAll(outlets);
                                outletList.postValue(allOutlets);
                            }

                            @Override
                            public void onError(Throwable e) {
                                errorMsg.postValue(e.getMessage());
                            }

                            @Override
                            public void onComplete() {
                            }
                        }));

        /////////////
        disposable.add(
                outletObservable
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .flatMap((Function<List<Outlet>, ObservableSource<Outlet>>) Observable::fromIterable)
                        .flatMap((Function<Outlet, ObservableSource<Outlet>>) this::getOrderObservable)
                        .subscribeWith(new DisposableObserver<Outlet>() {

                            @Override
                            public void onNext(Outlet outlet) {
                                int position = allOutlets.indexOf(outlet);

                                if (position == -1) {
                                    return;
                                }

                                allOutlets.set(position, outlet);
                                outletList.postValue(allOutlets);

                            }

                            @Override
                            public void onError(Throwable e) {
                                isLoading.postValue(false);
                                errorMsg.postValue(e.getMessage());
                            }

                            @Override
                            public void onComplete() {
                            }
                        }));

        // Calling connect to start emission
        outletObservable.connect();
    }



    private Observable<List<Outlet>> getOutlets(Long routeId,boolean isPjp) {
        return repository.getOutlets(routeId,isPjp?1:0).toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Observable<Outlet> getOrderObservable(final Outlet outlet) {
        return repository.findOrder(outlet.getOutletId())
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(orderModel -> {
                    if(orderModel.getOrder().getOrderStatus()==1) {
                        outlet.setTotalAmount(orderModel.getOrder().getPayable());
                       // outlet.setVisitStatus(1);
                    }
                    return outlet;
                });

    }

    public LiveData<Boolean> orderTaken(Long outletId){
        MutableLiveData<Boolean> orderAlreadyTaken = new MutableLiveData<>();
        repository.findOrder(outletId).toSingle().observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread()).subscribe(orderModel -> orderAlreadyTaken.postValue(orderModel.getOrder().getOrderStatus() == 1), throwable -> {
            if(throwable instanceof NoSuchElementException)
                orderAlreadyTaken.postValue(false);
            else onError(throwable);
        });
        return orderAlreadyTaken;
    }





    public LiveData<List<Outlet>> getOutletList(){
        return outletList;
    }

    public LiveData<List<Route>> getRouteList(){

        return routeList;
    }

    public MutableLiveData<Boolean> isLoading() {
        return isLoading;
    }

    private void onError(Throwable throwable) {
        isLoading.postValue(false);
        errorMsg.setValue(throwable.getMessage());
    }


}
