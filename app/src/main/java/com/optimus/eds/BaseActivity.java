package com.optimus.eds;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;


import com.optimus.eds.db.AppDatabase;
import com.optimus.eds.model.MasterModel;
import com.optimus.eds.ui.ProgressFragmentDialog;


/**
 * Created by sidhu on 4/10/2019.
 */

public abstract class BaseActivity extends AppCompatActivity implements Constant{

    public abstract int getID();

    public abstract void created(Bundle savedInstanceState);

    private ProgressFragmentDialog pd;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(getID());
        created(savedInstanceState);

    }

    public void setToolbar(String title) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(title);
    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Showing Alert Dialog with Settings option
     * Navigates user to app settings
     */
    public void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.location_dialog_title);
        builder.setMessage(R.string.location_dialog_message);
        builder.setPositiveButton("GOTO SETTINGS", (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();

    }

    protected void showSettingsDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();

    }

    // navigating user to app settings
    public void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    // navigating user to app settings
    public void openLocationSettings() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    /**** show progress *******************/

    public void showProgress() {

        if (pd==null) {
            pd = ProgressFragmentDialog.newInstance();
        }
        pd.show(getSupportFragmentManager(), "TAG");

    }

    public void showProgress(boolean cancelable) {

        if (pd==null) {
            pd = ProgressFragmentDialog.newInstance();
        }
        pd.setCancelable(cancelable);
        pd.show(getSupportFragmentManager(), "TAG");

    }

    /******************* hide progress ***********************/

    public void hideProgress() {
        if (pd != null) {
            pd.dismiss();
        }

    }

    /**
     * Retrieves Outlet availability status.
     * @param statusString
     * @return
     */
    public int getOutletPopCode(String statusString) {
        String[] statusArray = getResources().getStringArray(R.array.pop_array);
        for (int i = 0; i < statusArray.length; i++)
        {
            if(statusArray[i].equalsIgnoreCase(statusString))
                return ++i;
        }
        return 1;

    }


    protected BroadcastReceiver orderUploadSuccessReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction()==Constant.ACTION_ORDER_UPLOAD){
                MasterModel response = (MasterModel) intent.getSerializableExtra("Response");
                Toast.makeText(context, response.isSuccess()?"Order Uploaded Successfully!":response.getResponseMsg(), Toast.LENGTH_SHORT).show();

            }
        }
    };

}
