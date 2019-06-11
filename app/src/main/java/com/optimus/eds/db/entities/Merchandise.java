package com.optimus.eds.db.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.optimus.eds.ui.merchandize.MerchandiseImage;

import java.io.Serializable;
import java.util.List;

/**
 * Created By apple on 4/23/19
 */

@Entity(tableName = "Merchandise")
public class Merchandise implements Serializable {

    @PrimaryKey
    @ColumnInfo(name = "outletId")
    private Long mOutletId;

    @ColumnInfo(name = "remarks")
    private String mRemarks;

    @ColumnInfo(name = "merchandiseImages")
    private List<MerchandiseImage> mMerchandiseImages;


    public Long getOutletId() {
        return mOutletId;
    }

    public void setOutletId(Long mOutletId) {
        this.mOutletId = mOutletId;
    }

    public List<MerchandiseImage> getMerchandiseImages() {
        return mMerchandiseImages;
    }

    public void setMerchandiseImages(List<MerchandiseImage> mMerchandiseImages) {
        this.mMerchandiseImages = mMerchandiseImages;
    }

    public String getRemarks() {
        return mRemarks==null?"":mRemarks;
    }

    public void setRemarks(String mRemarks) {
        this.mRemarks = mRemarks;
    }
}
