package com.treefinance.saas.monitor.common.domain.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author chengtong
 * @date 18/3/13 11:34
 */
public class BaseAlarmMsgDTO implements Serializable{

    /**统计时间*/
    private Date dataTime;

    /**预警描述*/
    private String alarmDesc;

    /**预警简化描述,微信内容*/
    private String alarmSimpleDesc;

    /**当前指标*/
    private BigDecimal value;

    /**当前指标描述*/
    private String valueDesc;

    /**指标阈值*/
    private BigDecimal threshold;

    /**指标阈值描述*/
    private String thresholdDesc;

    /**偏离阈值程度*/
    private BigDecimal offset;

    /**预警类型*/
    private String alarmType;

    /**预警等级*/
    private Enum alarmLevel;
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

    public String getAlarmSimpleDesc() {
        return alarmSimpleDesc;
    }

    public void setAlarmSimpleDesc(String alarmSimpleDesc) {
        this.alarmSimpleDesc = alarmSimpleDesc;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public String getValueDesc() {
        return valueDesc;
    }

    public void setValueDesc(String valueDesc) {
        this.valueDesc = valueDesc;
    }

    public BigDecimal getThreshold() {
        return threshold;
    }

    public void setThreshold(BigDecimal threshold) {
        this.threshold = threshold;
    }

    public String getThresholdDesc() {
        return thresholdDesc;
    }

    public void setThresholdDesc(String thresholdDesc) {
        this.thresholdDesc = thresholdDesc;
    }

    public BigDecimal getOffset() {
        return offset;
    }

    public void setOffset(BigDecimal offset) {
        this.offset = offset;
    }

    public String getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(String alarmType) {
        this.alarmType = alarmType;
    }

    public Enum getAlarmLevel() {
        return alarmLevel;
    }

    public void setAlarmLevel(Enum alarmLevel) {
        this.alarmLevel = alarmLevel;
    }


}
