package com.optimus.eds.ui.route.outlet;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.widget.TextView;

import com.optimus.eds.R;
import com.optimus.eds.db.entities.Outlet;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OutletListItemView extends ConstraintLayout {


    @BindView(R.id.outletName)
    TextView outletName;
    @BindView(R.id.outletCode)
    TextView outletCode;
    @BindView(R.id.orderAmount)
    TextView orderAmount;

    private OutletListAdapter.Callback callback;
    private Outlet outletItem;

    public OutletListItemView(Context context) {
        super(context);
    }

    public OutletListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OutletListItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this, this);

    }

    public void setOutlet(Outlet item, OutletListAdapter.Callback callback) {
        this.callback = callback;
        this.outletItem = item;
        if (item != null) {

            outletName.setText(outletItem.getOutletName());
            outletCode.setText(getResources().getString(R.string.outlet_code,outletItem.getOutletCode()));
            orderAmount.setText("RS. "+ outletItem.getTotalAmount());
        }
    }

    @OnClick(R.id.outletView)
    public void onOutletClick(){
        callback.onOutletClick(outletItem);
    }

}
