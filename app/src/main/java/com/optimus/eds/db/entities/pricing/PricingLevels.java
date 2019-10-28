package com.optimus.eds.db.entities.pricing;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class PricingLevels implements Serializable {

    @SerializedName("pricingLevelId")
    @PrimaryKey
    private int pricingLevelId;
    @SerializedName("value")
    @Nullable
    private String value;

    public PricingLevels() {
    }
    public PricingLevels(int pricingLevelId, @Nullable String value) {
        this.pricingLevelId = pricingLevelId;
        this.value = value;
    }


    public int getPricingLevelId() {
        return pricingLevelId;
    }

    public void setPricingLevelId(int pricingLevelId) {
        this.pricingLevelId = pricingLevelId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
