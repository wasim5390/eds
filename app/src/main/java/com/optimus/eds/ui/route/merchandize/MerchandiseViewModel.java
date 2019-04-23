package com.optimus.eds.ui.route.merchandize;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.optimus.eds.Constant;

import java.util.List;


/**
 * Created By apple on 4/23/19
 */
public class MerchandiseViewModel extends AndroidViewModel {

    private MerchandiseRepository repository;
    private int imagesCount;
    private MutableLiveData<List<MerchandiseItem>> mMerchandise;

    public MerchandiseViewModel(@NonNull Application application) {
        super(application);
        repository = new MerchandiseRepository(application);
        mMerchandise = new MutableLiveData<>();
        imagesCount=0;
    }

    public void saveImages(String path, int type) {
        imagesCount++;
        MerchandiseItem item = new MerchandiseItem();
        item.setId(imagesCount);
        item.setPath(path);
        item.setType(type);
        mMerchandise.getValue().add(item);
    }

    public void insertMerchandiseIntoDB(Long outletId){
        repository.insertIntoDb(outletId,mMerchandise.getValue());
    }

    public MutableLiveData<List<MerchandiseItem>> getmMerchandise() {
        return mMerchandise;
    }
}
