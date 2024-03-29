package com.treefinance.saas.monitor.biz.config;

import com.github.diamond.client.extend.annotation.AfterUpdate;
import com.github.diamond.client.extend.annotation.BeforeUpdate;
import com.github.diamond.client.extend.annotation.DAttribute;
import com.github.diamond.client.extend.annotation.DResource;
import org.apache.commons.lang3.StringUtils;
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

    @DAttribute(key = "email.support.list")
    private String supportEmails;

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
     * 任务成功率预警配置(json字符串)
     * [{"alarmEndTime":"7:00","alarmStartTime":"0:00","intervalMins":3,
     * "mailAlarmSwitch":"on","saasEnv":0,"saasEnvDesc":"所有环境","smsAlarmSwitch":"on",
     * "succesThreshold":40,"taskTimeoutSecs":600,"times":3,"type":"OPERATOR","weChatAlarmSwitch":"on"},
     * {"alarmEndTime":"24:00","alarmStartTime":"7:00","intervalMins":3,
     * "mailAlarmSwitch":"on","saasEnv":0,"saasEnvDesc":"所有环境","smsAlarmSwitch":"on",
     * "succesThreshold":40,"taskTimeoutSecs":600,"times":3,"type":"OPERATOR","weChatAlarmSwitch":"on"}]
     */
    @DAttribute(key = "task.success.rate.alarm.config")
    private String taskSuccessRateAlarmConfig;

    @DAttribute(key = "saas.alarm.monitor.duty.ivr.switch")
    private String dutyIvrSwitch;

    /**
     * 运营商按人数预警配置
     */
    @DAttribute(key = "operator.monitor.alarm.config")
    private String operatorMonitorAlarmConfig;

    /**
     * (运营商监控)统计时间间隔:分钟为单位
     */
    @DAttribute(key = "operator.monitor.interval.minutes")
    private Integer operatorMonitorIntervalMinutes;

    /**
     * (运营商监控)统计时间间隔:分钟为单位
     */
    @DAttribute(key = "console.address")
    private String consoleAddress;

    /**
     * 运营商预警,需要预警的运营商列表
     */
    @DAttribute(key = "operator.alarm.operator.name.list")
    private String operatorAlarmOperatorNameList;

    /**
     * 运营商预警,确定预警等级的上下界 10
     */
    @DAttribute(key = "operator.alarm.level.error.lower")
    private Integer errorLower;

    /**
     * 运营商预警,确定预警等级的上下界，warning等级的下界，<= warningLower info; > warningLower && < errorLower warning ; >= errorLower error
     * 5
     */
    @DAttribute(key = "operator.alarm.level.warning.lower")
    private Integer warningLower;


    @DAttribute(key = "operator.all.alarm.level.error.lower")
    private Integer allErrorLower;

    @DAttribute(key = "operator.all.alarm.level.warning.lower")
    private Integer allErrorWarning;

    /**
     * 运营商预警,统计数量低于此值时,取特殊阀值处理
     */
    @DAttribute(key = "operator.alarm.few.num")
    private Integer operatorAlarmFewNum;

    /**
     * 运营商预警,统计数量低于一定值时,设置的特殊处理阀值
     */
    @DAttribute(key = "operator.alarm.few.threshold.percent")
    private Integer operatorAlarmFewThresholdPercent;

    /**
     * 任务预警,一定时间内无成功任务预警,json字符串格式
     */
    @DAttribute(key = "task.exist.alarm.no.success.task.config")
    private String taskExistAlarmNoSuccessTaskConfig;

    /**
     * 任务预警,一定时间内无任务预警,json字符串格式
     */
    @DAttribute(key = "task.exist.alarm.no.task.config")
    private String taskExistAlarmNoTaskConfig;


    /**
     * 统计时间间隔:分钟为单位
     */
    @DAttribute(key = "monitor.http.interval.minutes")
    private Integer monitorHttpIntervalMinutes;

    /**
     * 电商预警配置
     */
    @DAttribute(key = "ecommerce.monitor.alarm.config")
    private String ecommerceMonitorAlarmConfig;
    /**
     * 电商预警配置
     */
    @DAttribute(key = "ecommerce.monitor.alarm.bounds")
    private String ecommerceMonitorAlarmBounds;

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
    //预警消息,手机列表
    @DAttribute(key = "monitor.alarm.mobile")
    private String monitorAlarmMobiles;
    // 预警消息，微信tag
    @DAttribute(key = "monitor.alarm.tag.webchart")
    private String monitorAlarmWebchartTag;
    // 预警消息，报警阈值
    @DAttribute(key = "monitor.alarm.threshold")
    private Double monitorAlarmThreshold;
    // 预警消息，报警阈值
    @DAttribute(key = "monitor.alarm.threshold.max")
    private Double monitorAlarmThresholdMax;
    // 预警消息，超阈值次数
    @DAttribute(key = "monitor.alarm.thresholdCount")
    private Integer monitorAlarmThresholdCount;
    // 预警消息，剔除的商户
    @DAttribute(key = "monitor.alarm.excludeAppIds")
    private String monitorAlarmExcludeAppIds;
    // 预警消息，剔除的环境
    @DAttribute(key = "monitor.alarm.exclude.environment")
    private String monitorAlarmExcludeEnvironment;
    //预警消息,微信通知开关
    @DAttribute(key = "monitor.alarm.wechat.switch")
    private String monitorAlarmWechatSwitch;
    //预警消息,邮箱通知开关
    @DAttribute(key = "monitor.alarm.mail.switch")
    private String monitorAlarmMailSwitch;
    //预警消息,短信通知开关
    @DAttribute(key = "monitor.alarm.sms.switch")
    private String monitorAlarmSmsSwitch;
    // 监控环境
    @DAttribute(key = "monitor.environment")
    private String monitorEnvironment;
    //预警消息,剔除的商户
    @DAttribute(key = "monitor.alarm.excludeAppIdsAll")
    private String monitorAlarmExcludeAppIdsAll;
    /**
     * ivr 服务密钥
     */
    @DAttribute(key = "auth.ivr.accessKey")
    private String ivrAccessKey;
    /**
     * ivr token
     */
    @DAttribute(key = "auth.ivr.token")
    private String ivrToken;

    /**
     * 原预警总开关
     */
    @DAttribute(key = "old.alarm.all.switch")
    private String oldAlarmAllSwitch;

    public String getEcommerceMonitorAlarmBounds() {
        return ecommerceMonitorAlarmBounds;
    }

    public void setEcommerceMonitorAlarmBounds(String ecommerceMonitorAlarmBounds) {
        this.ecommerceMonitorAlarmBounds = ecommerceMonitorAlarmBounds;
    }

    public String getIvrAccessKey() {
        return ivrAccessKey;
    }

    public void setIvrAccessKey(String ivrAccessKey) {
        this.ivrAccessKey = ivrAccessKey;
    }

    public String getIvrToken() {
        return ivrToken;
    }

    public void setIvrToken(String ivrToken) {
        this.ivrToken = ivrToken;
    }

    public String getMonitorEnvironment() {
        return monitorEnvironment;
    }

    public String getSaasMonitorEnvironment() {
        return "saas-" + monitorEnvironment;
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

    public String getMonitorAlarmExcludeEnvironment() {
        return monitorAlarmExcludeEnvironment;
    }

    public void setMonitorAlarmExcludeEnvironment(String monitorAlarmExcludeEnvironment) {
        this.monitorAlarmExcludeEnvironment = monitorAlarmExcludeEnvironment;
    }

    public Double getMonitorAlarmThresholdMax() {
        return monitorAlarmThresholdMax;
    }

    public void setMonitorAlarmThresholdMax(Double monitorAlarmThresholdMax) {
        this.monitorAlarmThresholdMax = monitorAlarmThresholdMax;
    }

    public String getMonitorAlarmExcludeAppIdsAll() {
        return monitorAlarmExcludeAppIdsAll;
    }

    public void setMonitorAlarmExcludeAppIdsAll(String monitorAlarmExcludeAppIdsAll) {
        this.monitorAlarmExcludeAppIdsAll = monitorAlarmExcludeAppIdsAll;
    }

    public Integer getOperatorMonitorIntervalMinutes() {
        return operatorMonitorIntervalMinutes;
    }

    public void setOperatorMonitorIntervalMinutes(Integer operatorMonitorIntervalMinutes) {
        this.operatorMonitorIntervalMinutes = operatorMonitorIntervalMinutes;
    }

    public String getOperatorAlarmOperatorNameList() {
        return operatorAlarmOperatorNameList;
    }

    public void setOperatorAlarmOperatorNameList(String operatorAlarmOperatorNameList) {
        this.operatorAlarmOperatorNameList = operatorAlarmOperatorNameList;
    }

    public Integer getOperatorAlarmFewNum() {
        return operatorAlarmFewNum;
    }

    public void setOperatorAlarmFewNum(Integer operatorAlarmFewNum) {
        this.operatorAlarmFewNum = operatorAlarmFewNum;
    }

    public Integer getOperatorAlarmFewThresholdPercent() {
        return operatorAlarmFewThresholdPercent;
    }

    public void setOperatorAlarmFewThresholdPercent(Integer operatorAlarmFewThresholdPercent) {
        this.operatorAlarmFewThresholdPercent = operatorAlarmFewThresholdPercent;
    }

    public String getTaskSuccessRateAlarmConfig() {
        return taskSuccessRateAlarmConfig;
    }

    public void setTaskSuccessRateAlarmConfig(String taskSuccessRateAlarmConfig) {
        this.taskSuccessRateAlarmConfig = taskSuccessRateAlarmConfig;
    }

    public String getOperatorMonitorAlarmConfig() {
        return operatorMonitorAlarmConfig;
    }

    public void setOperatorMonitorAlarmConfig(String operatorMonitorAlarmConfig) {
        this.operatorMonitorAlarmConfig = operatorMonitorAlarmConfig;
    }

    public String getMonitorAlarmMobiles() {
        return monitorAlarmMobiles;
    }

    public void setMonitorAlarmMobiles(String monitorAlarmMobiles) {
        this.monitorAlarmMobiles = monitorAlarmMobiles;
    }

    public String getEcommerceMonitorAlarmConfig() {
        return ecommerceMonitorAlarmConfig;
    }

    public void setEcommerceMonitorAlarmConfig(String ecommerceMonitorAlarmConfig) {
        this.ecommerceMonitorAlarmConfig = ecommerceMonitorAlarmConfig;
    }

    public Integer getErrorLower() {
        return errorLower;
    }

    public void setErrorLower(Integer errorLower) {
        this.errorLower = errorLower;
    }

    public Integer getWarningLower() {
        return warningLower;
    }

    public void setWarningLower(Integer warningLower) {
        this.warningLower = warningLower;
    }

    public String getSupportEmails() {
        return supportEmails;
    }

    public void setSupportEmails(String supportEmails) {
        this.supportEmails = supportEmails;
    }

    public String getTaskExistAlarmNoSuccessTaskConfig() {
        return taskExistAlarmNoSuccessTaskConfig;
    }

    public void setTaskExistAlarmNoSuccessTaskConfig(String taskExistAlarmNoSuccessTaskConfig) {
        this.taskExistAlarmNoSuccessTaskConfig = taskExistAlarmNoSuccessTaskConfig;
    }

    public String getTaskExistAlarmNoTaskConfig() {
        return taskExistAlarmNoTaskConfig;
    }

    public void setTaskExistAlarmNoTaskConfig(String taskExistAlarmNoTaskConfig) {
        this.taskExistAlarmNoTaskConfig = taskExistAlarmNoTaskConfig;
    }

    public String getDutyIvrSwitch() {
        return dutyIvrSwitch;
    }

    public void setDutyIvrSwitch(String dutyIvrSwitch) {
        this.dutyIvrSwitch = dutyIvrSwitch;
    }


    public String getConsoleAddress() {
        return consoleAddress;
    }

    public void setConsoleAddress(String consoleAddress) {
        this.consoleAddress = consoleAddress;
    }


    public Integer getAllErrorLower() {
        return allErrorLower;
    }

    public void setAllErrorLower(Integer allErrorLower) {
        this.allErrorLower = allErrorLower;
    }

    public Integer getAllErrorWarning() {
        return allErrorWarning;
    }

    public void setAllErrorWarning(Integer allErrorWarning) {
        this.allErrorWarning = allErrorWarning;
    }

    public String getMonitorAlarmSmsSwitch() {
        return monitorAlarmSmsSwitch;
    }

    public void setMonitorAlarmSmsSwitch(String monitorAlarmSmsSwitch) {
        this.monitorAlarmSmsSwitch = monitorAlarmSmsSwitch;
    }

    public String getOldAlarmAllSwitch() {
        return oldAlarmAllSwitch;
    }

    public void setOldAlarmAllSwitch(String oldAlarmAllSwitch) {
        this.oldAlarmAllSwitch = oldAlarmAllSwitch;
    }

    public Boolean isOldAlarmAllSwitchOn() {
        if (StringUtils.equals(this.oldAlarmAllSwitch, "on")) {
            return true;
        }
        logger.info("原预警总开关已关闭...");
        return false;
    }
}
