package com.optimus.eds.db.entities.pricing;


import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(primaryKeys = {"priceConditionClass_Id","priceAccessSequence_Id"})
public class PriceConditionClassPriceAccessSequences {

    @NonNull
    private Integer priceConditionClass_Id;
    @NonNull
    private Integer priceAccessSequence_Id;

    public Integer getPriceConditionClass_Id() {
        return priceConditionClass_Id;
    }

    public void setPriceConditionClass_Id(Integer priceConditionClass_Id) {
        this.priceConditionClass_Id = priceConditionClass_Id;
    }

    public Integer getPriceAccessSequence_Id() {
        return priceAccessSequence_Id;
    }

    public void setPriceAccessSequence_Id(Integer priceAccessSequence_Id) {
        this.priceAccessSequence_Id = priceAccessSequence_Id;
    }


}
