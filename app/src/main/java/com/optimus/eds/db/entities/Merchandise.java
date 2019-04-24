package com.optimus.eds.db.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.optimus.eds.ui.route.merchandize.MerchandiseItem;

import java.io.Serializable;
import java.util.List;

/**
 * Created By apple on 4/23/19
 */

@Entity(tableName = "Merchandise")
public class Merchandise implements Serializable {

    @PrimaryKey
    private Long mOutletId;

    private List<MerchandiseItem> mMerchandiseItems;


    public Long getOutletId() {
        return mOutletId;
    }

    public void setOutletId(Long mOutletId) {
        this.mOutletId = mOutletId;
    }

    public List<MerchandiseItem> getMerchandiseItems() {
        return mMerchandiseItems;
    }

    public void setMerchandiseItems(List<MerchandiseItem> mMerchandiseItems) {
        this.mMerchandiseItems = mMerchandiseItems;
    }
}
