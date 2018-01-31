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

    private String alarmSimpleDesc;//预警简化描述,微信内容

    private BigDecimal value;//当前指标

    private String valueDesc;//当前指标描述

    private BigDecimal threshold;//指标阈值

    private String thresholdDesc;//指标阈值描述

    private BigDecimal offset;//偏离阈值程度

    private String alarmType;//预警类型

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

    public String getAlarmSimpleDesc() {
        return alarmSimpleDesc;
    }

    public void setAlarmSimpleDesc(String alarmSimpleDesc) {
        this.alarmSimpleDesc = alarmSimpleDesc;
    }

    public String getValueDesc() {
        return valueDesc;
    }

    public void setValueDesc(String valueDesc) {
        this.valueDesc = valueDesc;
    }

    public String getThresholdDesc() {
        return thresholdDesc;
    }

    public void setThresholdDesc(String thresholdDesc) {
        this.thresholdDesc = thresholdDesc;
    }

    public String getAlarmType(){
        return alarmType;
    }

    public void setAlarmType(String type){
        this.alarmType = type;
    }
}