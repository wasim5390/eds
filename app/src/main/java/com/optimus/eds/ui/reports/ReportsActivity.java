package com.optimus.eds.ui.reports;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.optimus.eds.BaseActivity;
import com.optimus.eds.R;
import com.optimus.eds.model.ReportModel;

import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.optimus.eds.utils.Util.formatCurrency;

public class ReportsActivity extends BaseActivity {

    private ReportsViewModel viewModel;

    @BindView(R.id.tvOrderAmount)
    TextView tvGrandTotal;
    @BindView(R.id.tvOrderQty)
    TextView tvOrderQty;
    @BindView(R.id.tvCompRate)
    TextView tvCompRate;
    @BindView(R.id.tvStrikeRate)
    TextView tvStrikeRate;

    @BindView(R.id.tvAvgSku)
    TextView tvAvgSku;
    @BindView(R.id.tvDropSize)
    TextView tvDropSize;

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
            tvGrandTotal.setText(formatCurrency(reportModel.getTotalAmount()));
            tvOrderQty.setText(reportModel.getCarton() +" ("+ reportModel.getUnit()+")");
            tvPlannedCount.setText(String.valueOf(reportModel.getPjpCount()));
            tvCompletedCount.setText(String.valueOf(reportModel.getCompletedOutletsCount()));
            tvProductiveCount.setText(String.valueOf(reportModel.getProductiveOutletCount()));

            setRatio(reportModel);

        });

    }

    private void setRatio(ReportModel summary){
        int completed = summary.getCompletedOutletsCount();
        float planned  = summary.getPjpCount()==0?1:summary.getPjpCount();
        int productive = summary.getProductiveOutletCount();

        float avgSku = summary.getAvgSkuSize();
        double dropSize = summary.getDropSize();

        float compRate =(completed/planned)*100;
        float strikeRate = (productive/planned)*100;
        tvCompRate.setText(String.format("%.01f", compRate)+" %");
        tvStrikeRate.setText(String.format("%.01f", strikeRate)+" %");
        tvAvgSku.setText(String.format("%.01f", avgSku));
        tvDropSize.setText(String.format("%.01f", dropSize));
    }
}
