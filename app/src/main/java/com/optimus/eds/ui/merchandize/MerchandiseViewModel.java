package com.optimus.eds.ui.merchandize;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.optimus.eds.db.entities.Merchandise;
import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.ui.route.outlet.detail.OutletDetailRepository;

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
    private OutletDetailRepository outletDetailRepository;
    private int imagesCount=0;
    private MutableLiveData<List<MerchandiseImage>> imagesLiveDate;
    private List<MerchandiseImage> listImages;
    private MutableLiveData<Boolean> isSaved;
    private MutableLiveData<Boolean> inProgress;
    private MutableLiveData<Boolean> enableAfterMerchandiseButton;
    private MutableLiveData<Boolean> enableNextButton;
    private MutableLiveData<Boolean> lessImages;

    private MutableLiveData<List<String>> mPlanogram;
    private Long outletId;
    public MerchandiseViewModel(@NonNull Application application) {
        super(application);
        repository = new MerchandiseRepository(application);
        outletDetailRepository = new OutletDetailRepository(application);
        listImages=new ArrayList<>();
        isSaved = new MutableLiveData<>();
        inProgress = new MutableLiveData<>();
        enableAfterMerchandiseButton = new MutableLiveData<>();
        imagesLiveDate = new MutableLiveData<>();
        enableNextButton = new MutableLiveData<>();
        lessImages = new MutableLiveData<>();
        mPlanogram = new MutableLiveData<>();
        enableAfterMerchandiseButton.setValue(false);
        enableNextButton.setValue(false);

    }

    public void setOutletId(Long outletId) {
        this.outletId = outletId;

    }

    public void saveImages(String path,String base64Image, int type) {

        imagesCount++;
        MerchandiseImage item = new MerchandiseImage();
        item.setId(imagesCount);
        item.setBase64Image(base64Image);
        item.setPath(path);
        item.setType(type);

        listImages.add(item);

        setEnableNextButton(type);

    }

    private void setEnableNextButton(int type){
        enableAfterMerchandiseButton.setValue(true);
        if(listImages.size()>1 && type==1){
            enableNextButton.setValue(true);
        }
        imagesLiveDate.setValue(listImages);
    }





    public void insertMerchandiseIntoDB(Long outletId,String remarks){

        if(listImages.size()>=3) {
            inProgress.postValue(true);
            saveMerchandise(outletId, remarks,imagesLiveDate.getValue());
        }else {
            lessImages.setValue(true);
        }
    }

    public LiveData<Merchandise> loadMerchandise(Long outletId){
        MutableLiveData<Merchandise> mutableLiveData = new MutableLiveData<>();
        repository.findMerchandise(outletId).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(merchandise -> {
                    listImages.clear();
                    listImages.addAll(merchandise.getMerchandiseImages());
                    imagesCount=listImages.size();
                    mutableLiveData.postValue(merchandise);
                    if(imagesCount>2)
                    setEnableNextButton(1);
        });
        return mutableLiveData;
    }

    public void saveMerchandise(Long outletId, String remarks,List<MerchandiseImage> merchandiseImages){

        Completable.create(e -> {
            Merchandise merchandise = new Merchandise();
            merchandise.setOutletId(outletId);
            merchandise.setRemarks(remarks);
            merchandise.setMerchandiseImages(merchandiseImages);
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


    public void removeImage(MerchandiseImage item){
        listImages.remove(item);
        imagesLiveDate.setValue(listImages);
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

    public MutableLiveData<List<MerchandiseImage>> getMerchandiseImages() {
        return imagesLiveDate;
    }
    public LiveData<Outlet> loadOutlet(Long outletId) {
        return outletDetailRepository.getOutletById(outletId);
    }
    public MutableLiveData<List<String>> getPlanogaram() {
        return mPlanogram;
    }
}
