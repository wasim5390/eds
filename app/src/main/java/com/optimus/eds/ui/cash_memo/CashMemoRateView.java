package com.optimus.eds.ui.cash_memo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.optimus.eds.R;
import com.optimus.eds.db.entities.CartonPriceBreakDown;
import com.optimus.eds.db.entities.OrderDetail;
import com.optimus.eds.db.entities.UnitPriceBreakDown;
import com.optimus.eds.utils.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;

import static com.optimus.eds.utils.Util.formatCurrency;

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

    public void setRates(OrderDetail orderDetail){
        if(Util.isListEmpty(orderDetail.getUnitPriceBreakDown()) && Util.isListEmpty(orderDetail.getCartonPriceBreakDown()))
            return;
        List<CartonPriceBreakDown> cartonPriceBreakDowns = orderDetail.getCartonPriceBreakDown();
        List<UnitPriceBreakDown> unitPriceBreakDowns = orderDetail.getUnitPriceBreakDown();

        LayoutInflater inflater =  LayoutInflater.from(getContext());

        HashMap<Integer,List<Object>> listHashMap =calculate(cartonPriceBreakDowns,unitPriceBreakDowns);

        for (Map.Entry<Integer, List<Object>> entry : listHashMap.entrySet())
        {

            LinearLayout rateView = (LinearLayout) inflater.inflate(R.layout.rate_child_layout,null);
            TextView title = rateView.findViewById(R.id.productRate);
            TextView rate = rateView.findViewById(R.id.tvProductRate);
            Double unitPrice=0.0,cartonPrice=0.0;
            String type="";


            for(Object breakDown:entry.getValue()) {

                if(breakDown instanceof CartonPriceBreakDown) {
                    cartonPrice = ((CartonPriceBreakDown) breakDown).getBlockPrice();
                    type = ((CartonPriceBreakDown) breakDown).getPriceConditionType();
                }
                else {
                    unitPrice = ((UnitPriceBreakDown) breakDown).getBlockPrice();
                    type = ((UnitPriceBreakDown) breakDown).getPriceConditionType();
                }
            }
            rate.setText(formatCurrency(cartonPrice.doubleValue()) + " / "+formatCurrency(unitPrice.doubleValue()));
            title.setText(type);
            this.addView(rateView);
        }


    }


    public HashMap<Integer,List<Object>> calculate(List<CartonPriceBreakDown> cartonPriceBreakDownList,List<UnitPriceBreakDown> unitPriceBreakDownList){
        if(!Util.isListEmpty(cartonPriceBreakDownList))
            Collections.sort(cartonPriceBreakDownList,(o1, o2) -> o1.getPriceConditionClassOrder().compareTo(o2.getPriceConditionClassOrder()));
        if(!Util.isListEmpty(unitPriceBreakDownList))
            Collections.sort(unitPriceBreakDownList,(o1, o2) ->  o1.getPriceConditionClassOrder().compareTo(o2.getPriceConditionClassOrder()));


        List<Object> breakDowns = new ArrayList<>();
        breakDowns.addAll(cartonPriceBreakDownList==null?new ArrayList<>():cartonPriceBreakDownList);
        breakDowns.addAll(unitPriceBreakDownList==null?new ArrayList<>():unitPriceBreakDownList);

        HashMap<Integer, List<Object>> hashMap = new HashMap();
        for (Object breakDown:breakDowns){
            Integer key;
            if(breakDown instanceof CartonPriceBreakDown)
                key=((CartonPriceBreakDown) breakDown).getPriceConditionId();
            else
                key = ((UnitPriceBreakDown) breakDown).getPriceConditionId();

            if (!hashMap.containsKey(key)) {
                List<Object> list = new ArrayList<>();
                list.add(breakDown);

                hashMap.put(key, list);
            } else {
                hashMap.get(key).add(breakDown);
            }
        }
        return hashMap;
    }





}
