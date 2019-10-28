package com.optimus.eds.db.entities.pricing;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity( foreignKeys = {
        @ForeignKey(
                entity = PricingLevels.class,
                parentColumns = "pricingLevelId",
                childColumns = "pricingLevelId",
                onDelete = ForeignKey.CASCADE),
}, indices = { @Index(value = "pricingLevelId")})

public class PriceConditionClass {

    @PrimaryKey
    @NonNull
    private int priceConditionClassId;
    private String name;
    @NonNull
    private int order;
    @NonNull
    private int severityLevel;
    private String severityLevelMessage;

    private int pricingLevelId;
    @NonNull
    private boolean isActive;
    private Boolean canLimit;
    private Boolean isPeriodic;
    private Integer pricingType;
    private Integer organizationId;
    private Integer distributionId;

    public int getPriceConditionClassId() {
        return priceConditionClassId;
    }

    public void setPriceConditionClassId(int priceConditionClassId) {
        this.priceConditionClassId = priceConditionClassId;
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

    public int getPricingLevelId() {
        return pricingLevelId;
    }

    public void setPricingLevelId(int pricingLevelId) {
        this.pricingLevelId = pricingLevelId;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Boolean getCanLimit() {
        return canLimit;
    }

    public void setCanLimit(Boolean canLimit) {
        this.canLimit = canLimit;
    }

    public Boolean getPeriodic() {
        return isPeriodic;
    }

    public void setPeriodic(Boolean periodic) {
        isPeriodic = periodic;
    }

    public Integer getPricingType() {
        return pricingType;
    }

    public void setPricingType(Integer pricingType) {
        this.pricingType = pricingType;
    }

    public Integer getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
    }

    public Integer getDistributionId() {
        return distributionId;
    }

    public void setDistributionId(Integer distributionId) {
        this.distributionId = distributionId;
    }



}
