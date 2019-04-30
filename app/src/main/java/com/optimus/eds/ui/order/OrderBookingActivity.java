package com.optimus.eds.ui.order;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.optimus.eds.BaseActivity;
import com.optimus.eds.R;
import com.optimus.eds.db.entities.Order;
import com.optimus.eds.db.entities.Product;
import com.optimus.eds.model.PackageModel;
import com.optimus.eds.ui.customer_input.CustomerInputActivity;

import java.util.List;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;


public class OrderBookingActivity extends BaseActivity {

    @BindView(R.id.rvProducts)
    RecyclerView rvProducts;

    private SectionedRecyclerViewAdapter sectionAdapter;

    private Long outletId;
    private OrderBookingViewModel viewModel;

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
        setObservers();
    }

    private void setObservers(){
        viewModel.getProductList().observe(this, packages -> setSectionedAdapter(packages));

        viewModel.isSaving().observe(this, aBoolean -> {
            Toast.makeText(this, aBoolean?"Order Saved Successfully":"Not saved", Toast.LENGTH_SHORT).show();
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


    @OnClick(R.id.btnNext)
    public void onAddClick(){
        List<Product> orderItems = viewModel.filterOrderProducts(sectionAdapter.getCopyOfSectionsMap());
        Order order = new Order("123",orderItems);
        viewModel.addOrder(order);
        CustomerInputActivity.start(this);
    }




}
