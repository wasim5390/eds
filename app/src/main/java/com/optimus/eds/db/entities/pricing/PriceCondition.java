package com.optimus.eds.db.entities.pricing;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = {
        @ForeignKey(entity = PriceAccessSequence.class
                ,parentColumns = "priceAccessSequenceId",
                childColumns = "accessSequenceId",
                onDelete=ForeignKey.CASCADE),

        @ForeignKey(entity = PriceConditionType.class
                ,parentColumns = "priceConditionTypeId",
                childColumns = "conditionTypeId",
                onDelete=ForeignKey.CASCADE)
},indices = @Index({"accessSequenceId","conditionTypeId"}))
public class PriceCondition {

    @PrimaryKey
    @NonNull
    private int priceConditionId;
    @NonNull
    private int conditionTypeId;
    @NonNull
    private int accessSequenceId;
    private String name;
    @NonNull
    private Boolean isActive;
    private Boolean isBundle;
    private Integer entityGroupById;
    private Integer pricingType;
    private String validFrom;
    private String validTo;
    private Integer organizationId;
    private Integer distributionId;

    public int getPriceConditionId() {
        return priceConditionId;
    }

    public void setPriceConditionId(int priceConditionId) {
        this.priceConditionId = priceConditionId;
    }

    public int getConditionTypeId() {
        return conditionTypeId;
    }

    public void setConditionTypeId(int conditionTypeId) {
        this.conditionTypeId = conditionTypeId;
    }

    public int getAccessSequenceId() {
        return accessSequenceId;
    }

    public void setAccessSequenceId(int accessSequenceId) {
        this.accessSequenceId = accessSequenceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NonNull
    public Boolean getActive() {
        return isActive;
    }

    public void setActive(@NonNull Boolean active) {
        isActive = active;
    }

    public Boolean getBundle() {
        return isBundle;
    }

    public void setBundle(Boolean bundle) {
        isBundle = bundle;
    }

    public Integer getEntityGroupById() {
        return entityGroupById;
    }

    public void setEntityGroupById(Integer entityGroupById) {
        this.entityGroupById = entityGroupById;
    }

    public Integer getPricingType() {
        return pricingType;
    }

    public void setPricingType(Integer pricingType) {
        this.pricingType = pricingType;
    }

    public String getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(String validFrom) {
        this.validFrom = validFrom;
    }

    public String getValidTo() {
        return validTo;
    }

    public void setValidTo(String validTo) {
        this.validTo = validTo;
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
