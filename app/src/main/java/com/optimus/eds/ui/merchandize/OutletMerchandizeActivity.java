package com.optimus.eds.ui.merchandize;

import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.optimus.eds.BaseActivity;
import com.optimus.eds.Constant;
import com.optimus.eds.R;
import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.ui.order.OrderBookingActivity;
import com.optimus.eds.ui.camera.ImageCropperActivity;
import com.optimus.eds.ui.merchandize.asset_verification.AssetsVerificationActivity;
import com.optimus.eds.ui.merchandize.planogaram.ImageDialog;
import com.optimus.eds.utils.Util;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.zelory.compressor.Compressor;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class OutletMerchandizeActivity extends BaseActivity {

    @BindView(R.id.rv_merchandise_images)
    RecyclerView recyclerView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.btnAfterMerchandize)
    Button btnAfterMerchandize;
    @BindView(R.id.btnNext)
    Button btnNext;
    @BindView(R.id.tvName)
    TextView tvOutletName;
    @BindView(R.id.etRemarks)
    EditText etRemarks;
    private MerchandiseAdapter merchandiseAdapter;
    private Long outletId;
    private static final int REQUEST_CODE_IMAGE = 0x0005;
    private static final int REQUEST_CODE=0x1100;

    MerchandiseViewModel viewModel;
    int type=0;
    ImageDialog dialogFragment;

    public static void start(Context context,Long outletId, int requestCode) {
        Intent starter = new Intent(context, OutletMerchandizeActivity.class);
        starter.putExtra("OutletId",outletId);
        ((Activity)context).startActivityForResult(starter,requestCode);
    }

    @Override
    public int getID() {
        return R.layout.activity_merchandize;
    }

    @Override
    public void created(Bundle savedInstanceState) {

        ButterKnife.bind(this);
        setToolbar(getString(R.string.merchandizing));
        initMerchandiseAdapter();
        outletId =  getIntent().getLongExtra("OutletId",0);
        viewModel = ViewModelProviders.of(this).get(MerchandiseViewModel.class);
        viewModel.loadOutlet(outletId).observe(this, outlet -> onOutletLoaded(outlet));
        viewModel.loadAssets(outletId);
        viewModel.loadMerchandise(outletId).observe(this,merchandise -> {
            etRemarks.setText(merchandise.getRemarks());
            updateMerchandiseList(merchandise.getMerchandiseImages());

        });

        viewModel.getMerchandiseImages().observe(this, merchandiseItems -> {
            updateMerchandiseList(merchandiseItems);
        });


        viewModel.isSaved().observe(this, aBoolean -> {
            if(aBoolean){
                OrderBookingActivity.start(OutletMerchandizeActivity.this,outletId,REQUEST_CODE);
            }
        });
        viewModel.isInProgress().observe(this, this::setProgress);

        viewModel.enableAfterMerchandiseButton().observe(this, aBoolean -> {
            btnAfterMerchandize.setEnabled(aBoolean);
            btnAfterMerchandize.setAlpha(aBoolean?1.0f:0.5f);
        });
        viewModel.enableNextButton().observe(this, aBoolean -> {
            btnNext.setEnabled(aBoolean);
            btnNext.setAlpha(aBoolean?1.0f:0.5f);
        });

        viewModel.lessImages().observe(this, aBoolean ->{
            Toast.makeText(OutletMerchandizeActivity.this,"At least 3 images required",Toast.LENGTH_LONG).show();
        });
    }
    private void onOutletLoaded(Outlet outlet) {
        tvOutletName.setText(outlet.getOutletName().concat(" - "+ outlet.getLocation()));

    }
    public void removeImage(MerchandiseImage item){
        viewModel.removeImage(item);

    }

    private void updateMerchandiseList(List<MerchandiseImage> merchandiseImages) {
        merchandiseAdapter.populateMerchandise(merchandiseImages);
    }

    private void setProgress(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void initMerchandiseAdapter() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        merchandiseAdapter = new MerchandiseAdapter(this);
        recyclerView.setAdapter(merchandiseAdapter);
        recyclerView.setNestedScrollingEnabled(false);
    }

    @OnClick(R.id.btnNext)
    public void onNextClick(){
        String remarks = etRemarks.getText().toString();
        viewModel.insertMerchandiseIntoDB(outletId,remarks);
    }


    @OnClick(R.id.btnShowPlanogram)
    public void showPlanogram(){

        viewModel.getImages();

        viewModel.getPlanogaram().observe(this, strings -> {
            FragmentManager fm = getSupportFragmentManager();
            dialogFragment = ImageDialog.newInstance(strings);
            dialogFragment.show(fm, "Dialog");
        });
    }

    @OnClick(R.id.btnAssetVerification)
    public void coolerVerification(){
        AssetsVerificationActivity.start(this,outletId);
    }

    @OnClick(R.id.btnBeforeMerchandize)
    public void onBeforeMerchandiseClick(){
        type= MerchandiseImgType.BEFORE_MERCHANDISE;
        actionPic(Constant.IntentExtras.ACTION_CAMERA);
    }

    @OnClick(R.id.btnAfterMerchandize)
    public void onAfterMerchandiseClick(){
        type=MerchandiseImgType.AFTER_MERCHANDISE;
        actionPic(Constant.IntentExtras.ACTION_CAMERA);
    }

    /**
     * Navigate to ImageCropperActivity with provided action {camera-action,gallery-action}
     * @param action
     */
    private void actionPic(String action) {
        Intent intent = new  Intent(this, ImageCropperActivity.class);
        intent.putExtra("ACTION", action);
        startActivityForResult(intent, REQUEST_CODE_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK){
            switch (requestCode){

                case REQUEST_CODE_IMAGE:
                    String imagePath = data.getStringExtra(Constant.IntentExtras.IMAGE_PATH);
                    if(imagePath!=null) {
                        // @TODO send to server and show in adapter
                        compress(imagePath,type);
                    }
                    break;
                case REQUEST_CODE:
                    setResult(RESULT_OK,data);
                    finish();
                    break;
            }
        }
    }

    public void compress(String actualImagePath,int type){
        File actualImage = new File(actualImagePath);
        new Compressor(this)
                .compressToFileAsFlowable(actualImage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        file ->{
                            if(Util.moveFile(file,actualImage.getParentFile()))
                                viewModel.saveImages(actualImage.getPath(),type);
                        }, throwable -> {

                            throwable.printStackTrace();
                            Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        });
    }


}
