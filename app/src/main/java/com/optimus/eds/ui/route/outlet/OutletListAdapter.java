package com.optimus.eds.ui.route.outlet;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.optimus.eds.R;
import com.optimus.eds.db.entities.Outlet;


import java.util.ArrayList;
import java.util.List;

public class OutletListAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private Context mContext;
    private Callback mCallback;
    private List<Outlet> outlets;
    private List<Outlet> outletsFiltered;

    public OutletListAdapter(Context context, OutletListAdapter.Callback callback) {
        this.mCallback = callback;
        this.mContext = context;
        this.outlets = new ArrayList<>();
    }

    public OutletListAdapter(Context context, List<Outlet> outlets,OutletListAdapter.Callback callback) {
        this.outlets = new ArrayList<>();
        this.outletsFiltered = new ArrayList<>();
        this.mCallback = callback;
        this.mContext = context;
        this.outlets = outlets;
        this.outletsFiltered = outlets;
    }

    public void populateOutlets(List<Outlet> outlets) {
        this.outlets = outlets;
        this.outletsFiltered=outlets;
        notifyDataSetChanged();

    }

    @Override
    public OutletListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());

        View view = layoutInflater.inflate(R.layout.outlet_list_item, parent, false);

        return new OutletListHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Outlet outlet = outletsFiltered.get(position);
        ((OutletListItemView)holder.itemView).setOutlet(outlet,mCallback);

    }

    @Override
    public int getItemCount() {
        return outletsFiltered.size();
    }

    static class OutletListHolder extends RecyclerView.ViewHolder {

        View view;
        public OutletListHolder(View itemView) {
            super(itemView);
            this.view = itemView;
        }

    }




    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    outletsFiltered = outlets;
                } else {
                    List<Outlet> filteredList = new ArrayList<>();
                    for (Outlet row : outlets) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getOutletName().toLowerCase().contains(charString.toLowerCase()) || row.getOutletCode().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    outletsFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = outletsFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                outletsFiltered = (ArrayList<Outlet>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
    interface Callback{
        void onOutletClick(Outlet outlet);
    }
}
