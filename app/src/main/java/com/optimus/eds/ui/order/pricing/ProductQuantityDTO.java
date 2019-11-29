package com.optimus.eds.ui.order.pricing;

public class ProductQuantityDTO {

    public int ProductDefinitionId;
    public int Quantity;

    public int getProductDefinitionId() {
        return ProductDefinitionId;
    }

    public void setProductDefinitionId(int productDefinitionId) {
        ProductDefinitionId = productDefinitionId;
    }

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int quantity) {
        Quantity = quantity;
    }


}
