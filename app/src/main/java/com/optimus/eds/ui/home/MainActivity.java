package com.optimus.eds.ui.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.optimus.eds.BaseActivity;
import com.optimus.eds.Constant;
import com.optimus.eds.R;
import com.optimus.eds.model.WorkStatus;
import com.optimus.eds.source.ApkDownloader;
import com.optimus.eds.source.UploadOrdersService;
import com.optimus.eds.ui.AlertDialogManager;
import com.optimus.eds.ui.AppUpdater;
import com.optimus.eds.ui.customer_complaints.CustomerComplaintsActivity;
import com.optimus.eds.ui.login.LoginActivity;
import com.optimus.eds.ui.reports.ReportsActivity;
import com.optimus.eds.ui.route.outlet.OutletListActivity;
import com.optimus.eds.utils.PermissionUtil;
import com.optimus.eds.utils.PreferenceUtil;
import com.optimus.eds.utils.Util;

import java.io.File;
import java.io.IOException;
import java.security.Permissions;
import java.util.Calendar;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;


public class MainActivity extends BaseActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.nav)
    NavigationView nav;
    @BindView(R.id.tvRunningDay)
    TextView tvRunningDay;
    private ActionBarDrawerToggle drawerToggle;
    HomeViewModel viewModel;

    public static void start(Context context) {
        Intent starter = new Intent(context, MainActivity.class);
        context.startActivity(starter);
    }

    @Override
    public int getID() {
        return R.layout.activity_home;
    }

    @Override
    public void created(Bundle savedInstanceState) {
        ButterKnife.bind(this);
       // retrieveFireBaseToken();
        viewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        setObservers();
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);

        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        TextView navProfileName = nav.getHeaderView(0).getRootView().findViewById(R.id.profileName);

        navProfileName.setText(PreferenceUtil.getInstance(this).getUsername());
        nav.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            switch (id) {
                case R.id.account:
                    Toast.makeText(MainActivity.this, getString(R.string.profile_will_avl_soon), Toast.LENGTH_SHORT).show();
                    break;
                case R.id.update:
                    PermissionUtil.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, new PermissionUtil.PermissionCallback() {
                        @Override
                        public void onPermissionsGranted(String permission) {

                        }

                        @Override
                        public void onPermissionsGranted() {
                            showProgress(true,"Downloading...");
                            viewModel.checkAppUpdate();
                        }

                        @Override
                        public void onPermissionDenied() {
                            Toast.makeText(MainActivity.this, "Need Permission to update!", Toast.LENGTH_SHORT).show();
                        }
                    });


                    break;
                case R.id.exit:
                    AlertDialogManager.getInstance().showVerificationAlertDialog(this,
                            getString(R.string.logout), getString(R.string.are_you_sure_to_logout), verified -> {
                                if(verified)
                                {
                                    PreferenceUtil.getInstance(this).clearToken();
                                    finishAffinity();
                                    LoginActivity.start(this);

                                }
                            });
                    break;
                default:
                    return true;
            }

            return false;
        });

        registerReceiver(downloadReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        viewModel.checkDayEnd();

    }

    @OnClick({R.id.btnStartDay, R.id.btnDownload, R.id.btnPlannedCall, R.id.btnReports, R.id.btnUpload, R.id.btnEndDay})
    public void onMainMenuClick(View view) {
        switch (view.getId()) {
            case R.id.btnStartDay:
                if(PreferenceUtil.getInstance(this).getWorkSyncData().isDayStarted())
                    showMessage(getString(R.string.already_started_day));
                else
                    viewModel.startDay();
                break;
            case R.id.btnDownload:
                AlertDialogManager.getInstance().showVerificationAlertDialog(this,getString(R.string.update_routes_title),
                        getString(R.string.update_routes_msg)
                        ,verified -> {
                            if(verified)
                                viewModel.download();
                        });

                break;
            case R.id.btnPlannedCall:
                if(PreferenceUtil.getInstance(this).getWorkSyncData().getDayStarted()!=1)
                {
                    showMessage(Constant.ERROR_START_DAY_FIRST);
                    return;
                }
                OutletListActivity.start(this);
                break;
            case R.id.btnReports:
                 ReportsActivity.start(this);
                break;
            case R.id.btnUpload:
                viewModel.pushOrdersToServer();
                break;
            case R.id.btnEndDay:
                if(PreferenceUtil.getInstance(this).getWorkSyncData().getDayStarted()!=1)
                {
                    showMessage(Constant.ERROR_DAY_NO_STARTED);
                    return;
                }

                viewModel.getEndDayLiveData().postValue(true);
             /*   viewModel.findOutletsWithPendingTasks().subscribe(outlets -> {
                    if(outlets.size()>0){
                        viewModel.getErrorMsg().postValue("Please complete your tasks");
                    }else{
                        viewModel.getEndDayLiveData().postValue(true);
                    }
                });*/
                break;
        }



    }

    public void setObservers(){
        viewModel.isLoading().observe(this, this::setProgress);
        viewModel.getErrorMsg().observe(this, this::showMessage);
        viewModel.onStartDay().observe(this, aBoolean -> {
            if(aBoolean) {
                findViewById(R.id.btnStartDay).setClickable(false);
                findViewById(R.id.btnStartDay).setAlpha(0.5f);
                String date = Util.formatDate(Util.DATE_FORMAT_3,PreferenceUtil.getInstance(this).getWorkSyncData().getSyncDate());
                AlertDialogManager.getInstance().
                        showAlertDialog(this, "Day Started! ( " + date+" )", "Your day has been started");

                tvRunningDay.setText("( "+date+" )");
                tvRunningDay.setVisibility(View.VISIBLE);
            }else{
                findViewById(R.id.btnStartDay).setClickable(true);
                findViewById(R.id.btnStartDay).setAlpha(1.0f);
                WorkStatus status = new WorkStatus(0);
                PreferenceUtil.getInstance(this).saveWorkSyncData(status);
                tvRunningDay.setVisibility(View.GONE);
            }
        });

        viewModel.dayStarted().observe(this, aBoolean -> {
            if(aBoolean){
                findViewById(R.id.btnStartDay).setClickable(false);
                findViewById(R.id.btnStartDay).setAlpha(0.5f);
                String date = Util.formatDate(Util.DATE_FORMAT_3,PreferenceUtil.getInstance(this).getWorkSyncData().getSyncDate());
                tvRunningDay.setText("( "+date+" )");
                tvRunningDay.setVisibility(View.VISIBLE);
            }
        });

        viewModel.dayEnded().observe(this, aBoolean -> {
            if(aBoolean){
                findViewById(R.id.btnEndDay).setClickable(false);
                findViewById(R.id.btnEndDay).setAlpha(0.5f);
                tvRunningDay.setVisibility(View.GONE);
            }
        });

        viewModel.getEndDayLiveData().observe(this,aBoolean -> {
            String endDate = Util.formatDate(Util.DATE_FORMAT_2,PreferenceUtil.getInstance(this).getWorkSyncData().getSyncDate());
            AlertDialogManager.getInstance().showVerificationAlertDialog(this,getString(R.string.day_closing_title).concat(" ( "+endDate+" )"),
                    getString(R.string.end_day_msg)
                    ,verified -> {
                        if(verified)
                            viewModel.updateDayEndStatus();
                    });
        });
        viewModel.appUpdateLiveData().observe(this,appUpdateModel -> {
            boolean appNeedToUpdate = AppUpdater.getInstance().apkChanged(appUpdateModel);
            if(appNeedToUpdate)
                ApkDownloader.getInstance().downloadApk(this,appUpdateModel);
            else
                showMessage(getString(R.string.already_updated));
        });
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (drawerToggle.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

    private void setProgress(boolean isLoading) {
        if (isLoading) {
            showProgress(true,"Loading...");
        } else {
            hideProgress();
        }
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }



    BroadcastReceiver downloadReceiver=new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

            if (downloadId == -1)
                return;

            // query download status
            Cursor cursor = downloadManager.query(new DownloadManager.Query().setFilterById(downloadId));
            if (cursor.moveToFirst()) {
                int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                if(status == DownloadManager.STATUS_SUCCESSFUL){

                    // download is successful
                    String uri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                    File file = new File(Uri.parse(uri).getPath());
                    AppUpdater.getInstance().installApk(ctxt,file);
                    hideProgress();
                }
                else {
                    hideProgress();
                    // download is assumed cancelled
                    Toast.makeText(ctxt, "Download Cancelled", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                hideProgress();
                // download is assumed cancelled
                Toast.makeText(ctxt, "Download Cancelled", Toast.LENGTH_SHORT).show();
            }

        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        AppUpdater.getInstance().deleteInstalledApkFromDownloads("VoiceChanger.apk");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (downloadReceiver != null) {
            unregisterReceiver(downloadReceiver);
        }
    }
}
