package com.optimus.eds.ui.home;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;

import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.optimus.eds.BaseActivity;
import com.optimus.eds.R;
import com.optimus.eds.ui.customer_complaints.CustomerComplaintsActivity;
import com.optimus.eds.ui.route.outlet.OutletListActivity;

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


    @Override
    public int getID() {
        return R.layout.activity_home;
    }

    @Override
    public void created(Bundle savedInstanceState) {
        ButterKnife.bind(this);

        viewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        //  viewModel.onScreenCreated();
          viewModel.isLoading().observe(this, this::setProgress);

        viewModel.getErrorMsg().observe(this, this::showError);

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

                case R.id.exit:
                    Toast.makeText(MainActivity.this, "Logout", Toast.LENGTH_SHORT).show();
                default:
                    return true;
            }


        });


    }

    @OnClick({R.id.btnStartDay, R.id.btnDownload, R.id.btnPlannedCall, R.id.btnReports, R.id.btnUpload, R.id.btnEndDay})
    public void onMainMenuClick(View view) {
        switch (view.getId()) {
            case R.id.btnStartDay:
                viewModel.startDay();
                break;
            case R.id.btnPlannedCall:
                OutletListActivity.start(this);
                break;
            case R.id.btnReports:
                CustomerComplaintsActivity.start(this);
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

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
