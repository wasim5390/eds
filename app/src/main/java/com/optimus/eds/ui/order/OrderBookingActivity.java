package com.optimus.eds.ui.order;

import androidx.lifecycle.ViewModelProviders;
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
import com.optimus.eds.R;
import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.db.entities.Product;
import com.optimus.eds.db.entities.ProductGroup;
import com.optimus.eds.model.PackageModel;
import com.optimus.eds.ui.AlertDialogManager;
import com.optimus.eds.ui.cash_memo.CashMemoActivity;

import java.util.List;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;


public class OrderBookingActivity extends BaseActivity implements AlertDialogManager.NoSaleReasonListener {

    @BindView(R.id.rvProducts)
    RecyclerView rvProducts;

    @BindView(R.id.group_spinner)
    AppCompatSpinner spinner;

    @BindView(R.id.tvName)
    TextView tvOutletName;
    private SectionedRecyclerViewAdapter sectionAdapter;

    private Long outletId;
    private OrderBookingViewModel viewModel;
    private ProductGroup group;

    public static void start(Context context, Long outletId) {
        Intent starter = new Intent(context, OrderBookingActivity.class);
        starter.putExtra("OutletId",outletId);
        context.startActivity(starter);
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
        viewModel = ViewModelProviders.of(this).get(OrderBookingViewModel.class);
        viewModel.setOutletId(outletId);
        setObservers();
    }

    private void setObservers(){


        viewModel.loadOutlet(outletId).observe(this, outlet -> onOutletLoaded(outlet));

        viewModel.getProductGroupList().observe(this, this::onProductGroupsLoaded);

        viewModel.getProductList().observe(this, this::setSectionedAdapter);

        viewModel.isSaving().observe(this, aBoolean -> {
            if(aBoolean)
                showProgress();
            else
                hideProgress();
        });
        viewModel.orderSaved().observe(this, aBoolean -> { if(aBoolean) CashMemoActivity.start(OrderBookingActivity.this,outletId);
        });

        viewModel.noOrderTaken().observe(this,aBoolean -> {
            if(aBoolean)
            AlertDialogManager.getInstance().showNoOrderAlertDialog(this,this::onNoSaleReasonEntered);
        });

        viewModel.showMessage().observe(this,s -> Toast.makeText(this, s, Toast.LENGTH_SHORT).show());
    }


    private void onOutletLoaded(Outlet outlet) {
        tvOutletName.setText(outlet.getOutletName().concat(" - "+ outlet.getLocation()));
    }

    public void onProductGroupsLoaded(List<ProductGroup> groups) {
        ArrayAdapter userAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, groups);
        spinner.setAdapter(userAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(group!=null)
                    onAdd(group.getProductGroupId(),false);
                viewModel.filterProductsByGroup(((ProductGroup)(parent.getSelectedItem())).getProductGroupId());
                new Handler().postDelayed(() -> {
                    group = ((ProductGroup)(parent.getSelectedItem()));
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
            sectionAdapter.addSection(pkg.getPackageName(),new PackageSection(pkg));
        }
        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        rvProducts.setAdapter(sectionAdapter);
    }



    public void onAdd(Long groupId,boolean sendToServer){
        if(sectionAdapter!=null) {
            List<Product> orderItems = viewModel.filterOrderProducts(sectionAdapter.getCopyOfSectionsMap());
            viewModel.addOrder(orderItems,groupId,sendToServer);
        }

    }

    @OnClick(R.id.btnNext)
    public void onNextClick(){
        onAdd(group.getProductGroupId(),true);
    }


    @Override
    public void onNoSaleReasonEntered(String reason) {
        Toast.makeText(this, reason, Toast.LENGTH_SHORT).show();
    }
}
