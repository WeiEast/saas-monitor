package com.treefinance.saas.monitor.biz.alarm.model;

import com.treefinance.saas.monitor.common.enumeration.EAlarmChannel;
import com.treefinance.saas.monitor.common.enumeration.EAlarmLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 预警消息
 * Created by yh-treefinance on 2018/7/30.
 */
@Getter
@Setter
public class AlarmMessage implements Serializable {
    /**
     * 预警编号
     */
    private Long alarmNo;

    /**
     * 预警记录ID
     */
    private Long recordId;
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

    /**
     * 预警消息类型
     */
    private EAnalysisType messageType;
    /**
     * 消息通道
     */
    private List<EAlarmChannel> alarmChannels;
}
