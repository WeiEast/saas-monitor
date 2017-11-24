package com.treefinance.saas.monitor.common.domain.dto;

import java.io.Serializable;

/**
 * Created by haojiahong on 2017/11/24.
 */
public class TaskSuccessRateAlarmConfigDTO implements Serializable {

    private static final long serialVersionUID = 7938223164128700307L;

    /**
     * 任务业务类型
     */
    private String type;
    /**
     * 任务成功率预警阀值
     */
    private Integer succesThreshold;
    /**
     * 预警间隔时间,5分钟,10分钟等
     */
    private Integer intervalMins;
    /**
     * 低于阀值多少次预警,3次,5次等
     */
    private Integer times;

    /**
     * 任务超时时间
     */
    private Integer taskTimeoutSecs;

    /**
     * 邮箱报警开关
     */
    private String mailAlarmSwitch;

    /**
     * 微信报警开关
     */
    private String weChatAlarmSwitch;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getSuccesThreshold() {
        return succesThreshold;
    }

    public void setSuccesThreshold(Integer succesThreshold) {
        this.succesThreshold = succesThreshold;
    }

    public Integer getIntervalMins() {
        return intervalMins;
    }

    public void setIntervalMins(Integer intervalMins) {
        this.intervalMins = intervalMins;
    }

    public Integer getTimes() {
        return times;
    }

    public void setTimes(Integer times) {
        this.times = times;
    }

    public Integer getTaskTimeoutSecs() {
        return taskTimeoutSecs;
    }

    public void setTaskTimeoutSecs(Integer taskTimeoutSecs) {
        this.taskTimeoutSecs = taskTimeoutSecs;
    }

    public String getMailAlarmSwitch() {
        return mailAlarmSwitch;
    }

    public void setMailAlarmSwitch(String mailAlarmSwitch) {
        this.mailAlarmSwitch = mailAlarmSwitch;
    }

    public String getWeChatAlarmSwitch() {
        return weChatAlarmSwitch;
    }

    public void setWeChatAlarmSwitch(String weChatAlarmSwitch) {
        this.weChatAlarmSwitch = weChatAlarmSwitch;
    }
}
