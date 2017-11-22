package com.treefinance.saas.monitor.common.domain.dto;

import java.io.Serializable;

/**
 * Created by haojiahong on 2017/11/22.
 */
public class TaskExistAlarmNoSuccessMinsConfigDTO implements Serializable {

    private static final long serialVersionUID = -8990996860189127367L;

    private String type;
    private Integer dayMins;
    private Integer nightMins;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getDayMins() {
        return dayMins;
    }

    public void setDayMins(Integer dayMins) {
        this.dayMins = dayMins;
    }

    public Integer getNightMins() {
        return nightMins;
    }

    public void setNightMins(Integer nightMins) {
        this.nightMins = nightMins;
    }
}
