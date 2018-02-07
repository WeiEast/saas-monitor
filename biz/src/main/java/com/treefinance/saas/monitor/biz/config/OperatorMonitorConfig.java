package com.treefinance.saas.monitor.biz.config;

import com.github.diamond.client.extend.annotation.AfterUpdate;
import com.github.diamond.client.extend.annotation.BeforeUpdate;
import com.github.diamond.client.extend.annotation.DAttribute;
import com.github.diamond.client.extend.annotation.DResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *  运营商监控的配置类
 *  后续的希望将对运营商的配置能够单独的移动到这个类中
 *
 * @Author: chengtong
 * @Date: 18/2/5 19:21
 */
@Component
@Scope
@DResource
public class OperatorMonitorConfig {

    private static final Logger logger = LoggerFactory.getLogger(IvrConfig.class);

    /**
     * 新增的 对运营商监控的ivr开关
     * */
    @DAttribute(key = "saas-monitor.operator.ivr.switch")
    private String ivrSwitch;

    /**
     * 新增的 对运营商监控的sms开关
     * */
    @DAttribute(key = "saas-monitor.operator.sms.switch")
    private String smsSwitch;

    /**
     * 大盘预警的短信模板
     * */
    @DAttribute(key = "saas-monitor.operator.sms.all.template")
    private String smsAllTemplate;

    /**
    * 分组预警的短信模板
    * */
    @DAttribute(key = "saas-monitor.operator.sms.group.template")
    private String smsGroupTemplate;

    public String getSmsAllTemplate() {
        return smsAllTemplate;
    }

    public void setSmsAllTemplate(String smsAllTemplate) {
        this.smsAllTemplate = smsAllTemplate;
    }

    public String getSmsGroupTemplate() {
        return smsGroupTemplate;
    }

    public void setSmsGroupTemplate(String smsGroupTemplate) {
        this.smsGroupTemplate = smsGroupTemplate;
    }

    public String getIvrSwitch() {
        return ivrSwitch;
    }

    public void setIvrSwitch(String ivrSwitch) {
        this.ivrSwitch = ivrSwitch;
    }

    public String getSmsSwitch() {
        return smsSwitch;
    }

    public void setSmsSwitch(String smsSwitch) {
        this.smsSwitch = smsSwitch;
    }



    @BeforeUpdate
    public void before(String key, Object newValue) {
        logger.info(key + " update to " + newValue + " start...");
    }

    @AfterUpdate
    public void after(String key, Object newValue) {
        logger.info(key + " update to " + newValue + " end...");
    }
}
