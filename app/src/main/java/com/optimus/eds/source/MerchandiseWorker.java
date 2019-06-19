package com.optimus.eds.source;

import android.content.Context;
import android.util.Log;

import com.optimus.eds.Constant;
import com.optimus.eds.db.AppDatabase;
import com.optimus.eds.db.entities.Merchandise;
import com.optimus.eds.model.BaseResponse;
import com.optimus.eds.model.MerchandiseModel;
import com.optimus.eds.ui.merchandize.MerchandiseImage;
import com.optimus.eds.utils.PreferenceUtil;
import com.optimus.eds.utils.Util;

import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;
import java.util.function.Function;

import androidx.annotation.NonNull;

import androidx.work.RxWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import io.reactivex.Single;
import io.reactivex.SingleObserver;

import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

public class MerchandiseWorker extends Worker {

    private static final String TAG = MerchandiseWorker.class.getSimpleName();

    public MerchandiseWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }


    @Override
    public Result doWork() {
        Long outletId = getInputData().getLong(Constant.EXTRA_PARAM_OUTLET_ID,-1);
        Context applicationContext = getApplicationContext();
        AppDatabase.getDatabase(applicationContext)
                .merchandiseDao().findMerchandiseByOutletId(outletId)
                .map(merchandise -> {
                    for(MerchandiseImage merchandiseImage: merchandise.getMerchandiseImages()){
                        merchandiseImage.setBase64Image(Util.imageFileToBase64(new File(merchandiseImage.getPath())));
                    }
                    return merchandise;
                })
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(this::uploadMerchandise,this::error);

        return Result.success();
    }


    private void uploadMerchandise(Merchandise merchandise) {

        MerchandiseModel merchandiseModel  = new MerchandiseModel(merchandise);
        RetrofitHelper.getInstance().getApi().postMerchandise(merchandiseModel,PreferenceUtil.getInstance(getApplicationContext()).getToken())
                .observeOn(Schedulers.io()).subscribeOn(Schedulers.io()).subscribe(this::onUpload,this::error);
    }

    private void onUpload(BaseResponse baseResponse) {
        Log.i(TAG,"Uploaded");
    }

    private void error(Throwable throwable) throws IOException {

        throwable.printStackTrace();
        String errorBody = throwable.getMessage();
        if (throwable instanceof HttpException){
            HttpException error = (HttpException)throwable;
            errorBody = error.response().errorBody().string();
        }
    }
}
