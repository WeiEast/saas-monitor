package com.treefinance.saas.monitor.common.domain.dto;

import java.util.Date;

/**
 * @author:guoguoyun
 * @date:Created in 2018/1/16下午4:09
 */
public class EcommerceTimeShareDTO {

    private Date dataDate;

    private Date startDate;

    private Date endDate;

    private String groupCode;

    private String groupName;

    private Byte statType;

    private String appId;

    public Date getDataDate() {
        return dataDate;
    }

    public void setDataDate(Date dataDate) {
        this.dataDate = dataDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Byte getStatType() {
        return statType;
    }

    public void setStatType(Byte statType) {
        this.statType = statType;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    @Override
    public String toString() {
        return "EcommerceTimeShareDTO{" +
                "dataDate=" + dataDate +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", groupCode='" + groupCode + '\'' +
                ", groupName='" + groupName + '\'' +
                ", statType=" + statType +
                ", appId='" + appId + '\'' +
                '}';
    }
}
