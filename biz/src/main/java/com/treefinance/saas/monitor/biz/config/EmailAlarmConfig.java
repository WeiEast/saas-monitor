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
 * 邮箱预警的配置类
 * @Author: chengtong
 * @Date: 18/3/9 18:52
 */
@Component
@Scope
@DResource
public class EmailAlarmConfig {

    private static final Logger logger = LoggerFactory.getLogger(OperatorMonitorConfig.class);

    @BeforeUpdate
    public void before(String key, Object newValue) {
        logger.info(key + " update to " + newValue + " start...");
    }

    @AfterUpdate
    public void after(String key, Object newValue) {
        logger.info(key + " update to " + newValue + " end...");
    }


    @DAttribute(key = "email.monitor.alarm.config")
    private String emailAlarmConfig;

    /**
     * 预警等级 info的上限
     * */
    @DAttribute(key = "email.monitor.alarm.all.info")
    private Integer allInfo;

    /**
     * 预警等级 warning的上限
     * */
    @DAttribute(key = "email.monitor.alarm.all.warning")
    private Integer allWarnning;

    /**
     * 预警等级 info的上限
     * */
    @DAttribute(key = "email.monitor.alarm.group.info")
    private Integer groupInfo;

    /**
     * 预警等级 warning的上限
     * */
    @DAttribute(key = "email.monitor.alarm.group.warning")
    private Integer groupWarning;



    public Integer getAllInfo() {
        return allInfo;
    }

    public void setAllInfo(Integer allInfo) {
        this.allInfo = allInfo;
    }

    public Integer getAllWarnning() {
        return allWarnning;
    }

    public void setAllWarnning(Integer allWarnning) {
        this.allWarnning = allWarnning;
    }

    public Integer getGroupInfo() {
        return groupInfo;
    }

    public void setGroupInfo(Integer groupInfo) {
        this.groupInfo = groupInfo;
    }

    public Integer getGroupWarning() {
        return groupWarning;
    }

    public void setGroupWarning(Integer groupWarning) {
        this.groupWarning = groupWarning;
    }

    public String getEmailAlarmConfig() {
        return emailAlarmConfig;
    }

    public void setEmailAlarmConfig(String emailAlarmConfig) {
        this.emailAlarmConfig = emailAlarmConfig;
    }







}
