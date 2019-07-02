package com.optimus.eds.ui.cash_memo;

import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.optimus.eds.Constant;
import com.optimus.eds.R;
import com.optimus.eds.db.entities.OrderDetail;

import androidx.appcompat.widget.AppCompatButton;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.optimus.eds.utils.Util.formatCurrency;

public class CashMemoFreeItemView extends MaterialCardView {


    @BindView(R.id.tvProductName)
    TextView productName;
    @BindView(R.id.tvProductQty)
    TextView productQty;

    @BindView(R.id.tvProductTotal)
    TextView total;
    @BindView(R.id.btnAdd)
    AppCompatButton btnAdd;
    @BindView(R.id.btnRemove)
    AppCompatButton btnRemove;

    @BindView(R.id.rate_container)
    FrameLayout rateContainer;

    private OrderDetail order;

    FreeItemSelector mListener;

    public CashMemoFreeItemView(Context context) {
        super(context);
    }

    public CashMemoFreeItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CashMemoFreeItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this, this);

    }

    public void setCartItem(OrderDetail item, int freeQuantityTypeId,FreeItemSelector listener) {
        this.order = item;
        this.mListener = listener;
        btnAdd.setVisibility(freeQuantityTypeId== Constant.SECONDARY ?VISIBLE:GONE);
        btnRemove.setVisibility(freeQuantityTypeId==Constant.SECONDARY?VISIBLE:GONE);
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
    @OnClick({R.id.btnAdd,R.id.btnRemove})
    public void OnFreeItemUpdateClick(View view){
        switch (view.getId()){
            case R.id.btnAdd:
                mListener.onFreeItemAdd(order);
                break;
            case R.id.btnRemove:
                mListener.onFreeItemRemove(order);
                break;
        }
    }

    public interface FreeItemSelector{
        void onFreeItemAdd(OrderDetail freeItem);
        void onFreeItemRemove(OrderDetail freeItem);
    }
}
