package com.optimus.eds.db.entities.pricing;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class PriceScaleBasis {


    @PrimaryKey
    @NonNull
    private int priceScaleBasisId;
    private String value;

    public int getPriceScaleBasisId() {
        return priceScaleBasisId;
    }

    public void setPriceScaleBasisId(int priceScaleBasisId) {
        this.priceScaleBasisId = priceScaleBasisId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
