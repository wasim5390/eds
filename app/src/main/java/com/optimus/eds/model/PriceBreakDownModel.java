
package com.optimus.eds.model;


import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import com.optimus.eds.db.entities.CartonPriceBreakDown;
import com.optimus.eds.db.entities.OrderDetail;
import com.optimus.eds.db.entities.UnitPriceBreakDown;
import com.optimus.eds.utils.Util;

import java.util.List;

public class PriceBreakDownModel {


    private List<UnitPriceBreakDown> unitPriceBreakDowns;

    private List<CartonPriceBreakDown> cartonPriceBreakDownList;

    public List<UnitPriceBreakDown> getUnitPriceBreakDowns() {
        return unitPriceBreakDowns;
    }

    public void setUnitPriceBreakDowns(List<UnitPriceBreakDown> unitPriceBreakDowns) {
        this.unitPriceBreakDowns = unitPriceBreakDowns;
    }

    public List<CartonPriceBreakDown> getCartonPriceBreakDownList() {
        return cartonPriceBreakDownList;
    }

    public void setCartonPriceBreakDownList(List<CartonPriceBreakDown> cartonPriceBreakDownList) {
        this.cartonPriceBreakDownList = cartonPriceBreakDownList;
    }

    public boolean isPriceEmpty(){
        return (Util.isListEmpty(getCartonPriceBreakDownList()) && Util.isListEmpty(getUnitPriceBreakDowns()));
    }


}
