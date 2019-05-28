package com.optimus.eds.model;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import com.optimus.eds.db.entities.CartonPriceBreakDown;
import com.optimus.eds.db.entities.OrderDetail;
import com.optimus.eds.db.entities.UnitPriceBreakDown;

import java.util.List;

public class OrderDetailAndCpriceBreakdown {


    @Embedded
    OrderDetail orderDetail;

    @Relation(parentColumn = "pk_modid", entityColumn = "fk_modid")
    List<CartonPriceBreakDown> cartonPriceBreakDownList;


    @Relation(parentColumn = "pk_modid", entityColumn = "fk_modid")
    List<UnitPriceBreakDown> unitPriceBreakDownList;

    public OrderDetail getOrderDetail() {
        return orderDetail;
    }

    public List<CartonPriceBreakDown> getCartonPriceBreakDownList() {
        return cartonPriceBreakDownList;
    }

    public List<UnitPriceBreakDown> getUnitPriceBreakDownList() {
        return unitPriceBreakDownList;
    }
}
