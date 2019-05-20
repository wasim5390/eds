package com.optimus.eds.ui.merchandize.coolerverification;

import android.arch.lifecycle.Observer;
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
import com.optimus.eds.ui.scanner.ScannerActivity;

import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created By apple on 4/30/19
 */
public class AssetsVerificationActivity extends BaseActivity {

    @BindView(R.id.rv_assets)
    RecyclerView recyclerView;
    @BindView(R.id.btnScanBarcode)
    Button btnScanBarcode;
    private AssetsVerificationAdapter assetsVerificationAdapter;
    private final int SCANNER_REQUEST_CODE = 0x0001;

    AssetsViewModel viewModel;

    public static void start(Context context) {
        Intent starter = new Intent(context, AssetsVerificationActivity.class);
        context.startActivity(starter);
    }

    @Override
    public int getID() {
        return R.layout.activity_asset_verification;
    }

    @Override
    public void created(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        setToolbar(getString(R.string.cooler_verification));
        initAssetsAdapter();
        viewModel = ViewModelProviders.of(this).get(AssetsViewModel.class);


        viewModel.getCoolerAssets().observe(this, new Observer<List<CoolerAsset>>() {
            @Override
            public void onChanged(@Nullable List<CoolerAsset> coolerAssets) {
                updateAssets(coolerAssets);
            }
        });

        viewModel.getAssets();


    }

    private void updateAssets(List<CoolerAsset> merchandiseItems) {
        assetsVerificationAdapter.populateAssets(merchandiseItems);
    }

    private void initAssetsAdapter() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        assetsVerificationAdapter = new AssetsVerificationAdapter(this);
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
                    break;
            }
        }
    }

}
