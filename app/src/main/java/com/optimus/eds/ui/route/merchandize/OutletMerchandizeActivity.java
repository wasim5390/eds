package com.optimus.eds.ui.route.merchandize;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.optimus.eds.BaseActivity;
import com.optimus.eds.Constant;
import com.optimus.eds.R;
import com.optimus.eds.ui.order.OrderBookingActivity;
import com.optimus.eds.utils.ImageCropperActivity;
import java.io.File;
import java.util.List;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OutletMerchandizeActivity extends BaseActivity {

    private Long outletId;
    private static final int REQUEST_CODE_IMAGE = 0x0005;

    MerchandiseViewModel viewModel;
    int type=0;

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
        viewModel = ViewModelProviders.of(this).get(MerchandiseViewModel.class);

        viewModel.getmMerchandise().observe(this, merchandiseItems -> {

        });

    }

    @OnClick(R.id.btnNext)
    public void onNextClick(){
        viewModel.insertMerchandiseIntoDB(outletId);
//        OrderBookingActivity.start(this,outletId);
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
