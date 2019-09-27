package com.optimus.eds.ui.reports;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.optimus.eds.BaseActivity;
import com.optimus.eds.R;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.optimus.eds.utils.Util.formatCurrency;

public class ReportsActivity extends BaseActivity {

    private ReportsViewModel viewModel;

    @BindView(R.id.tvSaleValue)
    TextView tvSaleValue;
    @BindView(R.id.tvCartonQty)
    TextView tvCartonQty;
    @BindView(R.id.tvUnitQty)
    TextView tvUnitQty;
    @BindView(R.id.tvOrders)
    TextView tvTotalOrders;

    @BindView(R.id.tvPlannedCount)
    TextView tvPlannedCount;
    @BindView(R.id.tvCompletedCount)
    TextView tvCompletedCount;
    @BindView(R.id.tvProductiveCount)
    TextView tvProductiveCount;

    public static void start(Context context) {
        Intent starter = new Intent(context, ReportsActivity.class);
        context.startActivity(starter);
    }
    @Override
    public int getID() {
        return R.layout.activity_reports;
    }

    @Override
    public void created(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        setToolbar(getString(R.string.reports));
        viewModel = ViewModelProviders.of(this).get(ReportsViewModel.class);
        viewModel.getReport();

        viewModel.getSummary().observe(this,reportModel -> {
            tvSaleValue.setText(formatCurrency(reportModel.getTotalSale()));
            tvCartonQty.setText(String.valueOf(reportModel.getCarton()));
            tvUnitQty.setText(String.valueOf(reportModel.getUnit()));
            tvTotalOrders.setText(String.valueOf(reportModel.getTotalOrders()));

            tvPlannedCount.setText(String.valueOf(reportModel.getPjpCount()));
            tvCompletedCount.setText(String.valueOf(reportModel.getCompletedTaskCount()));
            tvProductiveCount.setText(String.valueOf(reportModel.getProductiveOutletCount()));
        });

    }
}
