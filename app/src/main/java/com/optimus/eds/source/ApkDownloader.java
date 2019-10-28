package com.optimus.eds.source;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import com.optimus.eds.model.AppUpdateModel;

import static android.content.Context.DOWNLOAD_SERVICE;

public class ApkDownloader {
    private static final ApkDownloader ourInstance = new ApkDownloader();

    public static ApkDownloader getInstance() {
        return ourInstance;
    }

    private ApkDownloader() {
    }

    public void downloadApk(Context context, AppUpdateModel updateModel){
        //String url = "https://www.apkfiles.com/storage/apkfiles.com_596431_Voice%20changer.apk?ip=103.255.5.33";
        String name = "EDS_"+updateModel.getVersion()+".apk";
        String downloadFileName = name;

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);

        Uri downloadUri = Uri.parse(updateModel.getFile());
        DownloadManager.Request request = new DownloadManager.Request(downloadUri);
        request.setTitle(name);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, downloadFileName);
        long downloadId = downloadManager.enqueue(request);
    }
}
