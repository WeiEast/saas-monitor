package com.treefinance.saas.monitor.biz.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.treefinance.saas.monitor.app.SaasMonitorApplication;
import com.treefinance.saas.monitor.common.domain.dto.OperatorMonitorAlarmConfigDTO;
import com.treefinance.saas.monitor.common.enumeration.ETaskStatDataType;
import com.treefinance.saas.monitor.common.utils.MonitorDateUtils;
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

import static org.junit.Assert.*;

/**
 * @Author: chengtong
 * @Date: 18/3/6 11:19
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SaasMonitorApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class OperatorMonitorAllAlarmServiceTest {

    @Autowired
    OperatorMonitorAllAlarmService operatorMonitorAllAlarmService;
    @Autowired
    OperatorMonitorGroupAlarmService operatorMonitorGroupAlarmService;

    @Test
    public void alarm() throws InterruptedException, ParseException {
        String dateStr = "2018-02-07 17:40:00";
        Date jobTime = DateUtils.parseDate(dateStr, Locale.CHINA,"yyyy-MM-dd hh:mm:ss");
        String configStr = " [{\"alarmSwitch\":\"on\",\"alarmType\":1,\"alarmTypeDesc\":\"总运营商按人统计预警\",\"appId\":\"virtual_total_stat_appId\",\"appName\":\"所有商户\",\"callbackSuccessRate\":70,\"confirmMobileConversionRate\":70,\"crawlSuccessRate\":70,\"intervalMins\":30,\"loginConversionRate\":70,\"loginSuccessRate\":70,\"mailAlarmSwitch\":\"on\",\"previousDays\":7,\"processSuccessRate\":70,\"taskTimeoutSecs\":600,\"weChatAlarmSwitch\":\"on\",\"wholeConversionRate\":90},{\"alarmSwitch\":\"on\",\"alarmType\":2,\"alarmTypeDesc\":\"分运营商按人统计预警\",\"appId\":\"virtual_total_stat_appId\",\"appName\":\"所有商户\",\"callbackSuccessRate\":70,\"confirmMobileConversionRate\":70,\"crawlSuccessRate\":70,\"intervalMins\":30,\"loginConversionRate\":70,\"loginSuccessRate\":70,\"mailAlarmSwitch\":\"on\",\"previousDays\":7,\"processSuccessRate\":70,\"taskTimeoutSecs\":600,\"weChatAlarmSwitch\":\"on\"}]";
        List<OperatorMonitorAlarmConfigDTO> configList = JSONObject.parseArray(configStr, OperatorMonitorAlarmConfigDTO.class);

        for (OperatorMonitorAlarmConfigDTO configDTO : configList) {
            if (!StringUtils.equalsIgnoreCase(configDTO.getAlarmSwitch(), "on")) {
                continue;
            }
            if (configDTO.getAlarmType() == 1) {
                //总运营商按人统计的预警
                operatorMonitorAllAlarmService.alarm(jobTime, configDTO, ETaskStatDataType.USER);
            }
            if (configDTO.getAlarmType() == 2) {
                //分运营商按人统计的预警
                operatorMonitorGroupAlarmService.alarm(jobTime, configDTO, ETaskStatDataType.USER);
            }
            if (configDTO.getAlarmType() == 3) {
                //总运营商按任务统计的预警
                operatorMonitorAllAlarmService.alarm(jobTime, configDTO, ETaskStatDataType.TASK);
            }
            if (configDTO.getAlarmType() == 4) {
                //分运营商按任务统计的预警
                operatorMonitorGroupAlarmService.alarm(jobTime, configDTO, ETaskStatDataType.TASK);
            }
        }

        Thread.sleep(10009);
    }

    public static void main(String...args) throws ParseException {
        String dateStr = "2018-02-07 17:40:00";
        Date jobTime = DateUtils.parseDate(dateStr, Locale.CHINA,"yyyy-MM-dd hh:mm:ss");
        System.err.println(jobTime);
    }

}