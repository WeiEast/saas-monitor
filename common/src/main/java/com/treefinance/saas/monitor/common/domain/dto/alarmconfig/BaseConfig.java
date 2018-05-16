package com.treefinance.saas.monitor.common.domain.dto.alarmconfig;

import java.io.Serializable;

/**
 * @author chengtong
 * @date 18/5/16 10:56
 */
public class BaseConfig implements Serializable {

    static final long serialVersionUID = 42123131212L;

    private String alarmSwitch;

    public String getAlarmSwitch() {
        return alarmSwitch;
    }

    public void setAlarmSwitch(String alarmSwitch) {
        this.alarmSwitch = alarmSwitch;
    }

}
