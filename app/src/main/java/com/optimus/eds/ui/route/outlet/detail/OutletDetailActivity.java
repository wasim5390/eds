package com.optimus.eds.ui.route.outlet.detail;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.persistence.room.util.StringUtil;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatSpinner;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.optimus.eds.BaseActivity;
import com.optimus.eds.R;

import com.optimus.eds.db.entities.Outlet;

import com.optimus.eds.ui.route.merchandize.OutletMerchandizeActivity;
import com.optimus.eds.utils.Util;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OutletDetailActivity extends BaseActivity implements AdapterView.OnItemSelectedListener {


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

    OutletDetailViewModel viewModel;

    public static void start(Context context, Long outletId,Long routeId) {
        Intent starter = new Intent(context, OutletDetailActivity.class);
        starter.putExtra("OutletId",outletId);
        starter.putExtra("RouteId",routeId);
        context.startActivity(starter);
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

        setToolbar(getString(R.string.outlet_summary));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item);
        adapter.addAll(getResources().getStringArray(R.array.pop_array));
        popSpinner.setAdapter(adapter);
        popSpinner.setOnItemSelectedListener(this);
        viewModel.findOutlet(outletId).observe(this, outlet -> onOutletLoaded(outlet));

    }



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int code=  getOutletPopCode(popSpinner.getSelectedItem().toString());
        checkOutletStatus(code);
    }

    public void checkOutletStatus(int statusCode) {

        switch (statusCode){
            case 1:
                onContinueOrder();
                break;
            case 2:
                onOutletClosed();
                break;
            case 3:
                onAreaInaccessible();
                break;
            case 4:
                onNoTime();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    private void onContinueOrder() {
        updateBtn(true);
    }


    private void onOutletClosed() {
        updateBtn(false);
    }


    private void onAreaInaccessible() {
        updateBtn(false);
    }

    private void onNoTime() {
        updateBtn(false);
    }

    private void onOutletLoaded(Outlet outlet) {
        setTitle(outlet.getOutletName());
        outletAddress.setText(outlet.getAddress());
        outletLastSale.setText(String.valueOf( outlet.getLastSale()));
        outletSaleQty.setText(String.valueOf(outlet.getLastSaleQuantity()));
        outletChannel.setText(String.valueOf(outlet.getChannelName()));
        outletLastSaleDate.setText(Util.formatDate(Util.DATE_FORMAT_2,outlet.getLastSaleDate()));
        outletName.setText(outlet.getOutletName());
        outletVisits.setText(String.valueOf(outlet.getVisitFrequency()));
    }


    private void updateBtn(boolean enable){
        btnOk.setEnabled(enable);
        btnOk.setAlpha(enable?1.0f:0.5f);
    }

    @OnClick(R.id.btnOk)
    public void onOkClick(){
        OutletMerchandizeActivity.start(this,outletId);
    }
}
