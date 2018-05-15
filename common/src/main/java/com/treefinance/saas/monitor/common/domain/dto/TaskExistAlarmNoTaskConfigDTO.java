package com.treefinance.saas.monitor.common.domain.dto;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

/**
 * Created by haojiahong on 2017/11/22.
 */
public class TaskExistAlarmNoTaskConfigDTO implements Serializable {


    private static final long serialVersionUID = -3347705340925373780L;
    /**
     * 业务权限
     */
    private Byte bizType;
    /**
     * 业务权限描述
     */
    private String bizTypeDesc;

    /**
     * 白天(0,7)无成功任务间隔时间,最小配置5分钟
     */
    private Integer dayMins;
    /**
     * 晚上(7,24)无成功任务间隔时间,最小配置5分钟
     */
    private Integer nightMins;

    /**
     * saas环境: 0:所有环境 1:生产环境 2:预发布环境
     */
    private Byte saasEnv;
    private String saasEnvDesc;
    /**
     * 总开关
     */
    private String alarmSwitch;
    /**
     * 邮箱预警开关
     */
    private String mailAlarmSwitch;
    /**
     * 微信预警开关
     */
    private String weChatAlarmSwitch;

    /**
     * 短信预警开关
     */
    private String smsAlarmSwitch;


    public Integer getDayMins() {
        return dayMins;
    }

    public void setDayMins(Integer dayMins) {
        this.dayMins = dayMins;
    }

    public Integer getNightMins() {
        return nightMins;
    }

    public void setNightMins(Integer nightMins) {
        this.nightMins = nightMins;
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

    public Byte getBizType() {
        return bizType;
    }

    public void setBizType(Byte bizType) {
        this.bizType = bizType;
    }

    public String getBizTypeDesc() {
        return bizTypeDesc;
    }

    public void setBizTypeDesc(String bizTypeDesc) {
        this.bizTypeDesc = bizTypeDesc;
    }

    public String getAlarmSwitch() {
        return alarmSwitch;
    }

    public void setAlarmSwitch(String alarmSwitch) {
        this.alarmSwitch = alarmSwitch;
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

    public static void main(String[] args) {
        List<TaskExistAlarmNoTaskConfigDTO> list = Lists.newArrayList();
        TaskExistAlarmNoTaskConfigDTO config1 = new TaskExistAlarmNoTaskConfigDTO();
        config1.setAlarmSwitch("on");
        config1.setMailAlarmSwitch("on");
        config1.setWeChatAlarmSwitch("on");
        config1.setBizType(null);
        config1.setBizTypeDesc("所有业务类型");
        config1.setSaasEnv((byte) 0);
        config1.setSaasEnvDesc("所有环境");
        config1.setDayMins(10);
        config1.setNightMins(30);
        list.add(config1);

        TaskExistAlarmNoTaskConfigDTO config2 = new TaskExistAlarmNoTaskConfigDTO();
        config2.setAlarmSwitch("on");
        config2.setMailAlarmSwitch("on");
        config2.setWeChatAlarmSwitch("on");
        config2.setBizType((byte) 3);
        config2.setBizTypeDesc("运营商");
        config2.setSaasEnv((byte) 0);
        config2.setSaasEnvDesc("所有环境");
        config2.setDayMins(10);
        config2.setNightMins(20);
        list.add(config2);

        System.out.println(JSON.toJSONString(list));
    }
}
