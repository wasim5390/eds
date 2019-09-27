package com.optimus.eds.ui.customer_input;

import android.app.Activity;
import android.app.DatePickerDialog;
import androidx.lifecycle.ViewModelProviders;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.optimus.eds.BaseActivity;
import com.optimus.eds.Constant;
import com.optimus.eds.R;
import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.model.MasterModel;
import com.optimus.eds.model.OrderModel;
import com.optimus.eds.utils.Util;

import java.util.Calendar;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.optimus.eds.utils.Util.formatCurrency;


public class CustomerInputActivity extends BaseActivity implements SignaturePad.OnSignedListener{

    @BindView(R.id.signaturePad)
    SignaturePad signaturePad;
    @BindView(R.id.tvName)
    TextView tvOutletName;
    @BindView(R.id.tvOrderAmount)
    TextView tvOrderAmount;

    @BindView(R.id.etMobileNumber)
    EditText etMobileNumber;
    @BindView(R.id.customer_cnic)
    EditText etCnic;
    @BindView(R.id.customer_strn)
    EditText etStrn;
    @BindView(R.id.etCustomerRemarks)
    EditText etCustomerRemarks;

    Bitmap signature=null;
    private Long outletId;
    final Calendar calendar = Calendar.getInstance();
    private CustomerInputViewModel viewModel;


    public static void start(Context context, Long outletId,int resCode) {
        Intent starter = new Intent(context, CustomerInputActivity.class);
        starter.putExtra("OutletId",outletId);
        ((Activity)context).startActivityForResult(starter,resCode);
    }

    @Override
    public int getID() {
        return R.layout.activity_customer_input;
    }

    @Override
    public void created(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        setToolbar(getString(R.string.customer_input));
        viewModel = ViewModelProviders.of(this).get(CustomerInputViewModel.class);
        outletId =  getIntent().getLongExtra("OutletId",0);
        signaturePad.setMaxWidth(2);
        signaturePad.setOnSignedListener(this);
        setObserver();


    }

    private void setObserver(){
        viewModel.loadOutlet(outletId).observe(this, this::onOutletLoaded);
        viewModel.findOrder(outletId);
        viewModel.order().observe(this, this::onOrderLoaded);
        viewModel.orderSaved().observe(this,aBoolean -> {
            if (aBoolean) {
                setResult(RESULT_OK);
                finish();
                //CustomerComplaintsActivity.start(this);
            }
        });
        viewModel.isSaving().observe(this,this::setProgress);
        viewModel.showMessage().observe(this,this::showMsg);
        LocalBroadcastManager.getInstance(this).registerReceiver(orderUploadSuccessReceiver,new IntentFilter(Constant.ACTION_ORDER_UPLOAD));

    }


    private void onOutletLoaded(Outlet outlet) {
        tvOutletName.setText(outlet.getOutletName().concat(" - "+ outlet.getLocation()));
        etMobileNumber.setText(outlet.getMobileNumber());
        etCnic.setText(outlet.getCnic());
        etStrn.setText(outlet.getStrn());
    }


    private void onOrderLoaded(OrderModel orderModel) {
        tvOrderAmount.setText(formatCurrency(orderModel.getOrder().getPayable()));
    }


    private void showMsg(String error){
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    private void setProgress(boolean isLoading) {
        if (isLoading) {
            showProgress();
        } else {
            hideProgress();
        }
    }

    private void onDatePickerClick(){
        new DatePickerDialog(this, date, calendar
                .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
    };

    @OnClick(R.id.btnClearSignature)
    public void clearSignatureClick(){
        signaturePad.clear();
    }


    public void deliveryDateClick(){
        onDatePickerClick();
    }



    @OnClick(R.id.btnNext)
    public void navigateToComplaints(){
        if(signaturePad.isEmpty())
        {
            Toast.makeText(this, "Please take customer signature", Toast.LENGTH_SHORT).show();
            return;
        }
        String mobileNumber = etMobileNumber.getText().toString();
        String remarks = etCustomerRemarks.getText().toString();
        String cnic = etCnic.getText().toString();
        String strn = etStrn.getText().toString();
        String base64Sign = Util.compressBitmap(signature);

        viewModel.saveOrder(mobileNumber,remarks,cnic,strn,base64Sign,calendar.getTimeInMillis());
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



    @Override
    protected void onDestroy() {
        if (orderUploadSuccessReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(orderUploadSuccessReceiver);
            orderUploadSuccessReceiver = null;
        }
        super.onDestroy();
    }


    private BroadcastReceiver orderUploadSuccessReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction()==Constant.ACTION_ORDER_UPLOAD){
                MasterModel response = (MasterModel) intent.getSerializableExtra("Response");
                viewModel.orderSavedSuccess(response);
                Toast.makeText(context, response.isSuccess()?"Order Uploaded Successfully!":response.getResponseMsg(), Toast.LENGTH_SHORT).show();
            }
        }
    };
}
