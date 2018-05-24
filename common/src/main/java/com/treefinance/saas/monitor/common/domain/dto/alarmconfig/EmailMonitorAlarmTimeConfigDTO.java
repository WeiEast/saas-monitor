package com.treefinance.saas.monitor.common.domain.dto.alarmconfig;


import com.datatrees.common.util.DateUtils;

import java.beans.Transient;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

/**
 * @Author: chengtong
 * @Date: 18/3/9 17:01
 */
public class EmailMonitorAlarmTimeConfigDTO extends BaseTimeConfig{

    /**
     * 登录转化率
     */
    private Integer loginConversionRate;

    private Integer loginSuccessRate;

    private Integer crawlSuccessRate;

    private Integer processSuccessRate;

    private Integer callbackSuccessRate;

    private Integer wholeConversionRate;

    public Integer getLoginConversionRate() {
        return loginConversionRate;
    }

    public void setLoginConversionRate(Integer loginConversionRate) {
        this.loginConversionRate = loginConversionRate;
    }

    public Integer getLoginSuccessRate() {
        return loginSuccessRate;
    }

    public void setLoginSuccessRate(Integer loginSuccessRate) {
        this.loginSuccessRate = loginSuccessRate;
    }

    public Integer getCrawlSuccessRate() {
        return crawlSuccessRate;
    }

    public void setCrawlSuccessRate(Integer crawlSuccessRate) {
        this.crawlSuccessRate = crawlSuccessRate;
    }

    public Integer getProcessSuccessRate() {
        return processSuccessRate;
    }

    public void setProcessSuccessRate(Integer processSuccessRate) {
        this.processSuccessRate = processSuccessRate;
    }

    public Integer getCallbackSuccessRate() {
        return callbackSuccessRate;
    }

    public void setCallbackSuccessRate(Integer callbackSuccessRate) {
        this.callbackSuccessRate = callbackSuccessRate;
    }

    public Integer getWholeConversionRate() {
        return wholeConversionRate;
    }

    public void setWholeConversionRate(Integer wholeConversionRate) {
        this.wholeConversionRate = wholeConversionRate;
    }
}
