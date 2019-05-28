package com.optimus.eds.ui.cash_memo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.optimus.eds.R;
import com.optimus.eds.db.entities.CartonPriceBreakDown;
import com.optimus.eds.db.entities.UnitPriceBreakDown;
import com.optimus.eds.utils.Util;

import java.util.List;

import butterknife.ButterKnife;

public class CashMemoRateView extends LinearLayout {


    public CashMemoRateView(Context context) {
        super(context);
    }

    public CashMemoRateView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CashMemoRateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this, this);

    }

    public void setRates(PriceBreakDown priceBreakDown){
        if(priceBreakDown.isPriceEmpty())
            return;
        if(Util.isListEmpty(priceBreakDown.getCartonPriceBreakDownList()))
            return;
        LayoutInflater inflater =  LayoutInflater.from(getContext());

        for(CartonPriceBreakDown priceBreakDown1:priceBreakDown.getCartonPriceBreakDownList()){
            LinearLayout rateView = (LinearLayout) inflater.inflate(R.layout.rate_child_layout,null);
            TextView title = (TextView)rateView.findViewById(R.id.productRate);
            TextView rate = (TextView)rateView.findViewById(R.id.tvProductRate);
            rate.setText(priceBreakDown1.getBlockPrice()+"");
            title.setText(priceBreakDown1.getPriceConditionType());
            this.addView(rate);
        }
    }

    public static class PriceBreakDown{
        public List<UnitPriceBreakDown> getUnitPriceBreakDowns() {
            return unitPriceBreakDowns;
        }

        public void setUnitPriceBreakDowns(List<UnitPriceBreakDown> unitPriceBreakDowns) {
            this.unitPriceBreakDowns = unitPriceBreakDowns;
        }

        public List<CartonPriceBreakDown> getCartonPriceBreakDownList() {
            return cartonPriceBreakDownList;
        }

        public void setCartonPriceBreakDownList(List<CartonPriceBreakDown> cartonPriceBreakDownList) {
            this.cartonPriceBreakDownList = cartonPriceBreakDownList;
        }

        List<UnitPriceBreakDown> unitPriceBreakDowns;
        List<CartonPriceBreakDown> cartonPriceBreakDownList;

        public boolean isPriceEmpty(){
            return (Util.isListEmpty(getCartonPriceBreakDownList()) && Util.isListEmpty(getUnitPriceBreakDowns()));
        }

    }
}
