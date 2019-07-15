
package com.optimus.eds.model;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.optimus.eds.db.entities.OrderDetail;

public class OrderResponseModel extends BaseResponse{

    @Expose
    private String code;
    @Expose
    private Long deliveryDate;
    @Expose
    private Long distributionId;
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
    private Integer orderStatusId;
    @Expose
    private String orderStatusText;
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
    private Long visitDayId;

    @Expose
    private Integer outletStatus;

    public Integer getOutletStatus() {
        return outletStatus;
    }
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Long deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public Long getDistributionId() {
        return distributionId;
    }

    public void setDistributionId(Long distributionId) {
        this.distributionId = distributionId;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Long getMobileOrderId() {
        return mobileOrderId;
    }

    public void setMobileOrderId(Long mobileOrderId) {
        this.mobileOrderId = mobileOrderId;
    }

    public Long getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Long orderDate) {
        this.orderDate = orderDate;
    }

    public List<OrderDetail> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Integer getOrderStatusId() {
        return orderStatusId;
    }

    public void setOrderStatusId(Integer orderStatusId) {
        this.orderStatusId = orderStatusId;
    }

    public String getOrderStatusText() {
        return orderStatusText;
    }

    public void setOrderStatusText(String orderStatusText) {
        this.orderStatusText = orderStatusText;
    }

    public Long getOutletId() {
        return outletId;
    }

    public void setOutletId(Long outletId) {
        this.outletId = outletId;
    }

    public Long getPayable() {
        return payable;
    }

    public void setPayable(Long payable) {
        this.payable = payable;
    }

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    public Long getSalesmanId() {
        return salesmanId;
    }

    public void setSalesmanId(Long salesmanId) {
        this.salesmanId = salesmanId;
    }

    public Long getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Long subtotal) {
        this.subtotal = subtotal;
    }

    public Long getVisitDayId() {
        return visitDayId;
    }

    public void setVisitDayId(Long visitDayId) {
        this.visitDayId = visitDayId;
    }

}
