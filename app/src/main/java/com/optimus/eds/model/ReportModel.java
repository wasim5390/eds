
package com.optimus.eds.model;


public class ReportModel {

    private Long mCarton;
    private Double mTotalSale;
    private Long mUnit;
    private Integer mSkuSize;
    private Integer totalOrders;


    private Integer pjpCount,completedTaskCount,productiveOutletCount;

    public Long getCarton() {
        return mCarton;
    }

    public void setCarton(Long carton) {
        mCarton = carton;
    }

    public Double getTotalSale() {
        return mTotalSale;
    }

    public void setTotalSale(Double totalSale) {
        mTotalSale = totalSale;
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

    public void setSkuSize(int mSkuSize) {
        this.mSkuSize = mSkuSize;
    }

    public Integer getPjpCount() {
        return pjpCount;
    }

    public void setPjpCount(Integer pjpCount) {
        this.pjpCount = pjpCount;
    }

    public void setCounts(Integer pjpCount,Integer completedTaskCount, Integer productiveOutletCount) {
        this.pjpCount = pjpCount;
        this.completedTaskCount = completedTaskCount;
        this.productiveOutletCount = productiveOutletCount;
    }

    public Integer getCompletedTaskCount() {
        return completedTaskCount;
    }

    public void setCompletedTaskCount(Integer completedTaskCount) {
        this.completedTaskCount = completedTaskCount;
    }

    public Integer getProductiveOutletCount() {
        return productiveOutletCount;
    }

    public void setProductiveOutletCount(Integer productiveOutletCount) {
        this.productiveOutletCount = productiveOutletCount;
    }



    public void setTotalOrders(int totalOrders) {
        this.totalOrders = totalOrders;
    }

}
