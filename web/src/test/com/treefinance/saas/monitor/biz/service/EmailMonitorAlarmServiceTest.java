package com.treefinance.saas.monitor.biz.service;

import com.alibaba.fastjson.JSONObject;
import com.treefinance.saas.monitor.app.SaasMonitorApplication;
import com.treefinance.saas.monitor.biz.service.impl.EmailAlarmTemplateImpl;
import com.treefinance.saas.monitor.common.constants.AlarmConstants;
import com.treefinance.saas.monitor.common.domain.dto.EmailMonitorAlarmConfigDTO;
import com.treefinance.saas.monitor.common.domain.dto.alarmconfig.EmailMonitorAlarmConfigDTO;
import com.treefinance.saas.monitor.common.enumeration.ETaskStatDataType;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author chengtong
 * @date 18/3/13 17:08
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SaasMonitorApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class EmailMonitorAlarmServiceTest {

    @Autowired
    private EmailAlarmTemplateImpl emailAlarmTemplateImpl;

    @Test
    public void alarm() throws InterruptedException, ParseException {
        String dateStr = "2018-03-27 21:10:00";
        Date jobTime = DateUtils.parseDate(dateStr, Locale.CHINA, "yyyy-MM-dd hh:mm:ss");
        String configStr ="[{\"alarmSwitch\":\"off\",\"alarmType\":1,\"alarmTypeDesc\":\"所有邮箱所有商户按人数统计预警\"," +
                "\"appId\":\"virtual_total_stat_appId\",\"appName\":\"所有商户\",\"emails\":[\"virtual_total_stat_email\"],\"fewNum\":5,\"intervalMins\":30,\"levelConfig\":[{\"channels\":[\"email\",\"ivr\",\"wechat\"],\"level\":\"error\"},{\"channels\":[\"email\",\"sms\",\"wechat\"],\"level\":\"warning\"},{\"channels\":[\"email\",\"wechat\"],\"level\":\"info\"}],\"list\":[{\"callbackSuccessRate\":70,\"crawlSuccessRate\":70,\"endTime\":\"23:59:59\",\"inTime\":false,\"loginConversionRate\":70,\"loginSuccessRate\":70,\"processSuccessRate\":70,\"startTime\":\"18:00:00\",\"wholeConversionRate\":70},{\"callbackSuccessRate\":70,\"crawlSuccessRate\":70,\"endTime\":\"06:00:00\",\"inTime\":false,\"loginConversionRate\":70,\"loginSuccessRate\":70,\"processSuccessRate\":70,\"startTime\":\"00:00:00\",\"wholeConversionRate\":70},{\"callbackSuccessRate\":90,\"crawlSuccessRate\":90,\"endTime\":\"18:00:00\",\"inTime\":true,\"loginConversionRate\":90,\"loginSuccessRate\":90,\"processSuccessRate\":90,\"startTime\":\"06:00:00\",\"wholeConversionRate\":90}],\"previousDays\":7,\"switches\":{\"ivr\":\"on\",\"sms\":\"on\",\"wechat\":\"on\",\"email\":\"on\"},\"taskTimeoutSecs\":600,\"threshold\":20},{\"alarmSwitch\":\"on\",\"alarmType\":1,\"alarmTypeDesc\":\"分邮箱所有商户按人数统计预警\",\"appId\":\"virtual_total_stat_appId\",\"appName\":\"所有商户\",\"emails\":[\"126.com\",\"163.com\",\"139.com\",\"exmail.qq.com\",\"qq.com\",\"sina.com\",\"其他\"],\"fewNum\":5,\"intervalMins\":30,\"levelConfig\":[{\"channels\":[\"email\",\"ivr\",\"wechat\"],\"level\":\"error\"},{\"channels\":[\"email\",\"sms\",\"wechat\"],\"level\":\"warning\"},{\"channels\":[\"email\",\"wechat\"],\"level\":\"info\"}],\"list\":[{\"callbackSuccessRate\":70,\"crawlSuccessRate\":70,\"endTime\":\"23:59:59\",\"inTime\":false,\"loginConversionRate\":70,\"loginSuccessRate\":70,\"processSuccessRate\":70,\"startTime\":\"18:00:00\",\"wholeConversionRate\":70},{\"callbackSuccessRate\":70,\"crawlSuccessRate\":70,\"endTime\":\"06:00:00\",\"inTime\":false,\"loginConversionRate\":70,\"loginSuccessRate\":70,\"processSuccessRate\":70,\"startTime\":\"00:00:00\",\"wholeConversionRate\":70},{\"callbackSuccessRate\":90,\"crawlSuccessRate\":90,\"endTime\":\"18:00:00\",\"inTime\":true,\"loginConversionRate\":90,\"loginSuccessRate\":90,\"processSuccessRate\":90,\"startTime\":\"06:00:00\",\"wholeConversionRate\":90}],\"previousDays\":7,\"switches\":{\"ivr\":\"on\",\"sms\":\"on\",\"wechat\":\"on\",\"email\":\"on\"},\"taskTimeoutSecs\":600,\"threshold\":20}]\n";
        String emails = "126.com,163.com,139.com,exmail.qq.com,qq.com,sina.com,其他";
        List<EmailMonitorAlarmConfigDTO> configList = JSONObject.parseArray(configStr,
                EmailMonitorAlarmConfigDTO.class);

        for (EmailMonitorAlarmConfigDTO configDTO : configList) {
            if (!StringUtils.equalsIgnoreCase(configDTO.getAlarmSwitch(), AlarmConstants.SWITCH_ON)) {
                continue;
            }
            if (configDTO.getAlarmType() == 1) {
                //总运营商按人统计的预警
                emailAlarmTemplateImpl.alarm(jobTime, configDTO, ETaskStatDataType.USER, emails.split(","));
            }
        }

    }

}