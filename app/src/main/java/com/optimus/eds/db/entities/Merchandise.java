package com.optimus.eds.db.entities;

import android.arch.persistence.room.Entity;

import com.optimus.eds.ui.route.merchandize.MerchandiseItem;

import java.io.Serializable;
import java.util.List;

/**
 * Created By apple on 4/23/19
 */

@Entity(tableName = "Merchandise")
public class Merchandise implements Serializable {

    private Long mOutletId;
    private List<MerchandiseItem> merchandiseItems;

    public Long getmOutletId() {
        return mOutletId;
    }

    public void setmOutletId(Long mOutletId) {
        this.mOutletId = mOutletId;
    }

    public List<MerchandiseItem> getMerchandiseItems() {
        return merchandiseItems;
    }

    public void setMerchandiseItems(List<MerchandiseItem> merchandiseItems) {
        this.merchandiseItems = merchandiseItems;
    }
}
