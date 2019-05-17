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
import com.optimus.eds.db.entities.Merchandise;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


/**
 * Created By apple on 4/23/19
 */
public class MerchandiseViewModel extends AndroidViewModel {

    private MerchandiseRepository repository;
    private int imagesCount;
    private MutableLiveData<List<MerchandiseItem>> mMerchandise;
    private List<MerchandiseItem> list;
    private MutableLiveData<Boolean> isSaved;
    private MutableLiveData<Boolean> inProgress;
    private MutableLiveData<Boolean> enableAfterMerchandiseButton;
    private MutableLiveData<Boolean> enableNextButton;
    private MutableLiveData<Boolean> lessImages;

    private MutableLiveData<List<String>> mPlanogram;

    public MerchandiseViewModel(@NonNull Application application) {
        super(application);
        repository = new MerchandiseRepository(application);
        mMerchandise = new MutableLiveData<>();
        imagesCount=0;
        list=new ArrayList<>();
        isSaved = new MutableLiveData<>();
        inProgress = new MutableLiveData<>();
        enableAfterMerchandiseButton = new MutableLiveData<>();
        enableNextButton = new MutableLiveData<>();
        lessImages = new MutableLiveData<>();
        mPlanogram = new MutableLiveData<>();
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
            inProgress.postValue(true);
            saveMerchandise(outletId, mMerchandise.getValue());
        }else {
            lessImages.setValue(true);
        }
    }

    public void saveMerchandise(Long outletId, List<MerchandiseItem> merchandiseItems){

        Completable.create(e -> {
            Merchandise merchandise = new Merchandise();
            merchandise.setOutletId(outletId);
            merchandise.setMerchandiseItems(merchandiseItems);
            repository.insertIntoDb(merchandise);
            e.onComplete();
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onComplete() {
                isSaved.setValue(true);
                inProgress.postValue(false);

            }

            @Override
            public void onError(Throwable e) {
                isSaved.setValue(true);
                inProgress.postValue(false);
            }
        });

    }

    public void getImages(){
        List<String> stringList=new ArrayList<>();
        stringList.add("/storage/emulated/0/EDS/Images/JPEG_20190429_165907_1469908208.jpg");
        stringList.add("/storage/emulated/0/EDS/Images/JPEG_20190429_165907_1469908208.jpg");

        mPlanogram.setValue(stringList);
    }


    public void removeImage(MerchandiseItem item){
        list.remove(item);
        mMerchandise.setValue(list);
    }

    public LiveData<Boolean> isSaved() {
        return isSaved;
    }

    public LiveData<Boolean> isInProgress() {
        return inProgress;
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
    public MutableLiveData<List<String>> getPlanogaram() {
        return mPlanogram;
    }
}
