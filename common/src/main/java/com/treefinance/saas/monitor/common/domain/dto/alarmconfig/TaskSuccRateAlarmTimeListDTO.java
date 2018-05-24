package com.treefinance.saas.monitor.common.domain.dto.alarmconfig;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.HashMap;

/**
 * @author chengtong
 * @date 18/5/11 15:20
 */
public class TaskSuccRateAlarmTimeListDTO extends BaseTimeConfig{

    /**转化率的阈值*/
    private BigDecimal thresholdError;

    /**转化率的阈值*/
    private BigDecimal thresholdWarning;
    /**转化率的阈值*/
    private BigDecimal thresholdInfo;

    /**集成的开关配置*/
    private HashMap<String, String> switches;

    public BigDecimal getThresholdInfo() {
        return thresholdInfo;
    }

    public void setThresholdInfo(BigDecimal thresholdInfo) {
        this.thresholdInfo = thresholdInfo;
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

    public HashMap<String, String> getSwitches() {
        return switches;
    }

    public void setSwitches(HashMap<String, String> switches) {
        this.switches = switches;
    }

}
