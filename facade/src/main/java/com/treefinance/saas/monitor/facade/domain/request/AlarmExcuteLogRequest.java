package com.treefinance.saas.monitor.facade.domain.request;

import com.treefinance.saas.monitor.facade.domain.base.PageRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author:guoguoyun
 * @date:Created in 2018/7/19上午10:44
 */
@Getter
@Setter
public class AlarmExcuteLogRequest extends PageRequest{
    /**
     * 预警触发记录ID
     */
    private Long id;
    /**
     * 查询初始时间
     */
    private  Date startDate;
    /**
     * 查询结束时间
     */
    private  Date endDate;
    /**
     * 预警配置名称
     */
    private String name;

    /**
     * 预警触发条件名字
     */
    private String conditionName;

    /**
     * 预警触发状态
     */
    private Byte status;


    /**
     * 执行时间
     */
    private Date runTime;

    /**
     * 执行环境
     */
    private Byte runEnv;

    /**
     * 预警耗时
     */
    private Integer costTime;

    /**
     * Info触发
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
     * 预警恢复消息
     */
    private String recoveryMessage;
    /**
     * 预警消息
     */
    private String alarmMessage;
    /**
     * 通知方式
     */
    private String notifyTypes;

    /**
     * 预警处理上下文
     */
    private String context;

}
