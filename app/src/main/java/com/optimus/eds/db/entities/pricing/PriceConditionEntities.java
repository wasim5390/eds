package com.optimus.eds.db.entities.pricing;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        foreignKeys = {
                @ForeignKey(entity = PriceCondition.class,
                        parentColumns = "priceConditionId",
                        childColumns = "priceConditionId",
                        onDelete = ForeignKey.CASCADE)
        }
)
public class PriceConditionEntities {

    @PrimaryKey
    @NonNull
    private int priceConditionEntityId;
    @NonNull
    private int priceConditionId;

    private Integer outletId;
    private Integer routeId;
    private Integer distributionId;
    private Integer bundleId;
    private Boolean isDeleted;

    public int getPriceConditionEntityId() {
        return priceConditionEntityId;
    }

    public void setPriceConditionEntityId(int priceConditionEntityId) {
        this.priceConditionEntityId = priceConditionEntityId;
    }

    public int getPriceConditionId() {
        return priceConditionId;
    }

    public void setPriceConditionId(int priceConditionId) {
        this.priceConditionId = priceConditionId;
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

    public Integer getBundleId() {
        return bundleId;
    }

    public void setBundleId(Integer bundleId) {
        this.bundleId = bundleId;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }




}
