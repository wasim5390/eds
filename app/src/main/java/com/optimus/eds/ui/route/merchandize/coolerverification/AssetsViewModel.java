package com.optimus.eds.ui.route.merchandize.coolerverification;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.optimus.eds.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created By apple on 4/30/19
 */
public class AssetsViewModel extends AndroidViewModel {

    private MutableLiveData<List<CoolerAsset>> mCoolerAssets;

    List<CoolerAsset> coolerAssetList;
    public AssetsViewModel(@NonNull Application application) {
        super(application);

        mCoolerAssets = new MutableLiveData<>();
        coolerAssetList = new ArrayList<>();
    }

    public void getAssets(){

        for (int i=0;i<5;i++){
            CoolerAsset coolerAsset = new CoolerAsset();
            List<String> list = new ArrayList<>();
            list.add("");
            list.add("Scanning Not being done");
            list.add("No Barcode");
            list.add("No Cooler");
            coolerAsset.setReasons(list);
            coolerAssetList.add(coolerAsset);
        }

        mCoolerAssets.setValue(coolerAssetList);
    }

    public MutableLiveData<List<CoolerAsset>> getCoolerAssets() {
        return mCoolerAssets;
    }
}
