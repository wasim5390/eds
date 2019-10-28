package com.optimus.eds.db.entities.pricing;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = {
        @ForeignKey(entity = PriceConditionDetail.class,
                parentColumns = "priceConditionDetailId",
                childColumns = "priceConditionDetailId",
                onDelete = ForeignKey.CASCADE
        )},indices = @Index(value = {"priceConditionDetailId","priceConditionScaleId"}))
public class PriceConditionScale {

    @PrimaryKey @NonNull
    private int priceConditionScaleId;
    @NonNull
    private Double from;
    @NonNull
    private Double amount;
    @NonNull
    private int priceConditionDetailId;

    public int getPriceConditionScaleId() {
        return priceConditionScaleId;
    }

    public void setPriceConditionScaleId(int priceConditionScaleId) {
        this.priceConditionScaleId = priceConditionScaleId;
    }

    @NonNull
    public Double getFrom() {
        return from;
    }

    public void setFrom(@NonNull Double from) {
        this.from = from;
    }

    @NonNull
    public Double getAmount() {
        return amount;
    }

    public void setAmount(@NonNull Double amount) {
        this.amount = amount;
    }

    public int getPriceConditionDetailId() {
        return priceConditionDetailId;
    }

    public void setPriceConditionDetailId(int priceConditionDetailId) {
        this.priceConditionDetailId = priceConditionDetailId;
    }


}
