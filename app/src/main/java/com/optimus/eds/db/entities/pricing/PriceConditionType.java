package com.optimus.eds.db.entities.pricing;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = {@ForeignKey(
        entity = PriceScaleBasis.class,
        parentColumns = "priceScaleBasisId",
        childColumns = "priceScaleBasisId",
        onDelete = ForeignKey.CASCADE),

},indices = { @Index(value = {"priceScaleBasisId","priceConditionTypeId"})})
public class PriceConditionType {

    @PrimaryKey
    @NonNull
    private int priceConditionTypeId;
    private String code;
    private String name;
    @NonNull
    private int conditionClassId;
    @NonNull
    private int operationType;
    @NonNull
    private int calculationType;
    @NonNull
    private int roundingRule;
    private Integer processingOrder;

    @NonNull
    private int priceScaleBasisId;

    private Integer pricingType;

    private Integer organizationId;
    private Integer distributionId;

    public int getPriceConditionTypeId() {
        return priceConditionTypeId;
    }

    public void setPriceConditionTypeId(int priceConditionTypeId) {
        this.priceConditionTypeId = priceConditionTypeId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getConditionClassId() {
        return conditionClassId;
    }

    public void setConditionClassId(int conditionClassId) {
        this.conditionClassId = conditionClassId;
    }

    public int getOperationType() {
        return operationType;
    }

    public void setOperationType(int operationType) {
        this.operationType = operationType;
    }

    public int getCalculationType() {
        return calculationType;
    }

    public void setCalculationType(int calculationType) {
        this.calculationType = calculationType;
    }

    public int getRoundingRule() {
        return roundingRule;
    }

    public void setRoundingRule(int roundingRule) {
        this.roundingRule = roundingRule;
    }

    public Integer getProcessingOrder() {
        return processingOrder;
    }

    public void setProcessingOrder(Integer processingOrder) {
        this.processingOrder = processingOrder;
    }
    public int getPriceScaleBasisId() {
        return priceScaleBasisId;
    }

    public void setPriceScaleBasisId(int priceScaleBasisId) {
        this.priceScaleBasisId = priceScaleBasisId;
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
