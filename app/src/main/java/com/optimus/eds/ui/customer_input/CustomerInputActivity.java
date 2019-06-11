package com.optimus.eds.ui.customer_input;

import android.app.DatePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.optimus.eds.BaseActivity;
import com.optimus.eds.R;
import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.model.OrderModel;
import com.optimus.eds.ui.cash_memo.CashMemoViewModel;
import com.optimus.eds.ui.customer_complaints.CustomerComplaintsActivity;
import com.optimus.eds.utils.Util;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class CustomerInputActivity extends BaseActivity implements SignaturePad.OnSignedListener{

    @BindView(R.id.signaturePad)
    SignaturePad signaturePad;
    @BindView(R.id.tvName)
    TextView tvOutletName;
    @BindView(R.id.tvOrderAmount)
    TextView tvOrderAmount;

    @BindView(R.id.tvDeliveryDate)
    TextView tvDeliveryDate;
    @BindView(R.id.etMobileNumber)
    EditText etMobileNumber;
    @BindView(R.id.etCustomerRemarks)
    EditText etCustomerRemarks;

    Bitmap signature=null;
    private Long outletId;
    final Calendar calendar = Calendar.getInstance();
    private CustomerInputViewModel viewModel;


    public static void start(Context context, Long outletId) {
        Intent starter = new Intent(context, CustomerInputActivity.class);
        starter.putExtra("OutletId",outletId);
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
        viewModel = ViewModelProviders.of(this).get(CustomerInputViewModel.class);
        outletId =  getIntent().getLongExtra("OutletId",0);
        signaturePad.setMaxWidth(2);
        signaturePad.setOnSignedListener(this);
        setObserver();
        tvDeliveryDate.setText(Util.formatDate("MM/dd/yyyy",calendar.getTimeInMillis()));

    }

    private void setObserver(){
        viewModel.loadOutlet(outletId).observe(this, this::onOutletLoaded);
        viewModel.findOrder(outletId);
        viewModel.order().observe(this, orderModel -> {
            tvOrderAmount.setText(String.valueOf(orderModel.getOrder().getPayable()));


        });
    }

    private void onOutletLoaded(Outlet outlet) {
        tvOutletName.setText(outlet.getOutletName().concat(" - "+ outlet.getLocation()));
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
        tvDeliveryDate.setText(Util.formatDate("MM/dd/yyyy",calendar.getTimeInMillis()));
    };

    @OnClick(R.id.btnClearSignature)
    public void clearSignatureClick(){
        signaturePad.clear();
    }

    @OnClick(R.id.tvDeliveryDate)
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
