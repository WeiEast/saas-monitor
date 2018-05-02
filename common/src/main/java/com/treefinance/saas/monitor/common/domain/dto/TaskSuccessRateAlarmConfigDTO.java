package com.treefinance.saas.monitor.common.domain.dto;

import com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

/**
 * Created by haojiahong on 2017/11/24.
 */
public class TaskSuccessRateAlarmConfigDTO implements Serializable {

    private static final long serialVersionUID = 7938223164128700307L;

    /**
     * saas环境
     */
    private Byte saasEnv;

    /**
     * saas环境描述
     */
    private String saasEnvDesc;


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
     */*/
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
     * 此配置生效的预警开始时间,如0:00
     */
    private String alarmStartTime;

    /**
     * 此配置生效的预警结束时间,如7:00
     */
    private String alarmEndTime;

    /**
     * 邮箱报警开关
     */
    private String mailAlarmSwitch;

    /**
     * 微信报警开关
     */
    private String weChatAlarmSwitch;

    /**
     * 短信预警开关
     */
    private String smsAlarmSwitch;

    public static void main(String[] args) {
        List<TaskSuccessRateAlarmConfigDTO> list = Lists.newArrayList();
        TaskSuccessRateAlarmConfigDTO dto1 = new TaskSuccessRateAlarmConfigDTO();
        dto1.setSaasEnv((byte) 0);
        dto1.setSaasEnvDesc("所有环境");
        dto1.setType("OPERATOR");
        dto1.setSuccesThreshold(40);
        dto1.setIntervalMins(3);
        dto1.setTimes(3);
        dto1.setTaskTimeoutSecs(600);
        dto1.setAlarmStartTime("0:00");
        dto1.setAlarmEndTime("7:00");
        dto1.setMailAlarmSwitch("on");
        dto1.setWeChatAlarmSwitch("on");
        dto1.setSmsAlarmSwitch("on");

        TaskSuccessRateAlarmConfigDTO dto2 = new TaskSuccessRateAlarmConfigDTO();
        dto2.setSaasEnv((byte) 0);
        dto2.setSaasEnvDesc("所有环境");
        dto2.setType("OPERATOR");
        dto2.setSuccesThreshold(40);
        dto2.setIntervalMins(3);
        dto2.setTimes(3);
        dto2.setTaskTimeoutSecs(600);
        dto2.setAlarmStartTime("7:00");
        dto2.setAlarmEndTime("24:00");
        dto2.setMailAlarmSwitch("on");
        dto2.setWeChatAlarmSwitch("on");
        dto2.setSmsAlarmSwitch("on");

        list.add(dto1);
        list.add(dto2);
        System.out.println(JSON.toJSONString(list));
    }


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

    public String getSmsAlarmSwitch() {
        return smsAlarmSwitch;
    }

    public void setSmsAlarmSwitch(String smsAlarmSwitch) {
        this.smsAlarmSwitch = smsAlarmSwitch;
    }

    public String getAlarmStartTime() {
        return alarmStartTime;
    }

    public void setAlarmStartTime(String alarmStartTime) {
        this.alarmStartTime = alarmStartTime;
    }

    public String getAlarmEndTime() {
        return alarmEndTime;
    }

    public void setAlarmEndTime(String alarmEndTime) {
        this.alarmEndTime = alarmEndTime;
    }

    public Byte getSaasEnv() {
        return saasEnv;
    }

    public void setSaasEnv(Byte saasEnv) {
        this.saasEnv = saasEnv;
    }

    public String getSaasEnvDesc() {
        return saasEnvDesc;
    }

    public void setSaasEnvDesc(String saasEnvDesc) {
        this.saasEnvDesc = saasEnvDesc;
    }
}
