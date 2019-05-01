package com.optimus.eds.ui.cash_memo;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.optimus.eds.BaseActivity;
import com.optimus.eds.R;
import com.optimus.eds.db.entities.Product;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CashMemoActivity extends BaseActivity {


    private Long outletId;


    @BindView(R.id.rvCartItems)
    RecyclerView rvCartItems;

    private CashMemoAdapter cartAdapter;
    private CashMemoViewModel viewModel;
    public static void start(Context context, Long outletId) {
        Intent starter = new Intent(context, CashMemoActivity.class);
        starter.putExtra("OutletId",outletId);
        context.startActivity(starter);
    }


    @Override
    public int getID() {
        return R.layout.activity_cash_memo;
    }

    @Override
    public void created(Bundle savedInstanceState) {
        outletId =  getIntent().getLongExtra("OutletId",0);
        ButterKnife.bind(this);
        setToolbar(getString(R.string.cash_memo));
        viewModel = ViewModelProviders.of(this).get(CashMemoViewModel.class);
        initAdapter();
        viewModel.getCartProducts().observe(this, products -> {
            updateCart(products);
        });


    }

    private void initAdapter(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvCartItems.setLayoutManager(layoutManager);
        rvCartItems.setHasFixedSize(true);
        cartAdapter = new CashMemoAdapter(this);
        rvCartItems.setAdapter(cartAdapter);
        rvCartItems.setNestedScrollingEnabled(false);
    }


    private void updateCart(List<Product> products) {
        cartAdapter.populateCartItems(products);
    }
}
