package com.optimus.eds.source;

import android.app.DownloadManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.optimus.eds.Constant;
import com.optimus.eds.db.AppDatabase;
import com.optimus.eds.db.entities.Merchandise;
import com.optimus.eds.model.BaseResponse;
import com.optimus.eds.model.MerchandiseModel;
import com.optimus.eds.ui.merchandize.MerchandiseImage;
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
public class UploadFileService extends IntentService implements Constant {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_UPLOAD_MEDIA = "com.optimus.eds.ui.merchandise.action.UPLOAD_MEDIA";

    private static final String EXTRA_PARAM_ID = "com.optimus.eds.ui.merchandise.extra.PARAM_ID";

    public UploadFileService() {
        super("UploadFileService");
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
        intent.setAction(ACTION_UPLOAD_MEDIA);
        intent.putExtra(EXTRA_PARAM_ID, id);
        context.startService(intent);
    }



    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPLOAD_MEDIA.equals(action)) {
                final String outletId = intent.getStringExtra(EXTRA_PARAM_ID);
                AppDatabase.getDatabase(getApplication())
                        .merchandiseDao().findMerchandiseByOutletId(Long.parseLong(outletId))
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
        }
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
        /*File file = new File(path);
        HashMap<String, RequestBody> params = new HashMap<>();
        RequestBody fBody = null;

        params.put("thumbnail", RequestBody.create(MediaType.parse("text/plain"), String.valueOf(thumbString)));
         if(type == MEDIA_IMAGE)
            fBody = RequestBody.create(MediaType.parse("image/*"), file);

        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), fBody);


        params.put("type", mediaType);
        params.put("user_id", user_id);
        params.put("contact_id",contact_id);*/

        RetrofitHelper.getInstance().getApi().postMerchandise(merchandiseModel)
                .observeOn(Schedulers.io()).subscribeOn(Schedulers.io()).subscribe(this::onUpload,this::error);
    }

    private void onUpload(BaseResponse baseResponse) {

    }


}

