
package com.optimus.eds.model;


import com.google.gson.annotations.Expose;

public class PriceBreakDownModel {

    @Expose
    private Long blockPrice;
    @Expose
    private Long calculationType;
    @Expose
    private Long id;
    @Expose
    private Boolean isMaxLimitReached;
    @Expose
    private Double maximumLimit;
    @Expose
    private Long orderDetailId;
    @Expose
    private Long orderId;
    @Expose
    private Long outletId;
    @Expose
    private String priceCondition;
    @Expose
    private String priceConditionClass;
    @Expose
    private Long priceConditionDetailId;
    @Expose
    private Long priceConditionId;
    @Expose
    private String priceConditionType;
    @Expose
    private Long productDefinitionId;
    @Expose
    private Long productId;
    @Expose
    private Long totalPrice;
    @Expose
    private Long unitPrice;

    public Long getBlockPrice() {
        return blockPrice;
    }

    public Long getCalculationType() {
        return calculationType;
    }

    public Long getId() {
        return id;
    }

    public Boolean getIsMaxLimitReached() {
        return isMaxLimitReached;
    }

    public Object getMaximumLimit() {
        return maximumLimit;
    }

    public Long getOrderDetailId() {
        return orderDetailId;
    }

    public Object getOrderId() {
        return orderId;
    }

    public Long getOutletId() {
        return outletId;
    }

    public String getPriceCondition() {
        return priceCondition;
    }

    public String getPriceConditionClass() {
        return priceConditionClass;
    }

    public Long getPriceConditionDetailId() {
        return priceConditionDetailId;
    }

    public Long getPriceConditionId() {
        return priceConditionId;
    }

    public String getPriceConditionType() {
        return priceConditionType;
    }

    public Long getProductDefinitionId() {
        return productDefinitionId;
    }

    public Long getProductId() {
        return productId;
    }

    public Long getTotalPrice() {
        return totalPrice;
    }

    public Long getUnitPrice() {
        return unitPrice;
    }

    public static class Builder {

        private Long blockPrice;
        private Long calculationType;
        private Long id;
        private Boolean isMaxLimitReached;
        private Double maximumLimit;
        private Long orderDetailId;
        private Long orderId;
        private Long outletId;
        private String priceCondition;
        private String priceConditionClass;
        private Long priceConditionDetailId;
        private Long priceConditionId;
        private String priceConditionType;
        private Long productDefinitionId;
        private Long productId;
        private Long totalPrice;
        private Long unitPrice;

        public PriceBreakDownModel.Builder withBlockPrice(Long blockPrice) {
            this.blockPrice = blockPrice;
            return this;
        }

        public PriceBreakDownModel.Builder withCalculationType(Long calculationType) {
            this.calculationType = calculationType;
            return this;
        }

        public PriceBreakDownModel.Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public PriceBreakDownModel.Builder withIsMaxLimitReached(Boolean isMaxLimitReached) {
            this.isMaxLimitReached = isMaxLimitReached;
            return this;
        }


        public PriceBreakDownModel.Builder withMaximumLimit(Double maximumLimit) {
            this.maximumLimit = maximumLimit;
            return this;
        }

        public PriceBreakDownModel.Builder withOrderDetailId(Long orderDetailId) {
            this.orderDetailId = orderDetailId;
            return this;
        }

        public PriceBreakDownModel.Builder withOrderId(Long orderId) {
            this.orderId = orderId;
            return this;
        }

        public PriceBreakDownModel.Builder withOutletId(Long outletId) {
            this.outletId = outletId;
            return this;
        }

        public PriceBreakDownModel.Builder withPriceCondition(String priceCondition) {
            this.priceCondition = priceCondition;
            return this;
        }

        public PriceBreakDownModel.Builder withPriceConditionClass(String priceConditionClass) {
            this.priceConditionClass = priceConditionClass;
            return this;
        }

        public PriceBreakDownModel.Builder withPriceConditionDetailId(Long priceConditionDetailId) {
            this.priceConditionDetailId = priceConditionDetailId;
            return this;
        }

        public PriceBreakDownModel.Builder withPriceConditionId(Long priceConditionId) {
            this.priceConditionId = priceConditionId;
            return this;
        }

        public PriceBreakDownModel.Builder withPriceConditionType(String priceConditionType) {
            this.priceConditionType = priceConditionType;
            return this;
        }

        public PriceBreakDownModel.Builder withProductDefinitionId(Long productDefinitionId) {
            this.productDefinitionId = productDefinitionId;
            return this;
        }

        public PriceBreakDownModel.Builder withProductId(Long productId) {
            this.productId = productId;
            return this;
        }

        public PriceBreakDownModel.Builder withTotalPrice(Long totalPrice) {
            this.totalPrice = totalPrice;
            return this;
        }

        public PriceBreakDownModel.Builder withUnitPrice(Long unitPrice) {
            this.unitPrice = unitPrice;
            return this;
        }

        public PriceBreakDownModel build() {
            PriceBreakDownModel cartonPriceBreakDown = new PriceBreakDownModel();

            cartonPriceBreakDown.blockPrice = blockPrice;
            cartonPriceBreakDown.calculationType = calculationType;
            cartonPriceBreakDown.id = id;
            cartonPriceBreakDown.isMaxLimitReached = isMaxLimitReached;
            cartonPriceBreakDown.maximumLimit = maximumLimit;
            cartonPriceBreakDown.orderDetailId = orderDetailId;
            cartonPriceBreakDown.orderId = orderId;
            cartonPriceBreakDown.outletId = outletId;
            cartonPriceBreakDown.priceCondition = priceCondition;
            cartonPriceBreakDown.priceConditionClass = priceConditionClass;
            cartonPriceBreakDown.priceConditionDetailId = priceConditionDetailId;
            cartonPriceBreakDown.priceConditionId = priceConditionId;
            cartonPriceBreakDown.priceConditionType = priceConditionType;
            cartonPriceBreakDown.productDefinitionId = productDefinitionId;
            cartonPriceBreakDown.productId = productId;
            cartonPriceBreakDown.totalPrice = totalPrice;
            cartonPriceBreakDown.unitPrice = unitPrice;
            return cartonPriceBreakDown;
        }

    }

}
