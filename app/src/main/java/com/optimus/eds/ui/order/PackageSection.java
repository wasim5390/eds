package com.optimus.eds.ui.order;

import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.optimus.eds.R;
import com.optimus.eds.db.entities.Package;
import com.optimus.eds.db.entities.Product;
import com.optimus.eds.model.PackageModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

public class PackageSection extends StatelessSection {


    String title;
    List<Product> list;


    PackageSection(PackageModel pkg) {
        super(SectionParameters.builder()
                .itemResourceId(R.layout.order_booking_item_view)
                .headerResourceId(R.layout.section_header)
                .build());
        this.title = pkg.getPackageName();
        this.list = pkg.getProducts();


    }

    public List<Product> getList() {
        return list;
    }

    @Override
    public int getContentItemsTotal() {
        return list.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ItemViewHolder itemHolder = (ItemViewHolder) holder;

        Product product = list.get(position);

        itemHolder.tvItemName.setText(product.getName());
        itemHolder.tvAvlStock.setText(product.getCartonStockInHand()+"/"+ product.getUnitStockInHand());
        itemHolder.etCartonQty.setText(product.getQtyCarton()==null?"":String.valueOf(product.getQtyCarton()));
        itemHolder.etUnitQty.setText(product.getQtyUnit()==null?"":String.valueOf(product.getQtyUnit()));

        itemHolder.etCartonQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()<1 || Long.parseLong(s.toString())<1) {
                    product.setCarton(null);
                    return;
                }
                Long qty = Long.parseLong(s.toString());
                if(qty>0){
                    product.setCarton(qty);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        itemHolder.etUnitQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()<1 || Long.parseLong(s.toString())<1) {
                    product.setUnit(null);
                    return;
                }
                Long qty = Long.parseLong(s.toString());
                if(qty>0){
                    product.setUnit(qty);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        HeaderViewHolder headerHolder = (HeaderViewHolder) holder;

        headerHolder.tvTitle.setText(title);
    }




    private class HeaderViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvTitle;

        HeaderViewHolder(View view) {
            super(view);

            tvTitle = view.findViewById(R.id.tvTitle);
        }
    }



    private class ItemViewHolder extends RecyclerView.ViewHolder {

        private final View rootView;

        private final TextView tvItemName;
        private final TextView tvAvlStock;
        private final  EditText etCartonQty;
        private final EditText etUnitQty;

        ItemViewHolder(View view) {
            super(view);

            rootView = view;
            tvItemName = view.findViewById(R.id.item_name);
            tvAvlStock = view.findViewById(R.id.avl_stock);
            etCartonQty = view.findViewById(R.id.order_carton);
            etUnitQty = view.findViewById(R.id.order_unit);

        }
    }
}