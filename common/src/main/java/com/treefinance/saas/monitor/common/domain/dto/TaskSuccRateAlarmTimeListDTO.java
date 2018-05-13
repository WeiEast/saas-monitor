package com.treefinance.saas.monitor.common.domain.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalTime;

/**
 * @author chengtong
 * @date 18/5/11 15:20
 */
public class TaskSuccRateAlarmTimeListDTO implements Serializable{

    /**时间区间开始*/
    private String startTime;

    /**时间区间结束*/
    private String endTime;

    /**是否在时间区间*/
    private boolean inTime;

    /**转化率的阈值*/
    private BigDecimal thresholdError;

    /**转化率的阈值*/
    private BigDecimal thresholdWarning;
    /**转化率的阈值*/
    private BigDecimal thresholdInfo;


    public BigDecimal getThresholdInfo() {
        return thresholdInfo;
    }

    public void setThresholdInfo(BigDecimal thresholdInfo) {
        this.thresholdInfo = thresholdInfo;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public boolean isInTime() {
        LocalTime now = LocalTime.now();

        LocalTime start = LocalTime.parse(startTime);
        LocalTime end = LocalTime.parse(endTime);

        return now.isBefore(end) && now.isAfter(start);
    }

    public void setInTime(boolean inTime) {
        this.inTime = inTime;
    }

    public BigDecimal getThresholdError() {
        return thresholdError;
    }

    public void setThresholdError(BigDecimal thresholdError) {
        this.thresholdError = thresholdError;
    }

    public BigDecimal getThresholdWarning() {
        return thresholdWarning;
    }

    public void setThresholdWarning(BigDecimal thresholdWarning) {
        this.thresholdWarning = thresholdWarning;
    }
}
