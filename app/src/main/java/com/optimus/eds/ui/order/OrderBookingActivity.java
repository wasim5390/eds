package com.optimus.eds.ui.order;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.optimus.eds.BaseActivity;
import com.optimus.eds.Constant;
import com.optimus.eds.R;
import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.db.entities.Product;
import com.optimus.eds.db.entities.ProductGroup;
import com.optimus.eds.model.CustomObject;
import com.optimus.eds.model.PackageModel;
import com.optimus.eds.source.JobIdManager;
import com.optimus.eds.ui.AlertDialogManager;
import com.optimus.eds.ui.cash_memo.CashMemoActivity;

import java.util.ArrayList;
import java.util.List;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;


public class OrderBookingActivity extends BaseActivity  {

    private static final int RES_CODE = 0x101;
    @BindView(R.id.rvProducts)
    RecyclerView rvProducts;

    @BindView(R.id.group_spinner)
    AppCompatSpinner spinner;
    @BindView(R.id.tvName)
    TextView tvOutletName;
    private SectionedRecyclerViewAdapter sectionAdapter;
    private Outlet outlet;

    private Long outletId;
    private OrderBookingViewModel viewModel;
    private ProductGroup group;
    private List<CustomObject> noOrderReasonList;

    public static void start(Context context, Long outletId,int requestCode) {
        Intent starter = new Intent(context, OrderBookingActivity.class);
        starter.putExtra("OutletId",outletId);
        ((Activity)context).startActivityForResult(starter,requestCode);
    }

    @Override
    public int getID() {
        return R.layout.activity_order_booking;
    }

    @Override
    public void created(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        outletId =  getIntent().getLongExtra("OutletId",0);
        setToolbar(getString(R.string.order_booking));
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        viewModel = ViewModelProviders.of(this).get(OrderBookingViewModel.class);
        viewModel.setOutletId(outletId);
        createNoOrderReasonList();
        setObservers();
    }

    private void createNoOrderReasonList(){
        noOrderReasonList = new ArrayList<>();
        noOrderReasonList.add(new CustomObject(1L,"Sufficient Stock"));
        noOrderReasonList.add(new CustomObject(2L,"Price Variation"));
        noOrderReasonList.add(new CustomObject(3L,"Buying from WS"));
        noOrderReasonList.add(new CustomObject(4L,"Out of Cash"));
        noOrderReasonList.add(new CustomObject(5L,"Dispute"));
    }

    private void setObservers(){

        viewModel.loadOutlet(outletId).observe(this, this::onOutletLoaded);

        viewModel.getProductGroupList().observe(this, this::onProductGroupsLoaded);

        viewModel.getProductList().observe(this, this::setSectionedAdapter);

        viewModel.isSaving().observe(this, aBoolean -> {
            if(aBoolean)
                showProgress();
            else
                hideProgress();
        });
        viewModel.orderSaved().observe(this, aBoolean -> { if(aBoolean) CashMemoActivity.start(OrderBookingActivity.this,outletId,RES_CODE);
        });

        viewModel.noOrderTaken().observe(this,aBoolean -> {
            if(aBoolean){
                AlertDialogManager.getInstance().showVerificationAlertDialog(this,
                        getString(R.string.checkout_without_order),
                        getString(R.string.checkout_without_order_msg),
                        verified -> {
                            if(verified)
                                pickReasonForNoOrder();
                        });
            }

        });

        viewModel.showMessage().observe(this,s -> Toast.makeText(this, s, Toast.LENGTH_SHORT).show());
    }

    private void pickReasonForNoOrder(){
        AlertDialogManager.getInstance().showListAlertDialog(this,getString(R.string.no_order_reason),
                object -> {
                    onNoOrderReasonSelected(object);
                },noOrderReasonList);
    }


    private void onOutletLoaded(Outlet outlet) {
        this.outlet = outlet;
        tvOutletName.setText(outlet.getOutletName().concat(" - "+ outlet.getLocation()));
        JobIdManager.cancelJob(this,outlet.getOutletId().intValue());
    }

    private void onProductGroupsLoaded(List<ProductGroup> groups) {
        ArrayAdapter userAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, groups);
        spinner.setAdapter(userAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(group!=null)
                    onAdd(group.getProductGroupId(),false);
                viewModel.filterProductsByGroup(((ProductGroup)(parent.getSelectedItem())).getProductGroupId());
                new Handler().postDelayed(() -> {
                    group = ((ProductGroup)(parent.getSelectedItem()));
                    findViewById(R.id.btnNext).setAlpha(1.0f);
                    findViewById(R.id.btnNext).setClickable(true);
                },1000);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    private void setSectionedAdapter(List<PackageModel> packages){
        sectionAdapter = new SectionedRecyclerViewAdapter();
        for(PackageModel pkg:packages){
            sectionAdapter.addSection(pkg.getPackageName(),new PackageSection(pkg,
                    () -> Toast.makeText(OrderBookingActivity.this, "You cannot enter above maximum qty", Toast.LENGTH_LONG).show()));
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvProducts.setLayoutManager(linearLayoutManager);
        rvProducts.setAdapter(sectionAdapter);
    }



    private void onAdd(Long groupId, boolean sendToServer){
        if(sectionAdapter!=null) {
            List<Product> orderItems = viewModel.filterOrderProducts(sectionAdapter.getCopyOfSectionsMap());
            viewModel.addOrder(orderItems,groupId,sendToServer);
        }

    }

    @OnClick(R.id.btnNext)
    public void onNextClick(){
        if(group!=null)
            onAdd(group.getProductGroupId(),true);
    }



    public void onNoOrderReasonSelected(CustomObject object) {
        Intent intent = getIntent();
        intent.putExtra(EXTRA_PARAM_OUTLET_REASON_N_ORDER,object.getId());
        intent.putExtra(Constant.EXTRA_PARAM_NO_ORDER_FROM_BOOKING,true);
        setResult(RESULT_FIRST_USER,intent);
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){
            switch (requestCode){
                case RES_CODE:
                    setResult(RESULT_OK);
                    finish();
                    break;
            }
        }
    }

    interface QtySelectionCallback{
        void onInvalidQtyEntered();
    }

    @Override
    public void onBackPressed() {
        if(outlet!=null){
            if(outlet.getVisitStatus()==1) {
                Toast.makeText(this, "Complete order or checkout without order!", Toast.LENGTH_SHORT).show();
                return;
            }
            super.onBackPressed();

        }
    }
}
