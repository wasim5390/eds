package com.optimus.eds.ui.merchandize.coolerverification;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.optimus.eds.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created By apple on 4/30/19
 */
public class AssetsVerificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<CoolerAsset> coolerAssetList;

    public AssetsVerificationAdapter(Context context) {
        this.mContext = context;
        this.coolerAssetList = new ArrayList<>();
    }

    public void populateAssets(List<CoolerAsset> coolerAssetList) {
        this.coolerAssetList = coolerAssetList;
        notifyDataSetChanged();
    }

    @Override
    public AssetsListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View view = layoutInflater.inflate(R.layout.assets_verification_list_item, parent, false);

        return new AssetsListHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        AssetsListHolder assetsListHolder = (AssetsListHolder) holder;
        CoolerAsset asset = coolerAssetList.get(position);

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                (mContext, R.layout.spinner_item, asset.reasons);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        assetsListHolder.reasonsSpinner.setAdapter(spinnerArrayAdapter);
    }

    @Override
    public int getItemCount() {
        return coolerAssetList.size();
    }

    static class AssetsListHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvAssetCode)
        TextView codeTextView;
        @BindView(R.id.tvStatus)
        TextView statusTextView;
        @BindView(R.id.spinnerReason)
        Spinner reasonsSpinner;

        AssetsListHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
