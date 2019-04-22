package com.optimus.eds.ui.order;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.optimus.eds.BaseActivity;
import com.optimus.eds.R;
import com.optimus.eds.ui.customer_input.CustomerInputActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class OrderBookingActivity extends BaseActivity {

    private Long outletId;

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
    }


    @OnClick(R.id.btnNext)
    public void onNextClick(){
        CustomerInputActivity.start(this);
    }


}
