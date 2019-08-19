package com.optimus.eds.ui.home;

import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.optimus.eds.BaseActivity;
import com.optimus.eds.R;
import com.optimus.eds.ui.AlertDialogManager;
import com.optimus.eds.ui.customer_complaints.CustomerComplaintsActivity;
import com.optimus.eds.ui.route.outlet.OutletListActivity;
import com.optimus.eds.utils.PreferenceUtil;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends BaseActivity {
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.nav)
    NavigationView nav;

    TextView navProfileName;

    private ActionBarDrawerToggle drawerToggle;
    private HomeViewModel viewModel;

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
        viewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        viewModel.isLoading().observe(this, this::setProgress);

        viewModel.getErrorMsg().observe(this, this::showMessage);
        viewModel.onStartDay().observe(this, aBoolean -> {
            if(aBoolean) {
                findViewById(R.id.btnStartDay).setClickable(false);
                findViewById(R.id.btnStartDay).setAlpha(0.5f);
                PreferenceUtil.getInstance(this).saveSyncDate(Calendar.getInstance().getTimeInMillis());
            }else{
                findViewById(R.id.btnEndDay).setClickable(false);
                findViewById(R.id.btnEndDay).setAlpha(0.5f);
                PreferenceUtil.getInstance(this).saveEndDate(Calendar.getInstance().getTimeInMillis());
            }
        });
        viewModel.syncedToday().observe(this, aBoolean -> {
            if(aBoolean){
                findViewById(R.id.btnStartDay).setClickable(false);
                findViewById(R.id.btnStartDay).setAlpha(0.5f);
            }
        });

        viewModel.dayEnded().observe(this, aBoolean -> {
            if(aBoolean){
                findViewById(R.id.btnEndDay).setClickable(false);
                findViewById(R.id.btnEndDay).setAlpha(0.5f);
            }
        });
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);

        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navProfileName = nav.getHeaderView(0).getRootView().findViewById(R.id.profileName);

        navProfileName.setText("Wasim Sidhu");
        nav.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            switch (id) {
                case R.id.account:
                    Toast.makeText(MainActivity.this, "My Account", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.exit:
                    AlertDialogManager.getInstance().showVerificationAlertDialog(this,
                            getString(R.string.logout), getString(R.string.are_you_sure_to_logout), verified -> {
                        if(verified)
                        {
                            PreferenceUtil.getInstance(this).clearCredentials();
                            finish();
                        }
                    });
                default:
                    return true;
            }

return false;
        });


    }

    @OnClick({R.id.btnStartDay, R.id.btnDownload, R.id.btnPlannedCall, R.id.btnReports, R.id.btnUpload, R.id.btnEndDay})
    public void onMainMenuClick(View view) {
        switch (view.getId()) {
            case R.id.btnStartDay:
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
                OutletListActivity.start(this);
                break;
            case R.id.btnReports:
                CustomerComplaintsActivity.start(this);
                break;
            case R.id.btnUpload:
                viewModel.pushOrdersToServer();
                break;
            case R.id.btnEndDay:
                AlertDialogManager.getInstance().showVerificationAlertDialog(this,getString(R.string.day_closing_title),
                        getString(R.string.end_day_msg)
                        ,verified -> {
                            if(verified)
                                viewModel.dayEnd();
                        });
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (drawerToggle.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

    private void setProgress(boolean isLoading) {
        if (isLoading) {
            showProgress();
        } else {
            hideProgress();
        }
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
