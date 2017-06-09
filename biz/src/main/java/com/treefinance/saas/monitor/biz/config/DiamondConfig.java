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
}
