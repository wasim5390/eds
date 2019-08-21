package com.optimus.eds.model;

public class WorkStatus {

    private Long syncDate;
    private Long endDate;
    private Integer dayStarted;

    public WorkStatus(Long syncDate, Long endDate, Integer dayStarted) {
        this.syncDate = syncDate;
        this.endDate = endDate;
        this.dayStarted = dayStarted;
    }

    public Long getSyncDate() {
        return syncDate;
    }

    public void setSyncDate(Long syncDate) {
        this.syncDate = syncDate;
    }

    public Long getEndDate() {
        return endDate;
    }

    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }

    public Integer getDayStarted() {
        return dayStarted==null?0:dayStarted;
    }

    public boolean isDayStarted(){
        return getDayStarted()==1;
    }

    public void setDayStarted(Integer dayStarted) {
        this.dayStarted = dayStarted;
    }

}
