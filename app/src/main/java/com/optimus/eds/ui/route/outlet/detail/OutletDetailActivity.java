package com.optimus.eds.ui.route.outlet.detail;


import androidx.core.widget.ContentLoadingProgressBar;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatSpinner;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.optimus.eds.BaseActivity;
import com.optimus.eds.R;

import com.optimus.eds.db.entities.Outlet;

import com.optimus.eds.location_services.GpsUtils;
import com.optimus.eds.location_services.LocationService;
import com.optimus.eds.model.CustomObject;
import com.optimus.eds.ui.AlertDialogManager;
import com.optimus.eds.ui.merchandize.OutletMerchandizeActivity;
import com.optimus.eds.utils.PreferenceUtil;
import com.optimus.eds.utils.Util;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.optimus.eds.location_services.GpsUtils.GPS_REQUEST;
import static com.optimus.eds.location_services.LocationService.ACTION;
import static com.optimus.eds.location_services.LocationService.LOCATION;

public class OutletDetailActivity extends BaseActivity implements AdapterView.OnItemSelectedListener {


    private static final String TAG = OutletDetailActivity.class.getName();
    private static final int REQUEST_CODE = 0x1001;

    private Long outletId;
    @BindView(R.id.tvName)
    TextView outletName;
    @BindView(R.id.tvAddress)
    TextView outletAddress;
    @BindView(R.id.tvChannel)
    TextView outletChannel;
    @BindView(R.id.tvLastSale)
    TextView outletLastSale;
    @BindView(R.id.tvLastSaleQty)
    TextView outletSaleQty;
    @BindView(R.id.tvLastSaleDate)
    TextView outletLastSaleDate;
    @BindView(R.id.tvTotalVisit)
    TextView outletVisits;
    @BindView(R.id.pop_spinner)
    AppCompatSpinner popSpinner;
    @BindView(R.id.btnOk)
    Button btnOk;
    @BindView(R.id.progress)
    ContentLoadingProgressBar progressBar;
    OutletDetailViewModel viewModel;
    private String reasonForNoSale="";
    private boolean isGPS=false;
    private Location currentLocation = new Location("CurrentLocation");

    public static void start(Context context, Long outletId,Long routeId,int code) {
        Intent starter = new Intent(context, OutletDetailActivity.class);
        starter.putExtra("OutletId",outletId);
        starter.putExtra("RouteId",routeId);
        ((Activity)context).startActivityForResult(starter,code);
    }

    @Override
    public int getID() {
        return R.layout.activity_outlet;
    }

    @Override
    public void created(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        outletId =  getIntent().getLongExtra("OutletId",0);
        viewModel = ViewModelProviders.of(this).get(OutletDetailViewModel.class);
        showProgress();
        setToolbar(getString(R.string.outlet_summary));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item);
        adapter.addAll(getResources().getStringArray(R.array.pop_array));
        popSpinner.setAdapter(adapter);
        popSpinner.setOnItemSelectedListener(this);
        viewModel.findOutlet(outletId).observe(this, outlet -> updateUI(outlet));
        viewModel.getStatusLiveData().observe(this,integer -> updateBtn(true));
        viewModel.getOutletNearbyPos().observe(this,outletLocation -> {
            AlertDialogManager.getInstance().showLocationMissMatchAlertDialog(OutletDetailActivity.this,currentLocation,outletLocation);
        });
        viewModel.getUploadStatus().observe(this,aBoolean -> {
            if(aBoolean){
                viewModel.scheduleMasterJob(this,outletId,currentLocation, Calendar.getInstance().getTimeInMillis(),reasonForNoSale, PreferenceUtil.getInstance(getApplication()).getToken());
                finish(); // finish activity after sending status
            }else{
                OutletMerchandizeActivity.start(this,outletId,REQUEST_CODE);

            }
        });

        viewModel.loadProducts().observe(this, loaded -> {
            if (!loaded) showProgress();
            else hideProgress();
        });



        enableLocationServices();

    }
    BroadcastReceiver locationBroadcastReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {

            if (null != intent && intent.getAction().equals(ACTION)) {

               // hideProgress();

                Location locationData = intent.getParcelableExtra(LOCATION);
                currentLocation  = locationData;

            }
        }
    };
    public void enableLocationServices() {
        new GpsUtils(this, LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY))
                .turnGPSOn(isGPSEnable -> {
                    // turn on GPS
                    isGPS = isGPSEnable;
                    if(isGPS) {
                        startLocationService();
                    }
                });

    }

    private void startLocationService() {
        startService(new Intent(this, LocationService.class));
    }

    private void stopLocationService() {
        stopService(new Intent(this, LocationService.class ));
    }
    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart fired ..............");



        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()){

                            startLocationService();
                        }
                        else{
                            if(report.isAnyPermissionPermanentlyDenied())
                                openLocationSettings();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
        LocalBroadcastManager.getInstance(this).registerReceiver(locationBroadcastReceiver, new IntentFilter(ACTION));

    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(locationBroadcastReceiver);
        stopLocationService();

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int code=  getOutletPopCode(popSpinner.getSelectedItem().toString());
        viewModel.updateOutletStatusCode(code);

    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }



    private void updateUI(Outlet outlet) {
        setTitle(outlet.getOutletName());
        outletAddress.setText(outlet.getAddress());
        outletLastSale.setText(outlet.getLastSaleString());
        outletSaleQty.setText(String.valueOf(outlet.getLastSaleQuantity()));
        outletChannel.setText(String.valueOf(outlet.getChannelName()));
        outletLastSaleDate.setText(Util.formatDate(Util.DATE_FORMAT_2,outlet.getLastSaleDate()));
        outletName.setText(outlet.getOutletName().concat(" - "+ outlet.getLocation()));
        outletVisits.setText(String.valueOf(outlet.getVisitFrequency()));
        viewModel.setOutlet(outlet);
    }


    private void updateBtn(boolean enable){
        btnOk.setEnabled(enable);
        btnOk.setAlpha(enable?1.0f:0.5f);
    }

    @OnClick(R.id.btnPromotions)
    public void onPromotionsClick(){
       /* List<CustomObject> objects = new ArrayList<>();
        objects.add(new CustomObject(125L,"16 Free-345 ml with jumbo case"));
        AlertDialogManager.getInstance().showListAlertDialog(this, object -> {
            Toast.makeText(this, object.getText(), Toast.LENGTH_SHORT).show();
        }, objects);*/

    }

    @OnClick(R.id.btnOk)
    public void onOkClick(){
            viewModel.onNextClick(currentLocation);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode){
                case GPS_REQUEST:
                    isGPS = true; // flag maintain before get location
                    startLocationService();
                    break;
                case REQUEST_CODE:
                    setResult(RESULT_OK);
                    finish();
                    break;
            }

        } if(requestCode == CANCELLED){
            switch (requestCode){
                case REQUEST_CODE:
                boolean noOrderFromOrderBooking = data.getBooleanExtra(EXTRA_PARAM_NO_ORDER_FROM_BOOKING,false);
                reasonForNoSale = data.getStringExtra(EXTRA_PARAM_OUTLET_REASON_N_ORDER);
                viewModel.postOrderWithNoOrder(noOrderFromOrderBooking);
                break;
            }
        }
    }

    @Override
    public void showProgress() {
        progressBar.show();
    }

    @Override
    public void hideProgress() {
        progressBar.hide();
    }
}
