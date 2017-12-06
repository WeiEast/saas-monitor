package com.treefinance.saas.monitor.common.domain.dto;

import com.treefinance.saas.monitor.common.domain.BaseDTO;

/**
 * Created by yh-treefinance on 2017/12/6.
 */
public class IvrContactsDTO extends BaseDTO {
    /**
     * 通知人员
     */
    private String name;
    /**
     * 联系方式
     */
    private String telNum;

    /**
     * 值班时间：cron 表达式
     */
    private String[] dutyTimeCron;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTelNum() {
        return telNum;
    }

    public void setTelNum(String telNum) {
        this.telNum = telNum;
    }

    public String[] getDutyTimeCron() {
        return dutyTimeCron;
    }

    public void setDutyTimeCron(String[] dutyTimeCron) {
        this.dutyTimeCron = dutyTimeCron;
    }
}
