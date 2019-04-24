package com.optimus.eds.ui.route.merchandize;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.optimus.eds.Constant;
import com.optimus.eds.db.AppDatabase;
import com.optimus.eds.db.dao.MerchandiseDao;
import com.optimus.eds.db.entities.Merchandise;
import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.db.entities.Route;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableObserver;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created By apple on 4/23/19
 */
public class MerchandiseRepository {

    private MerchandiseDao merchandiseDao;
    private MutableLiveData<Boolean> isLoading;

    public MerchandiseRepository(Application application) {
        AppDatabase appDatabase = AppDatabase.getDatabase(application);
        merchandiseDao = appDatabase.merchandiseDao();
        isLoading = new MutableLiveData<>();
    }

    public void insertIntoDb(Long outletId, List<MerchandiseItem> merchandiseItems) {

        Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter e) throws Exception {
                Merchandise merchandise = new Merchandise();
                merchandise.setOutletId(outletId);
                merchandise.setMerchandiseItems(merchandiseItems);
                merchandiseDao.insertMerchandise(merchandise);
                e.onComplete();
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onComplete() {
                isLoading.setValue(false);
            }

            @Override
            public void onError(Throwable e) {

            }
        });
    }

    public LiveData<Boolean> isLoading() {
        return isLoading;
    }

}
