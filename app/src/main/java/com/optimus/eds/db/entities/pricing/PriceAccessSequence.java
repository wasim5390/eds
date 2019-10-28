package com.optimus.eds.db.entities.pricing;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class PriceAccessSequence {
    @PrimaryKey
    @NonNull
    private int priceAccessSequenceId;
    private String sequenceCode;
    private String sequenceName;
    @NonNull
    private int order;
    @Nullable
    private Integer pricingLevelId,pricingTypeId;

    public int getPriceAccessSequenceId() {
        return priceAccessSequenceId;
    }

    public void setPriceAccessSequenceId(int priceAccessSequenceId) {
        this.priceAccessSequenceId = priceAccessSequenceId;
    }

    public String getSequenceCode() {
        return sequenceCode;
    }

    public void setSequenceCode(String sequenceCode) {
        this.sequenceCode = sequenceCode;
    }

    public String getSequenceName() {
        return sequenceName;
    }

    public void setSequenceName(String sequenceName) {
        this.sequenceName = sequenceName;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Nullable
    public Integer getPricingLevelId() {
        return pricingLevelId;
    }

    public void setPricingLevelId(@Nullable Integer pricingLevelId) {
        this.pricingLevelId = pricingLevelId;
    }

    @Nullable
    public Integer getPricingTypeId() {
        return pricingTypeId;
    }

    public void setPricingTypeId(@Nullable Integer pricingTypeId) {
        this.pricingTypeId = pricingTypeId;
    }




}
