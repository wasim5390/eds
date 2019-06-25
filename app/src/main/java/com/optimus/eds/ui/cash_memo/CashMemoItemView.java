package com.optimus.eds.ui.cash_memo;

import android.content.Context;
import com.google.android.material.card.MaterialCardView;

import android.os.Build;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.optimus.eds.R;
import com.optimus.eds.db.entities.OrderDetail;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.optimus.eds.utils.Util.formatCurrency;

public class CashMemoItemView extends MaterialCardView {

    @BindView(R.id.tvProductName)
    TextView productName;
    @BindView(R.id.tvProductQty)
    TextView productQty;
    /*@BindView(R.id.tvProductRate)
    TextView productRate;*/
  /*  @BindView(R.id.tvProductDiscount)
    TextView productDiscount;*/

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

        this.order = item;
        if (item != null) {
            Double totalPrice = order.getCartonTotalPrice()+order.getUnitTotalPrice();
            String free = "";
            free = totalPrice>0?"":" &#127379;";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                productName.setText(Html.fromHtml(item.getProductName().concat(free),Html.FROM_HTML_MODE_LEGACY));
            }else{
                productName.setText(Html.fromHtml(item.getProductName().concat(free)));
            }
            productQty.setText(order.getQuantity());
            total.setText(formatCurrency(totalPrice));

            rateContainer.addView(addPricingView(item));
        }
    }


    private View addPricingView(OrderDetail item){
        LayoutInflater inflater =  LayoutInflater.from(getContext());
        CashMemoRateView rateView = (CashMemoRateView) inflater.inflate(R.layout.memo_rate_child_view,null);

        rateView.setRates(item);
        return rateView;
    }


}
