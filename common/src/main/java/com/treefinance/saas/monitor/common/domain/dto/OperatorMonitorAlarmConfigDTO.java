package com.treefinance.saas.monitor.common.domain.dto;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

/**
 * Created by haojiahong on 2017/12/5.
 */
public class OperatorMonitorAlarmConfigDTO implements Serializable {

    private static final long serialVersionUID = 4249280455861844218L;

    private String appId;
    private String appName;

    /**
     * saas环境: 0:所有环境 1:生产环境 2:预发布环境
     */
    private Byte saasEnv;
    private String saasEnvDesc;

    /**
     * 数据类型: 0:按任务 1:按人数
     */
    private Byte dataType;
    private String dataTypeDesc;

    /**
     * 预警维度: 1:总运营商 2:分运营商
     */
    private Integer alarmType;
    private String alarmTypeDesc;

    /**
     * 任务超时时间
     */
    private Integer taskTimeoutSecs;
    /**
     * 此appId是否预警,方便以后开关(总开关)
     */
    private String alarmSwitch;

    private Integer intervalMins;
    private Integer previousDays;

    private Integer confirmMobileConversionRate;
    /**
     * 登录转化率
     */
    private Integer loginConversionRate;

    private Integer loginSuccessRate;

    private Integer crawlSuccessRate;

    private Integer processSuccessRate;

    private Integer callbackSuccessRate;

    private Integer wholeConversionRate;

    private String mailAlarmSwitch;
    private String weChatAlarmSwitch;

    public static void main(String[] args) {
        List<OperatorMonitorAlarmConfigDTO> list = Lists.newArrayList();
        OperatorMonitorAlarmConfigDTO dto1 = new OperatorMonitorAlarmConfigDTO();
        dto1.setAppId("virtual_total_stat_appId");
        dto1.setAppName("所有商户");
        dto1.setAlarmType(1);
        dto1.setAlarmTypeDesc("总运营商");
        dto1.setSaasEnv((byte) 0);
        dto1.setSaasEnvDesc("所有环境");
        dto1.setDataType((byte) 1);
        dto1.setDataTypeDesc("按人数");
        dto1.setIntervalMins(30);
        dto1.setTaskTimeoutSecs(600);
        dto1.setAlarmSwitch("on");
        dto1.setPreviousDays(7);
        dto1.setConfirmMobileConversionRate(70);
        dto1.setLoginConversionRate(70);
        dto1.setLoginSuccessRate(70);
        dto1.setCrawlSuccessRate(70);
        dto1.setProcessSuccessRate(70);
        dto1.setCallbackSuccessRate(70);
        dto1.setWholeConversionRate(90);
        dto1.setMailAlarmSwitch("on");
        dto1.setWeChatAlarmSwitch("on");
        list.add(dto1);

        OperatorMonitorAlarmConfigDTO dto2 = new OperatorMonitorAlarmConfigDTO();
        dto2.setAppId("virtual_total_stat_appId");
        dto2.setAppName("所有商户");
        dto2.setAlarmType(2);
        dto2.setAlarmTypeDesc("分运营商");
        dto2.setSaasEnv((byte) 0);
        dto2.setSaasEnvDesc("所有环境");
        dto2.setDataType((byte) 1);
        dto2.setDataTypeDesc("按人数");
        dto2.setIntervalMins(30);
        dto2.setTaskTimeoutSecs(600);
        dto2.setAlarmSwitch("on");
        dto2.setPreviousDays(7);
        dto2.setConfirmMobileConversionRate(70);
        dto2.setLoginConversionRate(70);
        dto2.setLoginSuccessRate(70);
        dto2.setCrawlSuccessRate(70);
        dto2.setProcessSuccessRate(70);
        dto2.setCallbackSuccessRate(70);
        dto2.setMailAlarmSwitch("on");
        dto2.setWeChatAlarmSwitch("on");
        list.add(dto2);
        System.out.println(JSON.toJSONString(list));

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

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Integer getTaskTimeoutSecs() {
        return taskTimeoutSecs;
    }

    public void setTaskTimeoutSecs(Integer taskTimeoutSecs) {
        this.taskTimeoutSecs = taskTimeoutSecs;
    }

    public String getAlarmSwitch() {
        return alarmSwitch;
    }

    public void setAlarmSwitch(String alarmSwitch) {
        this.alarmSwitch = alarmSwitch;
    }

    public Integer getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(Integer alarmType) {
        this.alarmType = alarmType;
    }

    public String getAlarmTypeDesc() {
        return alarmTypeDesc;
    }

    public void setAlarmTypeDesc(String alarmTypeDesc) {
        this.alarmTypeDesc = alarmTypeDesc;
    }

    public Integer getIntervalMins() {
        return intervalMins;
    }

    public void setIntervalMins(Integer intervalMins) {
        this.intervalMins = intervalMins;
    }

    public Integer getPreviousDays() {
        return previousDays;
    }

    public void setPreviousDays(Integer previousDays) {
        this.previousDays = previousDays;
    }

    public Integer getConfirmMobileConversionRate() {
        return confirmMobileConversionRate;
    }

    public void setConfirmMobileConversionRate(Integer confirmMobileConversionRate) {
        this.confirmMobileConversionRate = confirmMobileConversionRate;
    }

    public Integer getLoginConversionRate() {
        return loginConversionRate;
    }

    public void setLoginConversionRate(Integer loginConversionRate) {
        this.loginConversionRate = loginConversionRate;
    }

    public Integer getLoginSuccessRate() {
        return loginSuccessRate;
    }

    public void setLoginSuccessRate(Integer loginSuccessRate) {
        this.loginSuccessRate = loginSuccessRate;
    }

    public Integer getCrawlSuccessRate() {
        return crawlSuccessRate;
    }

    public void setCrawlSuccessRate(Integer crawlSuccessRate) {
        this.crawlSuccessRate = crawlSuccessRate;
    }

    public Integer getProcessSuccessRate() {
        return processSuccessRate;
    }

    public void setProcessSuccessRate(Integer processSuccessRate) {
        this.processSuccessRate = processSuccessRate;
    }

    public Integer getCallbackSuccessRate() {
        return callbackSuccessRate;
    }

    public void setCallbackSuccessRate(Integer callbackSuccessRate) {
        this.callbackSuccessRate = callbackSuccessRate;
    }

    public Integer getWholeConversionRate() {
        return wholeConversionRate;
    }

    public void setWholeConversionRate(Integer wholeConversionRate) {
        this.wholeConversionRate = wholeConversionRate;
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

    public Byte getDataType() {
        return dataType;
    }

    public void setDataType(Byte dataType) {
        this.dataType = dataType;
    }

    public String getDataTypeDesc() {
        return dataTypeDesc;
    }

    public void setDataTypeDesc(String dataTypeDesc) {
        this.dataTypeDesc = dataTypeDesc;
    }
}
