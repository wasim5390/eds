package com.optimus.eds.model;

import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Relation;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.optimus.eds.db.entities.Order;
import com.optimus.eds.db.entities.OrderDetail;
import com.optimus.eds.db.entities.Outlet;


import java.util.ArrayList;
import java.util.List;

public class OrderModel {

    @Embedded
    public Order order;

    @Embedded
    public Outlet outlet;

    @Ignore
    List<OrderDetail> orderDetails;

    @Ignore
    List<OrderDetail> freeGoods;

    @Relation(parentColumn = "pk_oid", entityColumn = "fk_oid", entity = OrderDetail.class)
    public List<OrderDetailAndPriceBreakdown> orderDetailAndCPriceBreakdowns;

    public List<OrderDetailAndPriceBreakdown> getOrderDetailAndCPriceBreakdowns() {
        return orderDetailAndCPriceBreakdowns;
    }


    public List<OrderDetail> getOrderDetails() {

        return orderDetails==null?new ArrayList<>():orderDetails;
    }

    public void setOrderDetails(List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }

    public List<OrderDetail> getFreeGoods() {
        return freeGoods;
    }

    public void setFreeGoods(List<OrderDetail> freeGoods) {
        this.freeGoods = freeGoods;
    }


    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }


    public Outlet getOutlet() {
        return outlet;
    }

    public void setOutlet(Outlet outlet){
        this.outlet = outlet;
    }

    public JsonObject toJSON(){
        JsonObject jsonObject=null;
        Order order = getOrder();
        try {
            JsonParser parser = new JsonParser();
            jsonObject = parser.parse(new Gson().toJson(order)).getAsJsonObject();
            JsonArray jsonArray = parser.parse(new Gson().toJson(getOrderDetails())).getAsJsonArray();
            jsonObject.add("orderDetails",jsonArray);

        } catch (JsonParseException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

}
