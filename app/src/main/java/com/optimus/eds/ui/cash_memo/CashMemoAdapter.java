package com.optimus.eds.ui.cash_memo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.optimus.eds.R;
import com.optimus.eds.db.entities.OrderDetail;
import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.db.entities.Product;
import com.optimus.eds.model.PackageModel;
import com.optimus.eds.ui.route.outlet.OutletListAdapter;

import java.util.ArrayList;
import java.util.List;

public class CashMemoAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context mContext;
    private List<OrderDetail> products;

    public CashMemoAdapter(Context context) {
        this.mContext = context;
        this.products = new ArrayList<>();
    }

    public void populateCartItems(List<OrderDetail> products) {
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
        OrderDetail product = products.get(position);
        ((CartItemView)viewHolder.itemView).setCartItem(product);
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
