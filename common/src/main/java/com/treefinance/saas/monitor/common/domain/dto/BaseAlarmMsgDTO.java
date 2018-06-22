package com.treefinance.saas.monitor.common.domain.dto;

import com.treefinance.saas.monitor.common.enumeration.EAlarmAspectType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author chengtong
 * @date 18/3/13 11:34
 */
@Getter
@Setter
@ToString
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

    /**预警指标的类型*/
    private EAlarmAspectType alarmAspectType;


}
