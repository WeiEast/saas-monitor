package com.treefinance.saas.monitor.biz.alarm.model;

import com.treefinance.saas.monitor.common.enumeration.EAlarmLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 预警消息
 * Created by yh-treefinance on 2018/7/30.
 */
@Getter
@Setter
public class AlaramMessage implements Serializable {
    /**
     * 预警标题
     */
    private String title;
    /**
     * 预警消息
     */
    private String message;
    /**
     * 预警级别
     */
    private EAlarmLevel alarmLevel;


    public AlaramMessage(String title, String message, EAlarmLevel alarmLevel) {
        this.title = title;
        this.message = message;
        this.alarmLevel = alarmLevel;
    }

    public AlaramMessage() {
    }
}
