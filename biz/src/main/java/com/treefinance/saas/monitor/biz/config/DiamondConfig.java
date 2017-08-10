package com.treefinance.saas.monitor.biz.config;

import com.github.diamond.client.extend.annotation.AfterUpdate;
import com.github.diamond.client.extend.annotation.BeforeUpdate;
import com.github.diamond.client.extend.annotation.DAttribute;
import com.github.diamond.client.extend.annotation.DResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * super-diamond 配置
 */
@Component("diamondConfig")
@Scope
@DResource
public class DiamondConfig {
    private static final Logger logger = LoggerFactory.getLogger(DiamondConfig.class);

    @DAttribute(key = "mq.namesrvAddr")
    private String namesrvAddr;

    @DAttribute(key = "monitor.group.name")
    private String monitorGroupName;

    @DAttribute(key = "monitor.access.topic")
    private String monitorAccessTopic;

    @DAttribute(key = "monitor.access.tag")
    private String monitorAccessTag;
    /**
     * 统计时间间隔:分钟为单位
     */
    @DAttribute(key = "monitor.interval.minutes")
    private Integer monitorIntervalMinutes;
    /**
     * 统计时间间隔:分钟为单位
     */
    @DAttribute(key = "monitor.http.interval.minutes")
    private Integer monitorHttpIntervalMinutes;

    // 预警消息，group
    @DAttribute(key = "monitor.alarm.group.name")
    private String monitorAlarmGroupName;
    // 预警消息，topic
    @DAttribute(key = "monitor.alarm.topic")
    private String monitorAlarmTopic;
    // 预警消息，邮件tag
    @DAttribute(key = "monitor.alarm.tag.mail")
    private String monitorAlarmMailTag;
    // 预警消息，邮件列表
    @DAttribute(key = "monitor.alarm.mail")
    private String monitorAlarmMails;
    // 预警消息，微信tag
    @DAttribute(key = "monitor.alarm.tag.webchart")
    private String monitorAlarmWebchartTag;
    // 预警消息，报警阈值
    @DAttribute(key = "monitor.alarm.threshold")
    private Double monitorAlarmThreshold;
    // 预警消息，超阈值次数
    @DAttribute(key = "monitor.alarm.thresholdCount")
    private Integer monitorAlarmThresholdCount;
    // 预警消息，剔除的商户
    @DAttribute(key = "monitor.alarm.excludeAppIds")
    private String monitorAlarmExcludeAppIds;
    //预警消息,微信通知开关
    @DAttribute(key = "monitor.alarm.wechat.switch")
    private String monitorAlarmWechatSwitch;
    //预警消息,邮箱通知开关
    @DAttribute(key = "monitor.alarm.mail.switch")
    private String monitorAlarmMailSwitch;
    // 监控环境
    @DAttribute(key = "monitor.environment")
    private String monitorEnvironment;

    public String getMonitorEnvironment() {
        return monitorEnvironment;
    }

    public void setMonitorEnvironment(String monitorEnvironment) {
        this.monitorEnvironment = monitorEnvironment;
    }

    public String getMonitorAlarmExcludeAppIds() {
        return monitorAlarmExcludeAppIds;
    }

    public void setMonitorAlarmExcludeAppIds(String monitorAlarmExcludeAppIds) {
        this.monitorAlarmExcludeAppIds = monitorAlarmExcludeAppIds;
    }

    public Integer getMonitorAlarmThresholdCount() {
        return monitorAlarmThresholdCount;
    }

    public void setMonitorAlarmThresholdCount(Integer monitorAlarmThresholdCount) {
        this.monitorAlarmThresholdCount = monitorAlarmThresholdCount;
    }

    public String getMonitorAlarmMailTag() {
        return monitorAlarmMailTag;
    }

    public void setMonitorAlarmMailTag(String monitorAlarmMailTag) {
        this.monitorAlarmMailTag = monitorAlarmMailTag;
    }

    public String getMonitorAlarmWebchartTag() {
        return monitorAlarmWebchartTag;
    }

    public void setMonitorAlarmWebchartTag(String monitorAlarmWebchartTag) {
        this.monitorAlarmWebchartTag = monitorAlarmWebchartTag;
    }

    public String getMonitorAlarmGroupName() {
        return monitorAlarmGroupName;
    }

    public void setMonitorAlarmGroupName(String monitorAlarmGroupName) {
        this.monitorAlarmGroupName = monitorAlarmGroupName;
    }

    public String getMonitorAlarmTopic() {
        return monitorAlarmTopic;
    }

    public void setMonitorAlarmTopic(String monitorAlarmTopic) {
        this.monitorAlarmTopic = monitorAlarmTopic;
    }

    public String getMonitorAlarmMails() {
        return monitorAlarmMails;
    }

    public void setMonitorAlarmMails(String monitorAlarmMails) {
        this.monitorAlarmMails = monitorAlarmMails;
    }

    public Double getMonitorAlarmThreshold() {
        return monitorAlarmThreshold;
    }

    public void setMonitorAlarmThreshold(Double monitorAlarmThreshold) {
        this.monitorAlarmThreshold = monitorAlarmThreshold;
    }

    @BeforeUpdate
    public void before(String key, Object newValue) {
        logger.info(key + " update to " + newValue + " start...");
    }

    @AfterUpdate
    public void after(String key, Object newValue) {
        logger.info(key + " update to " + newValue + " end...");
    }

    public String getNamesrvAddr() {
        return namesrvAddr;
    }

    public String getMonitorGroupName() {
        return monitorGroupName;
    }

    public String getMonitorAccessTopic() {
        return monitorAccessTopic;
    }

    public String getMonitorAccessTag() {
        return monitorAccessTag;
    }

    public Integer getMonitorIntervalMinutes() {
        return monitorIntervalMinutes;
    }

    public void setNamesrvAddr(String namesrvAddr) {
        this.namesrvAddr = namesrvAddr;
    }

    public void setMonitorGroupName(String monitorGroupName) {
        this.monitorGroupName = monitorGroupName;
    }

    public void setMonitorAccessTopic(String monitorAccessTopic) {
        this.monitorAccessTopic = monitorAccessTopic;
    }

    public void setMonitorAccessTag(String monitorAccessTag) {
        this.monitorAccessTag = monitorAccessTag;
    }

    public void setMonitorIntervalMinutes(Integer monitorIntervalMinutes) {
        this.monitorIntervalMinutes = monitorIntervalMinutes;
    }

    public Integer getMonitorHttpIntervalMinutes() {
        return monitorHttpIntervalMinutes;
    }

    public void setMonitorHttpIntervalMinutes(Integer monitorHttpIntervalMinutes) {
        this.monitorHttpIntervalMinutes = monitorHttpIntervalMinutes;
    }

    public String getMonitorAlarmWechatSwitch() {
        return monitorAlarmWechatSwitch;
    }

    public void setMonitorAlarmWechatSwitch(String monitorAlarmWechatSwitch) {
        this.monitorAlarmWechatSwitch = monitorAlarmWechatSwitch;
    }

    public String getMonitorAlarmMailSwitch() {
        return monitorAlarmMailSwitch;
    }

    public void setMonitorAlarmMailSwitch(String monitorAlarmMailSwitch) {
        this.monitorAlarmMailSwitch = monitorAlarmMailSwitch;
    }
}
