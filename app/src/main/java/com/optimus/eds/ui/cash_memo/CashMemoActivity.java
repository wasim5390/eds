package com.optimus.eds.ui.cash_memo;

import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;

import com.optimus.eds.BaseActivity;
import com.optimus.eds.R;
import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.model.OrderDetailAndPriceBreakdown;
import com.optimus.eds.model.OrderModel;
import com.optimus.eds.ui.customer_input.CustomerInputActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.optimus.eds.utils.Util.formatCurrency;

public class CashMemoActivity extends BaseActivity {


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
        setObserver();

    }

    private void setObserver(){
        viewModel.loadOutlet(outletId).observe(this, this::onOutletLoaded);
        viewModel.getOrder(outletId).observe(this, orderModel -> {

            updateCart(orderModel.getOrderDetailAndCPriceBreakdowns());
            updatePricesOnUi(orderModel);

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
            Long cQty = detailAndPriceBreakdown.getOrderDetail().getCartonQuantity();
            Long uQty = detailAndPriceBreakdown.getOrderDetail().getUnitQuantity();
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

    @OnClick(R.id.btnNext)
    public void navigateToCustomerInput(){
        CustomerInputActivity.start(this,outletId);
    }

    @OnClick(R.id.btnAddPackages)
    public void upNavigate(){
        onBackPressed();
    }
}
