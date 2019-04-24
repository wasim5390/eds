package com.optimus.eds.ui.route.merchandize;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.optimus.eds.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created By apple on 4/24/19
 */
public class MerchandiseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<MerchandiseItem> merchandiseItems;

    public MerchandiseAdapter(Context context) {
        this.mContext = context;
        this.merchandiseItems = new ArrayList<>();
    }

    public void populateMerchandise(List<MerchandiseItem> merchandiseItems) {
        this.merchandiseItems = merchandiseItems;
        notifyDataSetChanged();
    }

    @Override
    public MerchandiseListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View view = layoutInflater.inflate(R.layout.merchandise_list_item, parent, false);

        return new MerchandiseListHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MerchandiseListHolder merchandiseListHolder = (MerchandiseListHolder) holder;
        MerchandiseItem merchandiseItem = merchandiseItems.get(position);
        File imgFile = new File(merchandiseItem.path);
//
//        if (imgFile.exists()) {
//            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//            merchandiseListHolder.imageView.setImageBitmap(myBitmap);
//        }

        Picasso.get().load(imgFile).fit().into(merchandiseListHolder.imageView);
        merchandiseListHolder.deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((OutletMerchandizeActivity) mContext).removeImage(merchandiseItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return merchandiseItems.size();
    }

    static class MerchandiseListHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_image)
        ImageView imageView;
        @BindView(R.id.imageView_delete)
        ImageView deleteImageView;

        MerchandiseListHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
