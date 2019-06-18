package com.optimus.eds.model;

import com.google.gson.annotations.Expose;
import com.optimus.eds.db.entities.CustomerInput;
import com.optimus.eds.db.entities.Merchandise;

public class MasterModel extends BaseResponse {

    @Expose
    private Long outletId;
    @Expose
    private Integer outletStatus;
    @Expose
    private String reason;

    @Expose
    private CustomerInput customerInput;
    @Expose
    private OrderResponseModel order;

    public Long getOutletId() {
        return outletId;
    }

    public void setOutletId(Long outletId) {
        this.outletId = outletId;
    }

    public Integer getOutletStatus() {
        return outletStatus;
    }

    public void setOutletStatus(Integer outletStatus) {
        this.outletStatus = outletStatus;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public OrderResponseModel getOrderModel() {
        return order;
    }

    public void setOrderModel(OrderResponseModel responseModel) {
        this.order = responseModel;
    }

    public void setCustomerInput(CustomerInput customerInput) {
        this.customerInput = customerInput;
    }






}
