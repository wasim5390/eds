package com.optimus.eds.ui.route.outlet;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.annotation.NonNull;

import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.db.entities.Route;
import com.optimus.eds.model.OrderModel;

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
    private OutletListRepository repository;
    public MutableLiveData<List<Outlet>> outletList;
    public MutableLiveData<List<Route>> routeList;
    private MutableLiveData<Boolean> isLoading;
    private MutableLiveData<String> errorMsg;
    private List<Outlet> allOutlets;
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
        allOutlets = new ArrayList<>();
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
        //repository.getOutlets(routeId).observeForever(outlets -> outletList.setValue(outlets));
        ConnectableObservable<List<Outlet>> outletObservable = getOutlets(routeId).replay();
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
        /**
         * Fetching individual ticket price
         * First FlatMap converts single List<Ticket> to multiple emissions
         * Second FlatMap makes HTTP call on each Ticket emission
         * */
        disposable.add(
                outletObservable
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        /**
                         * Converting List<Ticket> emission to single Ticket emissions
                         * */
                        .flatMap(new Function<List<Outlet>, ObservableSource<Outlet>>() {
                            @Override
                            public ObservableSource<Outlet> apply(List<Outlet> tickets) throws Exception {
                                return Observable.fromIterable(tickets);
                            }
                        })
                        /**
                         * Fetching price on each Ticket emission
                         * */
                        .flatMap(new Function<Outlet, ObservableSource<Outlet>>() {
                            @Override
                            public ObservableSource<Outlet> apply(Outlet outlet) throws Exception {
                                return getOrderObservable(outlet);
                            }
                        })
                        .subscribeWith(new DisposableObserver<Outlet>() {

                            @Override
                            public void onNext(Outlet outlet) {
                                int position = allOutlets.indexOf(outlet);

                                if (position == -1) {
                                    return;
                                }

                                allOutlets.set(position, outlet);
                                outletList.postValue(allOutlets);
                               // mAdapter.notifyItemChanged(position);
                            }

                            @Override
                            public void onError(Throwable e) {
                                errorMsg.postValue(e.getMessage());
                            }

                            @Override
                            public void onComplete() {

                            }
                        }));

        // Calling connect to start emission
        outletObservable.connect();
    }



    private Observable<List<Outlet>> getOutlets(Long routeId) {
        return repository.getOutlets(routeId).toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Observable<Outlet> getOrderObservable(final Outlet outlet) {
        return repository.findOrder(outlet.getOutletId())
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(orderModel -> {
                    if(orderModel.getOrder().getOrderStatus()==1)
                     outlet.setTotalAmount(orderModel.getOrder().getPayable());
                     return outlet;
                });

    }

    public LiveData<Boolean> orderTaken(Long outletId){
        MutableLiveData<Boolean> orderAlreadyTaken = new MutableLiveData<>();
        repository.findOrder(outletId).toSingle().observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread()).subscribe(orderModel -> {

            orderAlreadyTaken.postValue(orderModel.getOrder().getOrderStatus()==1?true:false);
        },throwable -> {
            if(throwable instanceof NoSuchElementException)
                orderAlreadyTaken.postValue(false);
            else onError(throwable);
        });
        return orderAlreadyTaken;
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
    private void onError(Throwable throwable) {
        isLoading.setValue(false);
        errorMsg.setValue(throwable.getMessage());
    }
}
