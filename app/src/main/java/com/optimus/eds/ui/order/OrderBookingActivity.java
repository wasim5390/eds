package com.optimus.eds.ui.order;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.optimus.eds.BaseActivity;
import com.optimus.eds.R;
import com.optimus.eds.db.entities.Order;
import com.optimus.eds.db.entities.OrderDetail;
import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.db.entities.Product;
import com.optimus.eds.db.entities.ProductGroup;
import com.optimus.eds.db.entities.Route;
import com.optimus.eds.model.PackageModel;
import com.optimus.eds.ui.cash_memo.CashMemoActivity;

import java.util.List;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;


public class OrderBookingActivity extends BaseActivity {

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

       // viewModel.getOrder().observe(this, this:: onOrderLoaded );

        viewModel.getProductGroupList().observe(this, this::onProductGroupsLoaded);

        viewModel.getProductList().observe(this, this::setSectionedAdapter);

        viewModel.isSaving().observe(this, aBoolean -> {
            Toast.makeText(this, aBoolean?"Order Saved Successfully":"Not saved", Toast.LENGTH_SHORT).show();
        });
    }

    private void onOrderLoaded(Order order) {
        if(order==null) return;
        viewModel.setOrder(order);
       // viewModel.getOrderItems(order.getOrderId()).observe(this,this::onOrderItemsLoaded);
    }

    private void onOrderItemsLoaded(List<OrderDetail> orderItems) {

    }

    private void onOutletLoaded(Outlet outlet) {
        tvOutletName.setText(outlet.getOutletName());
    }

    public void onProductGroupsLoaded(List<ProductGroup> groups) {
        ArrayAdapter userAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, groups);
        spinner.setAdapter(userAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                group = ((ProductGroup)(parent.getSelectedItem()));
                viewModel.filterProductsByGroup(group.getProductGroupId());

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


    @OnClick(R.id.btnAdd)
    public void onAddClick(){

        List<Product> orderItems = viewModel.filterOrderProducts(sectionAdapter.getCopyOfSectionsMap());
        viewModel.addOrderProducts(orderItems);

    }

    @OnClick(R.id.btnNext)
    public void onNextClick(){
        CashMemoActivity.start(this,outletId);
    }


}
