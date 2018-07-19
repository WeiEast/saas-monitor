package com.treefinance.saas.monitor.facade.domain.ro.autoalarm;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author:guoguoyun
 * @date:Created in 2018/7/18下午7:37
 */
@Setter
@Getter
public class AsAlarmTriggerRecordRO {
    /**
     * 记录表ID
     */
    private long id ;
    /**
     * 预警配置ID
     */
    private long alarmId;
    /**
     * 预警触发条件ID
     */
    private long conditionId;
    /**
     * 执行时间
     */
    private Date runTime;
    /**
     * 执行环境 (1-生产，2-预发布)
     */
    private Byte runEnv;
    /**
     * 预警耗时
     */
    private Integer costTime;
    /**
     * 预警处理上下文
     */
    private  String context;
    /**
     * info触发
     */
    private String infoTrigger;
    /**
     * warning触发
     */
    private String warningTrigger;
    /**
     * error触发
     */
    private String errorTrigger;
    /**
     * 预警恢复
     */
    private String recoveryTrigger;
    /**
     * 预警恢复信息
     */
    private String recoveryMessage;
    /**
     * 预警信息
     */
    private String alarmMessage;
    /**
     * 通知方式
     */
    private String notifyTypes;







}
