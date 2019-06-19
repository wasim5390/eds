package com.optimus.eds.source;

import android.app.DownloadManager;
import android.app.IntentService;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

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
import java.util.HashMap;

import io.reactivex.MaybeObserver;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.HttpException;
import retrofit2.Retrofit;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class UploadFileService extends JobService implements Constant {

    private final String iTAG = UploadFileService.class.getSimpleName();

    public UploadFileService() {

    }

    @Override
    public boolean onStartJob(JobParameters params) {
        if (params != null) {
            PersistableBundle bundle = params.getExtras();

            final Long outletId = bundle.getLong(EXTRA_PARAM_OUTLET_ID);
            AppDatabase.getDatabase(getApplication())
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

        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void uploadMedia(Context context, String id) {
        Intent intent = new Intent(context, UploadFileService.class);
        intent.putExtra(EXTRA_PARAM_OUTLET_ID, id);
        context.startService(intent);
    }


    private void error(Throwable throwable) throws IOException {

        throwable.printStackTrace();
        String errorBody = throwable.getMessage();
        if (throwable instanceof HttpException){
            HttpException error = (HttpException)throwable;
            errorBody = error.response().errorBody().string();
        }
        UploadFileService.this.stopSelf();
    }


    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void uploadMerchandise(Merchandise merchandise) {

        MerchandiseModel merchandiseModel  = new MerchandiseModel(merchandise);


        RetrofitHelper.getInstance().getApi().postMerchandise(merchandiseModel, PreferenceUtil.getInstance(getApplication()).getToken())
                .observeOn(Schedulers.io()).subscribeOn(Schedulers.io()).subscribe(this::onUpload,this::error);
    }

    private void onUpload(BaseResponse baseResponse) {
        Log.i(iTAG,"File Uploaded");

    }


}

