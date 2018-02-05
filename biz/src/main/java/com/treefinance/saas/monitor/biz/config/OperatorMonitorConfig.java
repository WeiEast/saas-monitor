package com.treefinance.saas.monitor.biz.config;

import com.github.diamond.client.extend.annotation.DAttribute;
import com.github.diamond.client.extend.annotation.DResource;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 *  运营商监控的配置类
 *  后续的希望将对运营商的配置能够单独的移动到这个类中
 *
 * @Author: chengtong
 * @Date: 18/2/5 19:21
 */
@Configuration
@Scope
@DResource
public class OperatorMonitorConfig {

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
}
