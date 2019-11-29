package com.optimus.eds.model;

import com.google.gson.annotations.SerializedName;
import com.optimus.eds.BaseModel;
import com.optimus.eds.db.entities.pricing.PriceAccessSequence;
import com.optimus.eds.db.entities.pricing.PriceBundle;
import com.optimus.eds.db.entities.pricing.PriceCondition;
import com.optimus.eds.db.entities.pricing.PriceConditionClass;
import com.optimus.eds.db.entities.pricing.PriceConditionDetail;
import com.optimus.eds.db.entities.pricing.PriceConditionEntities;
import com.optimus.eds.db.entities.pricing.PriceConditionScale;
import com.optimus.eds.db.entities.pricing.PriceConditionType;

import java.util.ArrayList;
import java.util.List;

public class PricingModel extends BaseModel {

    @SerializedName("priceConditionClass")
    public List<PriceConditionClass> priceConditionClasses;
    @SerializedName("priceConditionType")
    public List<PriceConditionType> priceConditionTypes;
    @SerializedName("priceCondition")
    public List<PriceCondition> priceConditions;


    @SerializedName("priceBundle")
    public List<PriceBundle> priceBundles;
    @SerializedName("priceConditionEntity")
    public List<PriceConditionEntities> priceConditionEntities;
    @SerializedName("priceConditionDetail")
    public List<PriceConditionDetail> priceConditionDetails;

    @SerializedName("priceConditionScale")
    public List<PriceConditionScale> priceConditionScales;

    @SerializedName("priceAccessSequence")
    public List<PriceAccessSequence> priceAccessSequences;

    public List<PriceConditionClass> getPriceConditionClasses() {
        return (priceConditionClasses != null) ? priceConditionClasses : new ArrayList<>();
    }

    public List<PriceConditionType> getPriceConditionTypes() {
        return (priceConditionTypes != null) ? priceConditionTypes : new ArrayList<>();
    }

    public List<PriceCondition> getPriceConditions() {
        return (priceConditions != null) ? priceConditions : new ArrayList<>();
    }

    public List<PriceBundle> getPriceBundles() {
        return  (priceBundles != null) ? priceBundles : new ArrayList<>();
    }


    public List<PriceConditionEntities> getPriceConditionEntities() {
        return   (priceConditionEntities != null) ? priceConditionEntities : new ArrayList<>();
    }


    public List<PriceConditionDetail> getPriceConditionDetails() {
        return   (priceConditionDetails != null) ? priceConditionDetails : new ArrayList<>();
    }


    public List<PriceConditionScale> getPriceConditionScales() {

        return   (priceConditionScales != null) ? priceConditionScales : new ArrayList<>();
    }


    public List<PriceAccessSequence> getPriceAccessSequences() {
        return   (priceAccessSequences != null) ? priceAccessSequences : new ArrayList<>();
    }
}
