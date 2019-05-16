package com.optimus.eds.model;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import com.optimus.eds.db.entities.Order;
import com.optimus.eds.db.entities.OrderDetail;
import com.optimus.eds.db.entities.Outlet;



import java.util.List;

public class OrderModel {

    @Embedded
    public Order order;

    @Embedded
    public Outlet outlet;

    @Relation(parentColumn = "oid", entityColumn = "c_oid")
    List<OrderDetail> orderDetails;


    public List<OrderDetail> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
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
