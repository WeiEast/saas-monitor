package com.treefinance.saas.monitor.biz.alarm.model;

import com.treefinance.saas.monitor.dao.entity.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by yh-treefinance on 2018/7/18.
 */
@Getter
@Setter
public class AlarmConfig {

    /**
     * 预警主表信息
     */
    private AsAlarm alarm;

    /**
     * 预警常量
     */
    private List<AsAlarmConstant> alarmConstants;

    /**
     * 预警查询
     */
    private List<AsAlarmQuery> alarmQueries;

    /**
     * 预警变量
     */
    private List<AsAlarmVariable> alarmVariables;

    /**
     * 预警通知
     */
    private List<AsAlarmNotify> alarmNotifies;

    /**
     * 预警消息
     */
    private AsAlarmMsg alarmMsg;

    /**
     * 预警触发条件
     */
    private List<AsAlarmTrigger> alarmTriggers;
}
