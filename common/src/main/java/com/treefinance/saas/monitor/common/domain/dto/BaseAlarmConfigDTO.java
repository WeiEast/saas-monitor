package com.treefinance.saas.monitor.common.domain.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author chengtong
 * @date 18/5/24 10:28
 */

@Setter
@Getter
@ToString
public class BaseAlarmConfigDTO implements Serializable {

    /**
     * 任务超时时间
     */
    private Integer taskTimeoutSecs;
    /**
     * 此appId是否预警,方便以后开关
     */
    private String alarmSwitch;
    /**
     * 分区时间段 表示的是统计的时间段
     */
    private Integer intervalMins;



}
