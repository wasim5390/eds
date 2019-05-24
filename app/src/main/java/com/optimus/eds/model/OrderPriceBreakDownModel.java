
package com.optimus.eds.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.optimus.eds.db.entities.OrderDetail;

public class OrderPriceBreakDownModel {

    @Expose
    private Object code;
    @Expose
    private Long deliveryDate;
    @Expose
    private Long distributionId;
    @Expose
    private Object errorMessage;
    @Expose
    private Double latitude;
    @Expose
    private Double longitude;
    @Expose
    private Long mobileOrderId;
    @Expose
    private Long orderDate;
    @Expose
    private List<OrderDetail> orderDetails;
    @Expose
    private Long orderId;
    @Expose
    private Long orderStatusId;
    @Expose
    private Object orderStatusText;
    @Expose
    private Long outletId;
    @Expose
    private Long payable;
    @Expose
    private Long routeId;
    @Expose
    private Long salesmanId;
    @Expose
    private Long subtotal;
    @Expose
    private String success;
    @Expose
    private Long visitDayId;

    public Object getCode() {
        return code;
    }

    public Long getDeliveryDate() {
        return deliveryDate;
    }

    public Long getDistributionId() {
        return distributionId;
    }

    public Object getErrorMessage() {
        return errorMessage;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Long getMobileOrderId() {
        return mobileOrderId;
    }

    public Long getOrderDate() {
        return orderDate;
    }

    public List<OrderDetail> getOrderDetails() {
        return orderDetails;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getOrderStatusId() {
        return orderStatusId;
    }

    public Object getOrderStatusText() {
        return orderStatusText;
    }

    public Long getOutletId() {
        return outletId;
    }

    public Long getPayable() {
        return payable;
    }

    public Long getRouteId() {
        return routeId;
    }

    public Long getSalesmanId() {
        return salesmanId;
    }

    public Long getSubtotal() {
        return subtotal;
    }

    public String getSuccess() {
        return success;
    }

    public Long getVisitDayId() {
        return visitDayId;
    }

    public static class Builder {

        private Object code;
        private Long deliveryDate;
        private Long distributionId;
        private Object errorMessage;
        private Double latitude;
        private Double longitude;
        private Long mobileOrderId;
        private Long orderDate;
        private List<OrderDetail> orderDetails;
        private Long orderId;
        private Long orderStatusId;
        private Object orderStatusText;
        private Long outletId;
        private Long payable;
        private Long routeId;
        private Long salesmanId;
        private Long subtotal;
        private String success;
        private Long visitDayId;

        public OrderPriceBreakDownModel.Builder withCode(Object code) {
            this.code = code;
            return this;
        }

        public OrderPriceBreakDownModel.Builder withDeliveryDate(Long deliveryDate) {
            this.deliveryDate = deliveryDate;
            return this;
        }

        public OrderPriceBreakDownModel.Builder withDistributionId(Long distributionId) {
            this.distributionId = distributionId;
            return this;
        }

        public OrderPriceBreakDownModel.Builder withErrorMessage(Object errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public OrderPriceBreakDownModel.Builder withLatitude(Double latitude) {
            this.latitude = latitude;
            return this;
        }

        public OrderPriceBreakDownModel.Builder withLongitude(Double longitude) {
            this.longitude = longitude;
            return this;
        }

        public OrderPriceBreakDownModel.Builder withMobileOrderId(Long mobileOrderId) {
            this.mobileOrderId = mobileOrderId;
            return this;
        }

        public OrderPriceBreakDownModel.Builder withOrderDate(Long orderDate) {
            this.orderDate = orderDate;
            return this;
        }

        public OrderPriceBreakDownModel.Builder withOrderDetails(List<OrderDetail> orderDetails) {
            this.orderDetails = orderDetails;
            return this;
        }

        public OrderPriceBreakDownModel.Builder withOrderId(Long orderId) {
            this.orderId = orderId;
            return this;
        }

        public OrderPriceBreakDownModel.Builder withOrderStatusId(Long orderStatusId) {
            this.orderStatusId = orderStatusId;
            return this;
        }

        public OrderPriceBreakDownModel.Builder withOrderStatusText(Object orderStatusText) {
            this.orderStatusText = orderStatusText;
            return this;
        }

        public OrderPriceBreakDownModel.Builder withOutletId(Long outletId) {
            this.outletId = outletId;
            return this;
        }

        public OrderPriceBreakDownModel.Builder withPayable(Long payable) {
            this.payable = payable;
            return this;
        }

        public OrderPriceBreakDownModel.Builder withRouteId(Long routeId) {
            this.routeId = routeId;
            return this;
        }

        public OrderPriceBreakDownModel.Builder withSalesmanId(Long salesmanId) {
            this.salesmanId = salesmanId;
            return this;
        }

        public OrderPriceBreakDownModel.Builder withSubtotal(Long subtotal) {
            this.subtotal = subtotal;
            return this;
        }

        public OrderPriceBreakDownModel.Builder withSuccess(String success) {
            this.success = success;
            return this;
        }

        public OrderPriceBreakDownModel.Builder withVisitDayId(Long visitDayId) {
            this.visitDayId = visitDayId;
            return this;
        }

        public OrderPriceBreakDownModel build() {
            OrderPriceBreakDownModel orderPriceBreakDownModel = new OrderPriceBreakDownModel();
            orderPriceBreakDownModel.code = code;
            orderPriceBreakDownModel.deliveryDate = deliveryDate;
            orderPriceBreakDownModel.distributionId = distributionId;
            orderPriceBreakDownModel.errorMessage = errorMessage;
            orderPriceBreakDownModel.latitude = latitude;
            orderPriceBreakDownModel.longitude = longitude;
            orderPriceBreakDownModel.mobileOrderId = mobileOrderId;
            orderPriceBreakDownModel.orderDate = orderDate;
            orderPriceBreakDownModel.orderDetails = orderDetails;
            orderPriceBreakDownModel.orderId = orderId;
            orderPriceBreakDownModel.orderStatusId = orderStatusId;
            orderPriceBreakDownModel.orderStatusText = orderStatusText;
            orderPriceBreakDownModel.outletId = outletId;
            orderPriceBreakDownModel.payable = payable;
            orderPriceBreakDownModel.routeId = routeId;
            orderPriceBreakDownModel.salesmanId = salesmanId;
            orderPriceBreakDownModel.subtotal = subtotal;
            orderPriceBreakDownModel.success = success;
            orderPriceBreakDownModel.visitDayId = visitDayId;
            return orderPriceBreakDownModel;
        }

    }

}
