package com.optimus.eds.db.entities.pricing;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity
public class PriceConditionDetail {

    @PrimaryKey
    @NonNull
    private int priceConditionDetailId;
    @NonNull
    private Double amount;
    @NonNull
    private Boolean isScale;

    private String validFrom;
    private String validTo;

    @NonNull
    private int type;
    @NonNull
    private int priceConditionId;

    private Boolean isDeleted;
    private Integer productId;
    private Integer productDefinitionId;
    private Integer outletId;
    private Integer routeId;
    private Integer distributionId;
    private Integer minimumQuantity;
    private Integer bundleId;
    private Double maximumLimit;
    private Integer limitBy;

    public int getPriceConditionDetailId() {
        return priceConditionDetailId;
    }

    public void setPriceConditionDetailId(int priceConditionDetailId) {
        this.priceConditionDetailId = priceConditionDetailId;
    }

    @NonNull
    public Double getAmount() {
        return amount;
    }

    public void setAmount(@NonNull Double amount) {
        this.amount = amount;
    }

    @NonNull
    public Boolean getScale() {
        return isScale;
    }

    public void setScale(@NonNull Boolean scale) {
        isScale = scale;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPriceConditionId() {
        return priceConditionId;
    }

    public void setPriceConditionId(int priceConditionId) {
        this.priceConditionId = priceConditionId;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getProductDefinitionId() {
        return productDefinitionId;
    }

    public void setProductDefinitionId(Integer productDefinitionId) {
        this.productDefinitionId = productDefinitionId;
    }

    public Integer getOutletId() {
        return outletId;
    }

    public void setOutletId(Integer outletId) {
        this.outletId = outletId;
    }

    public Integer getRouteId() {
        return routeId;
    }

    public void setRouteId(Integer routeId) {
        this.routeId = routeId;
    }

    public Integer getDistributionId() {
        return distributionId;
    }

    public void setDistributionId(Integer distributionId) {
        this.distributionId = distributionId;
    }

    public Integer getMinimumQuantity() {
        return minimumQuantity;
    }

    public void setMinimumQuantity(Integer minimumQuantity) {
        this.minimumQuantity = minimumQuantity;
    }

    public Integer getBundleId() {
        return bundleId;
    }

    public void setBundleId(Integer bundleId) {
        this.bundleId = bundleId;
    }

    public Double getMaximumLimit() {
        return maximumLimit;
    }

    public void setMaximumLimit(Double maximumLimit) {
        this.maximumLimit = maximumLimit;
    }

    public Integer getLimitBy() {
        return limitBy;
    }

    public void setLimitBy(Integer limitBy) {
        this.limitBy = limitBy;
    }




}
