
package com.optimus.eds.model;

import java.util.List;
import com.google.gson.annotations.Expose;


public class OrderDetailModel {

    @Expose
    private Long cartonCode;
    @Expose
    private Long cartonOrderDetailId;
    @Expose
    private Long cartonPrice;
    @Expose
    private List<PriceBreakDownModel> cartonPriceBreakDown;
    @Expose
    private List<PriceBreakDownModel> unitPriceBreakDown;
    @Expose
    private Long cartonQuantity;
    @Expose
    private Long cartonTotalPrice;
    @Expose
    private OrderDetailModel childOrderDetail;
    @Expose
    private Integer freeGoodQuantity;
    @Expose
    private Long freeQuantityTypeId;
    @Expose
    private Long mobileOrderId;
    @Expose
    private Long orderId;
    @Expose
    private Long parentId;
    @Expose
    private Long productId;
    @Expose
    private String productName;
    @Expose
    private String type;
    @Expose
    private Long unitCode;
    @Expose
    private Long unitOrderDetailId;
    @Expose
    private Long unitPrice;

    @Expose
    private Long unitQuantity;
    @Expose
    private Long unitTotalPrice;

    public Long getCartonCode() {
        return cartonCode;
    }

    public Long getCartonOrderDetailId() {
        return cartonOrderDetailId;
    }

    public Long getCartonPrice() {
        return cartonPrice;
    }

    public List<PriceBreakDownModel> getCartonPriceBreakDown() {
        return cartonPriceBreakDown;
    }

    public Long getCartonQuantity() {
        return cartonQuantity;
    }

    public Long getCartonTotalPrice() {
        return cartonTotalPrice;
    }

    public Object getChildOrderDetail() {
        return childOrderDetail;
    }

    public Object getFreeGoodQuantity() {
        return freeGoodQuantity;
    }

    public Long getFreeQuantityTypeId() {
        return freeQuantityTypeId;
    }

