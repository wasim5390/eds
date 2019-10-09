
package com.optimus.eds.model;


public class ReportModel {

    private Long mCarton;
    private Double mGrandTotal;
    private Long mUnit;
    private Integer mSkuSize;
    private Integer totalOrders;


    private Integer pjpCount, completedOutletsCount,productiveOutletCount;

    public Long getCarton() {
        return mCarton;
    }

    public void setCarton(Long carton) {
        mCarton = carton;
    }

    public Double getTotalAmount() {
        return mGrandTotal==null?0:mGrandTotal;
    }

    public void setTotalSale(Double totalAmount) {
        mGrandTotal = totalAmount;
    }

    public Long getUnit() {
        return mUnit;
    }

    public void setUnit(Long unit) {
        mUnit = unit;
    }

    public Integer getTotalOrders() {
        return totalOrders==null?0:totalOrders;
    }

    public Integer getSkuSize() {
         return mSkuSize==null?0:mSkuSize;
    }

    public float getAvgSkuSize(){
        if(getProductiveOutletCount()<1)
            return 0;
        int skuSize = getSkuSize();
        return (float)skuSize/ getProductiveOutletCount();
    }

    public Double getDropSize(){
        if(getProductiveOutletCount()<1)
            return 0.0;
        return getTotalAmount()/ getProductiveOutletCount();
    }

    public void setSkuSize(int mSkuSize) {
        this.mSkuSize = mSkuSize;
    }

    public Integer getPjpCount() {
        return pjpCount==null?0:pjpCount;
    }

    public void setPjpCount(Integer pjpCount) {
        this.pjpCount = pjpCount;
    }

    public void setCounts(Integer pjpCount,Integer completedTaskCount, Integer productiveOutletCount) {
        this.pjpCount = pjpCount;
        this.completedOutletsCount = completedTaskCount;
        this.productiveOutletCount = productiveOutletCount;
    }

    public Integer getCompletedOutletsCount() {
        return completedOutletsCount ==null?0: completedOutletsCount;
    }

    public void setCompletedOutletsCount(Integer completedTaskCount) {
        this.completedOutletsCount = completedTaskCount;
    }

    public Integer getProductiveOutletCount() {
        return productiveOutletCount==null?0:productiveOutletCount;
    }

    public void setProductiveOutletCount(Integer productiveOutletCount) {
        this.productiveOutletCount = productiveOutletCount;
    }



    public void setTotalOrders(int totalOrders) {
        this.totalOrders = totalOrders;
    }

}
