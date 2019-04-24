package com.optimus.eds.ui.route.merchandize;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.optimus.eds.Constant;

import java.util.ArrayList;
import java.util.List;


/**
 * Created By apple on 4/23/19
 */
public class MerchandiseViewModel extends AndroidViewModel {

    private MerchandiseRepository repository;
    private int imagesCount;
    private MutableLiveData<List<MerchandiseItem>> mMerchandise;
    private List<MerchandiseItem> list;
    private MutableLiveData<Boolean> isLoading;
    private MutableLiveData<Boolean> enableAfterMerchandiseButton;
    private MutableLiveData<Boolean> enableNextButton;
    private MutableLiveData<Boolean> lessImages;

    public MerchandiseViewModel(@NonNull Application application) {
        super(application);
        repository = new MerchandiseRepository(application);
        mMerchandise = new MutableLiveData<>();
        imagesCount=0;
        list=new ArrayList<>();
        isLoading = new MutableLiveData<>();
        enableAfterMerchandiseButton = new MutableLiveData<>();
        enableNextButton = new MutableLiveData<>();
        lessImages = new MutableLiveData<>();

        repository.isLoading().observeForever(aBoolean -> isLoading.setValue(aBoolean));
        enableAfterMerchandiseButton.setValue(false);
        enableNextButton.setValue(false);
    }

    public void saveImages(String path, int type) {
        imagesCount++;
        MerchandiseItem item = new MerchandiseItem();
        item.setId(imagesCount);
        item.setPath(path);
        item.setType(type);
        list.add(item);

        enableAfterMerchandiseButton.setValue(true);
        if(list.size()>1 && item.getType()==1){
            enableNextButton.setValue(true);
        }

        mMerchandise.setValue(list);
    }

    public void insertMerchandiseIntoDB(Long outletId){

        if(list.size()>=3) {
            isLoading.setValue(true);
            repository.insertIntoDb(outletId, mMerchandise.getValue());
        }else {
            lessImages.setValue(true);
        }
    }

    public void removeImage(MerchandiseItem item){
        list.remove(item);
        mMerchandise.setValue(list);
    }

    public LiveData<Boolean> isLoading() {
        return isLoading;
    }

    public LiveData<Boolean> enableAfterMerchandiseButton() {
        return enableAfterMerchandiseButton;
    }

    public LiveData<Boolean> enableNextButton() {
        return enableNextButton;
    }

    public LiveData<Boolean> lessImages() {
        return lessImages;
    }

    public MutableLiveData<List<MerchandiseItem>> getmMerchandise() {
        return mMerchandise;
    }
}
