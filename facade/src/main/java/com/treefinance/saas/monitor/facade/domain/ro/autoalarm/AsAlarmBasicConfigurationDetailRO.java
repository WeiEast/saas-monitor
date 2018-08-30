package com.treefinance.saas.monitor.facade.domain.ro.autoalarm;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * @author haojiahong
 * @date 2018/7/19
 */
@Getter
@Setter
public class AsAlarmBasicConfigurationDetailRO implements Serializable {
    private static final long serialVersionUID = 6958602035029883720L;

    /**
     * 预警配置表
     */
    private AsAlarmRO asAlarmRO;

    /**
     * 预警常量表
     */
    private List<AsAlarmConstantRO> asAlarmConstantROList;

    /**
     * 预警数据查询表
     */
    private List<AsAlarmQueryRO> asAlarmQueryROList;

    /**
     * 预警变量表
     */
    private List<AsAlarmVariableRO> asAlarmVariableROList;

    /**
     * 预警通知表
     */
    private List<AsAlarmNotifyRO> asAlarmNotifyROList;

    /**
     * 预警消息模板表
     */
    private List<AsAlarmMsgRO> asAlarmMsgROList;

    /**
     * 预警触发条件表
     */
    private List<AsAlarmTriggerRO> asAlarmTriggerROList;


}
