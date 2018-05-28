package com.treefinance.saas.monitor.common.domain.dto.alarmconfig;

import com.alibaba.fastjson.JSON;
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

    /**
     * saas环境
     */
    private Byte saasEnv;

    /**
     * saas环境描述
     */
    private String saasEnvDesc;


    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
