package com.optimus.eds.ui.cash_memo;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.optimus.eds.R;
import com.optimus.eds.db.entities.OrderDetail;
import com.optimus.eds.model.OrderDetailAndPriceBreakdown;

import java.util.ArrayList;
import java.util.List;

public class CashMemoAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context mContext;
    private List<OrderDetailAndPriceBreakdown> products;

    public CashMemoAdapter(Context context) {
        this.mContext = context;
        this.products = new ArrayList<>();
    }

    public void populateCartItems(List<OrderDetailAndPriceBreakdown> products) {
        this.products = products;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());

        View view = layoutInflater.inflate(R.layout.cashmemo_item_view, parent, false);

        return new CartProductHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        OrderDetail product = products.get(position).getOrderDetail();

        ((CashMemoItemView)viewHolder.itemView).setCartItem(product, new CashMemoFreeItemView.FreeItemSelector() {
            @Override
            public void onFreeItemAdd(OrderDetail freeItem) {
                int pos = product.getCartonFreeGoods().indexOf(freeItem);
                Integer selectedCartonQty = freeItem.getSelectedCartonFreeGoodQuantity()==null?0:freeItem.getSelectedCartonFreeGoodQuantity();
                Integer selectedUnitQty = freeItem.getSelectedUnitFreeGoodQuantity()==null?0:freeItem.getSelectedUnitFreeGoodQuantity();
                if(product.getCartonFreeGoodQuantity()>selectedCartonQty){
                    selectedCartonQty++;
                    freeItem.setSelectedCartonFreeGoodQuantity(selectedCartonQty);
                    notifyDataSetChanged();
                    Toast.makeText(mContext, String.valueOf(selectedCartonQty), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFreeItemRemove(OrderDetail freeItem) {
                int pos = product.getCartonFreeGoods().indexOf(freeItem);
                Integer selectedCartonQty = freeItem.getSelectedCartonFreeGoodQuantity()==null?0:freeItem.getSelectedCartonFreeGoodQuantity();
                Integer selectedUnitQty = freeItem.getSelectedUnitFreeGoodQuantity()==null?0:freeItem.getSelectedUnitFreeGoodQuantity();
                if(selectedCartonQty>0){
                    selectedCartonQty--;
                    freeItem.setSelectedCartonFreeGoodQuantity(selectedCartonQty);
                    notifyDataSetChanged();
                    Toast.makeText(mContext, String.valueOf(selectedCartonQty), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class CartProductHolder extends RecyclerView.ViewHolder {

        View view;
        public CartProductHolder(View itemView) {
            super(itemView);
            this.view = itemView;
        }

    }
}
