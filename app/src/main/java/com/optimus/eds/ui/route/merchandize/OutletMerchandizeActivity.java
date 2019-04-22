package com.optimus.eds.ui.route.merchandize;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import com.optimus.eds.BaseActivity;
import com.optimus.eds.R;
import com.optimus.eds.ui.customer_input.CustomerInputActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class OutletMerchandizeActivity extends BaseActivity {

    private Long outletId;

    public static void start(Context context,Long outletId) {
        Intent starter = new Intent(context, OutletMerchandizeActivity.class);
        starter.putExtra("OutletId",outletId);
        context.startActivity(starter);
    }

    @Override
    public int getID() {
        return R.layout.activity_merchandize;
    }

    @Override
    public void created(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        setToolbar(getString(R.string.merchandizing));
        outletId =  getIntent().getLongExtra("OutletId",0);
    }


    @OnClick(R.id.btnNext)
    public void onNextClick(){
        CustomerInputActivity.start(this);
    }


}
