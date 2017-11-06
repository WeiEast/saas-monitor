package com.treefinance.saas.monitor.common.domain.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class OperatorStatAccessAlarmMsgDTO implements Serializable {

    private static final long serialVersionUID = -6477590375789936061L;

    private String groupCode;

    private String groupName;

    private Date dataTime;

    private String alarmDesc;//预警描述

    private BigDecimal value;//当前指标

    private BigDecimal threshold;//指标阈值

    private BigDecimal offset;//偏离阈值程度

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

    public Date getDataTime() {
        return dataTime;
    }

    public void setDataTime(Date dataTime) {
        this.dataTime = dataTime;
    }

    public String getAlarmDesc() {
        return alarmDesc;
    }

    public void setAlarmDesc(String alarmDesc) {
        this.alarmDesc = alarmDesc;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public BigDecimal getThreshold() {
        return threshold;
    }

    public void setThreshold(BigDecimal threshold) {
        this.threshold = threshold;
    }

    public BigDecimal getOffset() {
        return offset;
    }

    public void setOffset(BigDecimal offset) {
        this.offset = offset;
    }
}