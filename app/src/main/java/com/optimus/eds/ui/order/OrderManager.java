package com.optimus.eds.ui.order;

import com.optimus.eds.db.entities.OrderDetail;
import com.optimus.eds.db.entities.Product;
import com.optimus.eds.model.OrderModel;

public class OrderManager {

    private static OrderManager instance;

    public static OrderManager instance(){
        if(instance ==null)
            instance = new OrderManager();
        return instance;
    }

    /**
     * Converts units to cartons and remaining items as units
     * @param productUnitsPerCarton
     * @param orderedUnits
     * @param orderedCartons
     * @return
     */
    public OrderQuantity calculateOrderQty(Long productUnitsPerCarton, Long orderedUnits, Long orderedCartons){

        OrderQuantity orderQuantity = new OrderQuantity(orderedUnits,orderedCartons);
        if(orderedUnits>productUnitsPerCarton && productUnitsPerCarton!=null && productUnitsPerCarton>0) {
            long quotient = orderedUnits / productUnitsPerCarton;
            long remainder = orderedUnits % productUnitsPerCarton;
            orderQuantity.setCarton(quotient+orderedCartons);
            orderQuantity.setUnits(remainder);
        }
        return orderQuantity;
    }




    public class OrderQuantity{

        public OrderQuantity(Long units, Long carton) {
            this.units = units;
            this.carton = carton;
        }

        public Long getUnits() {
            return units;
        }

        public Long getCarton() {
            return carton;
        }

        public void setUnits(Long units) {
            this.units = units;
        }

        public void setCarton(Long carton) {
            this.carton = carton;
        }

       private Long units;
       private Long carton;
    }
}
