package com.optimus.eds.ui.customer_input;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.optimus.eds.BaseActivity;
import com.optimus.eds.R;
import com.optimus.eds.ui.customer_complaints.CustomerComplaintsActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class CustomerInputActivity extends BaseActivity implements SignaturePad.OnSignedListener{

    @BindView(R.id.signaturePad)
    SignaturePad signaturePad;
    Bitmap signature=null;


    public static void start(Context context) {
        Intent starter = new Intent(context, CustomerInputActivity.class);
        context.startActivity(starter);
    }
    @Override
    public int getID() {
        return R.layout.activity_customer_input;
    }

    @Override
    public void created(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        setToolbar(getString(R.string.customer_input));
        signaturePad.setMaxWidth(2);
        signaturePad.setOnSignedListener(this);

    }

    @OnClick(R.id.btnClearSignature)
    public void clearSignatureClick(){
        signaturePad.clear();
    }

    @OnClick(R.id.btnNext)
    public void navigateToComplaints(){
        CustomerComplaintsActivity.start(this);
    }
    @Override
    public void onStartSigning() {

    }

    @Override
    public void onSigned() {
        signature = signaturePad.getSignatureBitmap();
    }

    @Override
    public void onClear() {
        signature = null;
    }
}