    public Long getMobileOrderId() {
        return mobileOrderId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getParentId() {
        return parentId;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getType() {
        return type;
    }

    public Long getUnitCode() {
        return unitCode;
    }

    public Long getUnitOrderDetailId() {
        return unitOrderDetailId;
    }

    public Long getUnitPrice() {
        return unitPrice;
    }

    public  List<PriceBreakDownModel>  getUnitPriceBreakDown() {
        return unitPriceBreakDown;
    }

    public Long getUnitQuantity() {
        return unitQuantity;
    }

    public Long getUnitTotalPrice() {
        return unitTotalPrice;
    }

    public static class Builder {

        private Long cartonCode;
        private Long cartonOrderDetailId;
        private Long cartonPrice;
        private List<PriceBreakDownModel> cartonPriceBreakDown;
        private List<PriceBreakDownModel> unitPriceBreakDown;
        private Long cartonQuantity;
        private Long cartonTotalPrice;
        private OrderDetailModel childOrderDetail;
        private Integer freeGoodQuantity;
        private Long freeQuantityTypeId;
        private Long mobileOrderId;
        private Long orderId;
        private Long parentId;
        private Long productId;
        private String productName;
        private String type;
        private Long unitCode;
        private Long unitOrderDetailId;
        private Long unitPrice;
        private Long unitQuantity;
        private Long unitTotalPrice;

        public OrderDetailModel.Builder withCartonCode(Long cartonCode) {
            this.cartonCode = cartonCode;
            return this;
        }

        public OrderDetailModel.Builder withCartonOrderDetailId(Long cartonOrderDetailId) {
            this.cartonOrderDetailId = cartonOrderDetailId;
            return this;
        }

        public OrderDetailModel.Builder withCartonPrice(Long cartonPrice) {
            this.cartonPrice = cartonPrice;
            return this;
        }

        public OrderDetailModel.Builder withCartonPriceBreakDown(List<PriceBreakDownModel> cartonPriceBreakDown) {
            this.cartonPriceBreakDown = cartonPriceBreakDown;
            return this;
        }

        public OrderDetailModel.Builder withCartonQuantity(Long cartonQuantity) {
            this.cartonQuantity = cartonQuantity;
            return this;
        }

        public OrderDetailModel.Builder withCartonTotalPrice(Long cartonTotalPrice) {
            this.cartonTotalPrice = cartonTotalPrice;
            return this;
        }

        public OrderDetailModel.Builder withChildOrderDetail(OrderDetailModel childOrderDetail) {
            this.childOrderDetail = childOrderDetail;
            return this;
        }

        public OrderDetailModel.Builder withFreeGoodQuantity(Integer freeGoodQuantity) {
            this.freeGoodQuantity = freeGoodQuantity;
            return this;
        }

        public OrderDetailModel.Builder withFreeQuantityTypeId(Long freeQuantityTypeId) {
            this.freeQuantityTypeId = freeQuantityTypeId;
            return this;
        }

        public OrderDetailModel.Builder withMobileOrderId(Long mobileOrderId) {
            this.mobileOrderId = mobileOrderId;
            return this;
        }

        public OrderDetailModel.Builder withOrderId(Long orderId) {
            this.orderId = orderId;
            return this;
        }

        public OrderDetailModel.Builder withParentId(Long parentId) {
            this.parentId = parentId;
            return this;
        }

        public OrderDetailModel.Builder withProductId(Long productId) {
            this.productId = productId;
            return this;
        }

        public OrderDetailModel.Builder withProductName(String productName) {
            this.productName = productName;
            return this;
        }

        public OrderDetailModel.Builder withType(String type) {
            this.type = type;
            return this;
        }

        public OrderDetailModel.Builder withUnitCode(Long unitCode) {
            this.unitCode = unitCode;
            return this;
        }

        public OrderDetailModel.Builder withUnitOrderDetailId(Long unitOrderDetailId) {
            this.unitOrderDetailId = unitOrderDetailId;
            return this;
        }

        public OrderDetailModel.Builder withUnitPrice(Long unitPrice) {
            this.unitPrice = unitPrice;
            return this;
        }

        public OrderDetailModel.Builder withUnitPriceBreakDown(List<PriceBreakDownModel> unitPriceBreakDown) {
            this.unitPriceBreakDown = unitPriceBreakDown;
            return this;
        }

        public OrderDetailModel.Builder withUnitQuantity(Long unitQuantity) {
            this.unitQuantity = unitQuantity;
            return this;
        }

        public OrderDetailModel.Builder withUnitTotalPrice(Long unitTotalPrice) {
            this.unitTotalPrice = unitTotalPrice;
            return this;
        }

        public OrderDetailModel build() {
            OrderDetailModel orderDetail = new OrderDetailModel();
            orderDetail.cartonCode = cartonCode;
            orderDetail.cartonOrderDetailId = cartonOrderDetailId;
            orderDetail.cartonPrice = cartonPrice;
            orderDetail.cartonPriceBreakDown = cartonPriceBreakDown;
            orderDetail.cartonQuantity = cartonQuantity;
            orderDetail.cartonTotalPrice = cartonTotalPrice;
            orderDetail.childOrderDetail = childOrderDetail;
            orderDetail.freeGoodQuantity = freeGoodQuantity;
            orderDetail.freeQuantityTypeId = freeQuantityTypeId;
            orderDetail.mobileOrderId = mobileOrderId;
            orderDetail.orderId = orderId;
            orderDetail.parentId = parentId;
            orderDetail.productId = productId;
            orderDetail.productName = productName;
            orderDetail.type = type;
            orderDetail.unitCode = unitCode;
            orderDetail.unitOrderDetailId = unitOrderDetailId;
            orderDetail.unitPrice = unitPrice;
            orderDetail.unitPriceBreakDown = unitPriceBreakDown;
            orderDetail.unitQuantity = unitQuantity;
            orderDetail.unitTotalPrice = unitTotalPrice;
            return orderDetail;
        }

    }

}
