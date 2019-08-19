package com.optimus.eds.ui.cash_memo;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.TextView;

import com.optimus.eds.BaseActivity;
import com.optimus.eds.R;
import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.model.OrderDetailAndPriceBreakdown;
import com.optimus.eds.model.OrderModel;
import com.optimus.eds.ui.customer_input.CustomerInputActivity;
import com.optimus.eds.ui.route.outlet.OutletListActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.optimus.eds.utils.Util.formatCurrency;

public class CashMemoActivity extends BaseActivity {


    private static final int RES_CODE = 0x101;
    private Long outletId;


    @BindView(R.id.rvCartItems)
    RecyclerView rvCartItems;

    @BindView(R.id.tvOutletName)
    TextView tvOutletName;

    @BindView(R.id.tvGrandTotal)
    TextView tvGrandTotal;

    @BindView(R.id.tvFreeQty)
    TextView tvFreeQty;

    @BindView(R.id.tvQty)
    TextView tvQty;

    @BindView(R.id.btnNext)
    AppCompatButton btnNext;
    @BindView(R.id.btnEditOrder)
    AppCompatButton btnEditOrder;
    private CashMemoAdapter cartAdapter;
    private CashMemoViewModel viewModel;
    private boolean cashMemoEditable;
    public static void start(Context context, Long outletId,int resCode) {
        Intent starter = new Intent(context, CashMemoActivity.class);
        starter.putExtra("OutletId",outletId);
        ((Activity)context).startActivityForResult(starter,resCode);
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
        setObserver();

    }

    private void configUi(){
        if(!cashMemoEditable){
            btnNext.setVisibility(View.GONE);
            btnEditOrder.setText("Outlets");
        }
    }

    private void setObserver(){
        viewModel.loadOutlet(outletId).observe(this, this::onOutletLoaded);
        viewModel.getOrder(outletId).observe(this, orderModel -> {
            cashMemoEditable= orderModel.getOrder().getOrderStatus() != 1;
            updateCart(orderModel.getOrderDetailAndCPriceBreakdowns());
            updatePricesOnUi(orderModel);
            configUi();

        });
    }

    private void onOutletLoaded(Outlet outlet) {
        tvOutletName.setText(outlet.getOutletName().concat(" - "+ outlet.getLocation()));
    }

    private void updatePricesOnUi(OrderModel order){
        tvGrandTotal.setText(formatCurrency(order.getOrder().getPayable()));
        Long carton=0l,units=0l;
        for(OrderDetailAndPriceBreakdown detailAndPriceBreakdown:order.getOrderDetailAndCPriceBreakdowns())
        {
            Integer cQty = detailAndPriceBreakdown.getOrderDetail().getCartonQuantity();
            Integer uQty = detailAndPriceBreakdown.getOrderDetail().getUnitQuantity();
            carton+= cQty!=null?cQty:0;
            units+=uQty!=null?uQty:0;
        }
        tvQty.setText(String.valueOf(carton)+"."+String.valueOf(units));
        tvFreeQty.setText(String.valueOf(order.getFreeAvailableQty()));

    }

    private void initAdapter(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        rvCartItems.setLayoutManager(layoutManager);
        rvCartItems.setHasFixedSize(true);
        cartAdapter = new CashMemoAdapter(this);
        rvCartItems.setAdapter(cartAdapter);
        rvCartItems.setNestedScrollingEnabled(false);
    }


    private void updateCart(List<OrderDetailAndPriceBreakdown> products) {

        cartAdapter.populateCartItems(products, productsWithFreeItem -> {
            viewModel.updateOrder(productsWithFreeItem);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            switch (requestCode){
                case RES_CODE:
                    setResult(RESULT_OK);
                    finish();
                    break;
            }
        }
    }

    @OnClick(R.id.btnNext)
    public void navigateToCustomerInput(){
        CustomerInputActivity.start(this,outletId,RES_CODE);
    }

    @OnClick(R.id.btnEditOrder)
    public void upNavigate(){
        onBackPressed();
    }
}
