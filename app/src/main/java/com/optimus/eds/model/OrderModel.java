package com.optimus.eds.model;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.optimus.eds.db.entities.CartonPriceBreakDown;
import com.optimus.eds.db.entities.Order;
import com.optimus.eds.db.entities.OrderDetail;
import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.db.entities.UnitPriceBreakDown;


import java.util.ArrayList;
import java.util.List;

public class OrderModel {

    @Embedded
    public Order order;

    @Embedded
    public Outlet outlet;

    @Relation(parentColumn = "pk_oid", entityColumn = "fk_oid")
    List<OrderDetail> orderDetails;


/*    @Relation(parentColumn = "cartonOrderDetailId", entityColumn = "fk_odid")
    List<CartonPriceBreakDown> cartonPriceBreakDowns;

    @Relation(parentColumn = "unitOrderDetailId", entityColumn = "fk_odid")
    List<UnitPriceBreakDown> unitPriceBreakDowns;


    public void setCartonPriceBreakDowns(List<CartonPriceBreakDown> cartonPriceBreakDowns) {
        this.cartonPriceBreakDowns = cartonPriceBreakDowns;
    }

    public void setUnitPriceBreakDowns(List<UnitPriceBreakDown> unitPriceBreakDowns) {
        this.unitPriceBreakDowns = unitPriceBreakDowns;
    }*/

    public List<OrderDetail> getOrderDetails() {
        return orderDetails==null?new ArrayList<>():orderDetails;
    }

    public void setOrderDetails(List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }


 /*   public List<CartonPriceBreakDown> getCartonPriceBreakDowns() {
        return cartonPriceBreakDowns==null?new ArrayList<>():cartonPriceBreakDowns;
    }

    public List<UnitPriceBreakDown> getUnitPriceBreakDowns() {
        return unitPriceBreakDowns==null?new ArrayList<>():unitPriceBreakDowns;
    }*/
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
