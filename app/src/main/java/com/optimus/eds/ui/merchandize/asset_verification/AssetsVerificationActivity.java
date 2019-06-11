package com.optimus.eds.ui.merchandize.asset_verification;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import com.optimus.eds.BaseActivity;
import com.optimus.eds.R;
import com.optimus.eds.db.entities.Asset;
import com.optimus.eds.ui.scanner.ScannerActivity;

import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created By apple on 4/30/19
 */
public class AssetsVerificationActivity extends BaseActivity implements AssetVerificationStatusListener {

    @BindView(R.id.rv_assets)
    RecyclerView recyclerView;
    @BindView(R.id.btnScanBarcode)
    Button btnScanBarcode;
    private Long outletId;
    private AssetsVerificationAdapter assetsVerificationAdapter;
    private final int SCANNER_REQUEST_CODE = 0x0001;

    AssetsViewModel viewModel;

    public static void start(Context context,Long outletId) {
        Intent starter = new Intent(context, AssetsVerificationActivity.class);
        starter.putExtra("OutletId",outletId);
        context.startActivity(starter);
    }

    @Override
    public int getID() {
        return R.layout.activity_asset_verification;
    }

    @Override
    public void created(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        setToolbar(getString(R.string.asset_verification));
        initAssetsAdapter();
        outletId =  getIntent().getLongExtra("OutletId",0);
        viewModel = ViewModelProviders.of(this).get(AssetsViewModel.class);
        viewModel.loadAssets(outletId);
        viewModel.getAssets().observe(this,assets -> updateAssets(assets));


    }

    private void updateAssets(List<Asset> assets) {
        assetsVerificationAdapter.populateAssets(assets);
    }

    private void initAssetsAdapter() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        assetsVerificationAdapter = new AssetsVerificationAdapter(this,this);
        recyclerView.setAdapter(assetsVerificationAdapter);
        recyclerView.setNestedScrollingEnabled(false);
    }

    @OnClick(R.id.btnScanBarcode)
    public void BarCodeClick(){
        Intent intent = new Intent(this, ScannerActivity.class);
        startActivityForResult(intent,SCANNER_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK){
            switch (requestCode){
                case SCANNER_REQUEST_CODE:
                    String barcode = data.getStringExtra(KEY_SCANNER_RESULT);
                    viewModel.verifyAsset(barcode);
                    break;
            }
        }
    }

    @Override
    public void onStatusChange(Asset asset) {
        viewModel.updateAsset(asset);
    }



}
