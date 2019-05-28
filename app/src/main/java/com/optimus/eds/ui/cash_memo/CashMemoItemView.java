package com.optimus.eds.ui.cash_memo;

import android.content.Context;
import android.support.design.card.MaterialCardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.optimus.eds.R;
import com.optimus.eds.db.entities.OrderDetail;
import com.optimus.eds.db.entities.Product;

import java.text.DecimalFormat;
import java.util.Currency;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CashMemoItemView extends MaterialCardView {

    @BindView(R.id.tvProductName)
    TextView productName;
    @BindView(R.id.tvProductQty)
    TextView productQty;
    /*@BindView(R.id.tvProductRate)
    TextView productRate;*/
    @BindView(R.id.tvProductDiscount)
    TextView productDiscount;

    @BindView(R.id.tvProductTotal)
    TextView total;

    @BindView(R.id.rate_container)
    FrameLayout rateContainer;

    private OrderDetail order;

    public CashMemoItemView(Context context) {
        super(context);
    }

    public CashMemoItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CashMemoItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this, this);

    }

    public void setCartItem(OrderDetail item) {

        DecimalFormat df = new DecimalFormat("#.##");

        this.order = item;
        if (item != null) {
            productName.setText(item.getProductName());
            productQty.setText(String.valueOf(order.getCartonQuantity()+"/"+order.getUnitQuantity()));
            productDiscount.setText("0.0");
            total.setText(df.format((order.getCartonTotalPrice()+order.getUnitTotalPrice())));

            LayoutInflater inflater =  LayoutInflater.from(getContext());
            CashMemoRateView rateView = (CashMemoRateView) inflater.inflate(R.layout.memo_rate_child_view,null);
            CashMemoRateView.PriceBreakDown priceBreakDown = new CashMemoRateView.PriceBreakDown();
            priceBreakDown.setCartonPriceBreakDownList(item.getCartonPriceBreakDown());
            priceBreakDown.setUnitPriceBreakDowns(item.getUnitPriceBreakDown());
            rateView.setRates(priceBreakDown);
            rateContainer.addView(rateView);
        }
    }


}
