package com.optimus.eds.ui.merchandize.coolerverification;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.optimus.eds.db.AppDatabase;
import com.optimus.eds.db.dao.MerchandiseDao;
import com.optimus.eds.db.entities.Asset;
import com.optimus.eds.db.entities.Merchandise;
import com.optimus.eds.ui.merchandize.MerchandiseRepository;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created By apple on 4/30/19
 */
public class AssetsViewModel extends AndroidViewModel {


    private MutableLiveData<List<Asset>> mAssets;
    private MerchandiseRepository repository;
    private MerchandiseDao merchandiseDao;

    public AssetsViewModel(@NonNull Application application) {
        super(application);
        merchandiseDao= AppDatabase.getDatabase(application).merchandiseDao();
        mAssets = new MutableLiveData<>();
        repository = new MerchandiseRepository(application);


    }

    public LiveData<List<Asset>> loadAssets(Long outletId){
        repository.loadAssets(outletId).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribe(assets -> {
                    mAssets.postValue(assets);
        });
        return mAssets;

    }

    public void updateAsset(Asset asset){
         repository.updateAsset(asset);
    }

    public LiveData<List<Asset>> getAssets() {
        return mAssets;
    }

}
