package com.optimus.eds.ui.route.merchandize;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.optimus.eds.BaseActivity;
import com.optimus.eds.Constant;
import com.optimus.eds.R;
import com.optimus.eds.ui.order.OrderBookingActivity;
import com.optimus.eds.ui.camera.ImageCropperActivity;
import com.optimus.eds.ui.route.merchandize.planogaram.ImageDialog;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OutletMerchandizeActivity extends BaseActivity {

    @BindView(R.id.rv_merchandise_images)
    RecyclerView recyclerView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.btnAfterMerchandize)
    Button btnAfterMerchandize;
    @BindView(R.id.btnNext)
    Button btnNext;
    private MerchandiseAdapter merchandiseAdapter;
    private Long outletId;
    private static final int REQUEST_CODE_IMAGE = 0x0005;

    MerchandiseViewModel viewModel;
    int type=0;
    ImageDialog dialogFragment;

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
        initMerchandiseAdapter();
        outletId =  getIntent().getLongExtra("OutletId",0);
        viewModel = ViewModelProviders.of(this).get(MerchandiseViewModel.class);

        viewModel.getmMerchandise().observe(this, merchandiseItems -> {
            updateMerchandiseList(merchandiseItems);
        });


        viewModel.isLoading().observe(this, this::setProgress);

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

    public void removeImage(MerchandiseItem item){
        viewModel.removeImage(item);
    }

    private void updateMerchandiseList(List<MerchandiseItem> merchandiseItems) {
        merchandiseAdapter.populateMerchandise(merchandiseItems);
    }

    private void setProgress(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
            OrderBookingActivity.start(this,outletId);
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
        viewModel.insertMerchandiseIntoDB(outletId);
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

    @OnClick(R.id.btnBeforeMerchandize)
    public void onBeforeMerchandiseClick(){
        type=0;
        actionPic(Constant.IntentExtras.ACTION_CAMERA);
    }

    @OnClick(R.id.btnAfterMerchandize)
    public void onAfterMerchandiseClick(){
        type=1;
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
                        Log.e("ImagePath",imagePath);
                        File imageFile = new File(imagePath);
                        viewModel.saveImages(imagePath,type);
                    }
                    break;
            }
        }
    }
}
