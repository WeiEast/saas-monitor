package com.treefinance.saas.monitor.biz.config;

import com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSON;
import com.github.diamond.client.extend.annotation.AfterUpdate;
import com.github.diamond.client.extend.annotation.BeforeUpdate;
import com.github.diamond.client.extend.annotation.DAttribute;
import com.github.diamond.client.extend.annotation.DResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by yh-treefinance on 2017/12/6.
 */
@Component
@Scope
@DResource
public class IvrConfig {
    private static final Logger logger = LoggerFactory.getLogger(IvrConfig.class);


    @DAttribute(key = "ivr.contacts")
    private String contacts;

    @DAttribute(key = "ivr.base.config")
    private String ivrBaseConfig;

    @DAttribute(key = "ivr.alarm.type.cron")
    private String alarmTypeCron;

    @DAttribute(key = "monitor.environment")
    private String environment;

    @DAttribute(key = "ivr.replay.url")
    private String ivrReplayurl;

    @DAttribute(key = "auth.ivr.accessKey")
    private String accessKey;
    @DAttribute(key = "auth.ivr.token")
    private String token;

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getIvrReplayurl() {
        return ivrReplayurl;
    }

    public void setIvrReplayurl(String ivrreplayurl) {
        this.ivrReplayurl = ivrreplayurl;
    }

    @BeforeUpdate
    public void before(String key, Object newValue) {
        logger.info(key + " update to " + newValue + " start...");
    }

    @AfterUpdate
    public void after(String key, Object newValue) {
        logger.info(key + " update to " + newValue + " end...");
    }

    public String getIvrUrl() {
        return JSON.parseObject(ivrBaseConfig).get("url").toString();
    }


    public String getIvrToken() {
        return JSON.parseObject(ivrBaseConfig).get("token").toString();
    }


    public String getIvrKey() {
        return JSON.parseObject(ivrBaseConfig).get("key").toString();
    }


    public Integer getModelId() {
        return Integer.valueOf(JSON.parseObject(ivrBaseConfig).get("module").toString());
    }


    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public Integer getIvrCount() {
        return Integer.valueOf(JSON.parseObject(ivrBaseConfig).get("count").toString());
    }

    public String getIvrSwitch() {
        return JSON.parseObject(ivrBaseConfig).get("switch").toString();
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getIvrBaseConfig() {
        return ivrBaseConfig;
    }

    public void setIvrBaseConfig(String ivrBaseConfig) {
        this.ivrBaseConfig = ivrBaseConfig;
    }

    public String getAlarmTypeCron() {
        return alarmTypeCron;
    }

    public void setAlarmTypeCron(String alarmTypeCron) {
        this.alarmTypeCron = alarmTypeCron;
    }
}
