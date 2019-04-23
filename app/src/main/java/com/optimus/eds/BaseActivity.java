package com.optimus.eds;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;


import com.optimus.eds.db.AppDatabase;
import com.optimus.eds.ui.ProgressFragmentDialog;


/**
 * Created by sidhu on 4/10/2019.
 */

public abstract class BaseActivity extends AppCompatActivity implements Constant{

    public abstract int getID();

    public abstract void created(Bundle savedInstanceState);

    private ProgressFragmentDialog pd;
    private AppDatabase db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    protected AppDatabase getDb(){
        return AppDatabase.getDatabase(getApplication());
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

    /**** show progress *******************/

    public void showProgress() {

        if (pd==null) {
            pd = ProgressFragmentDialog.newInstance();
        }
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




}
