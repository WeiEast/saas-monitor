package com.treefinance.saas.monitor.common.domain.dto.alarmconfig;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author chengtong
 * @date 18/5/16 10:56
 */
@Getter
@Setter
@ToString
public class BaseAlarmConfigDTO implements Serializable {

    static final long serialVersionUID = 42123131212L;

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
