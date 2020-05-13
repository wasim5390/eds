package com.optimus.eds.db.entities.pricing;


import java.util.List;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity( indices = { @Index(value = "pricingLevelId")})

public class PriceConditionClass {

    @PrimaryKey
    private int priceConditionClassId;
    private String name;
    @NonNull
    private int order;
    @NonNull
    private int severityLevel;
    private String severityLevelMessage;

    private int pricingAreaId;

    @Ignore
    private List<PriceConditionType> priceConditionTypes = null;

    @NonNull
    public int getPriceConditionClassId() {
        return priceConditionClassId;
    }

    public void setPriceConditionClassId(int priceConditionClassId) {
        this.priceConditionClassId = priceConditionClassId;
    }

    public int getPricingAreaId() {
        return pricingAreaId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getSeverityLevel() {
        return severityLevel;
    }

    public void setSeverityLevel(int severityLevel) {
        this.severityLevel = severityLevel;
    }

    public String getSeverityLevelMessage() {
        return severityLevelMessage;
    }

    public void setSeverityLevelMessage(String severityLevelMessage) {
        this.severityLevelMessage = severityLevelMessage;
    }

    public Integer getPricingLevelId() {
        return pricingLevelId;
    }

    public void setPricingLevelId(Integer pricingLevelId) {
        this.pricingLevelId = pricingLevelId;
    }

    public List<PriceConditionType> getPriceConditionTypes() {
        return priceConditionTypes;
    }

    public void setPriceConditionTypes(List<PriceConditionType> priceConditionTypes) {
        this.priceConditionTypes = priceConditionTypes;
    }
    public void setPricingAreaId(int pricingAreaId) {
        this.pricingAreaId = pricingAreaId;
    }

   /* @ForeignKey(
            entity = PricingLevels.class,
            parentColumns = "pricingLevelId",
            childColumns = "pricingLevelId",
            onDelete = ForeignKey.CASCADE)*/
    private Integer pricingLevelId;

}
