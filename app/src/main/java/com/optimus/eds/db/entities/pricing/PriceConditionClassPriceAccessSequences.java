package com.optimus.eds.db.entities.pricing;


import androidx.room.Entity;

@Entity(primaryKeys = {"priceConditionClass_Id","priceAccessSequence_Id"})
public class PriceConditionClassPriceAccessSequences {

    private int priceConditionClass_Id;
    private int priceAccessSequence_Id;

    public int getPriceConditionClass_Id() {
        return priceConditionClass_Id;
    }

    public void setPriceConditionClass_Id(int priceConditionClass_Id) {
        this.priceConditionClass_Id = priceConditionClass_Id;
    }

    public int getPriceAccessSequence_Id() {
        return priceAccessSequence_Id;
    }

    public void setPriceAccessSequence_Id(int priceAccessSequence_Id) {
        this.priceAccessSequence_Id = priceAccessSequence_Id;
    }


}
