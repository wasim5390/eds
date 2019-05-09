package com.optimus.eds.ui.cash_memo;

import android.content.Context;
import android.support.design.card.MaterialCardView;
import android.util.AttributeSet;
import android.widget.TextView;

import com.optimus.eds.R;
import com.optimus.eds.db.entities.Product;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CartItemView extends MaterialCardView {

    @BindView(R.id.tvProductName)
    TextView productName;
    @BindView(R.id.tvProductQty)
    TextView productQty;
    @BindView(R.id.tvProductRate)
    TextView productRate;
    @BindView(R.id.tvProductDiscount)
    TextView productDiscount;

    @BindView(R.id.tvProductTotal)
    TextView total;

    private Product product;

    public CartItemView(Context context) {
        super(context);
    }

    public CartItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CartItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this, this);

    }

    public void setCartItem(Product item) {

        this.product = item;
        if (item != null) {
            productName.setText(product.getName());
            productQty.setText(String.valueOf(product.getQtyCarton()+"/"+product.getQtyUnit()));
            productRate.setText("0.0");
            productDiscount.setText("0.0");
            total.setText(String.valueOf(83.6*product.getQtyUnit()));
        }
    }


}
