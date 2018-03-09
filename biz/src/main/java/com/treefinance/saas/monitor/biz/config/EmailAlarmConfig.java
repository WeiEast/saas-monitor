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

    public String getEmailAlarmConfig() {
        return emailAlarmConfig;
    }

    public void setEmailAlarmConfig(String emailAlarmConfig) {
        this.emailAlarmConfig = emailAlarmConfig;
    }
}
