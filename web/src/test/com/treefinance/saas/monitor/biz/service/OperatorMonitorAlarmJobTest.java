package com.treefinance.saas.monitor.biz.service;

import com.alibaba.fastjson.JSONObject;
import com.treefinance.saas.monitor.app.SaasMonitorApplication;
import com.treefinance.saas.monitor.biz.config.DiamondConfig;
import com.treefinance.saas.monitor.common.domain.dto.TaskExistAlarmNoSuccessTaskConfigDTO;
import com.treefinance.saas.monitor.common.domain.dto.TaskExistAlarmNoTaskConfigDTO;
import com.treefinance.saas.monitor.common.domain.dto.alarmconfig.OperatorMonitorAlarmConfigDTO;
import com.treefinance.saas.monitor.common.enumeration.ETaskStatDataType;
import com.treefinance.saas.monitor.common.utils.MonitorDateUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Buddha Bless , No Bug !
 *
 * @author haojiahong
 * @date 2018/4/11
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SaasMonitorApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class OperatorMonitorAlarmJobTest {

    private static final Logger logger = LoggerFactory.getLogger(OperatorMonitorAlarmJobTest.class);

    @Autowired
    private DiamondConfig diamondConfig;
    @Autowired
    @Qualifier("operatorAlarmMonitorService")
    private MonitorAlarmService operatorAlarmMonitorService;
    @Autowired
    private TaskExistMonitorAlarmService taskExistMonitorAlarmService;

    @Test
    public void testAlarmJobTest() throws InterruptedException, ParseException {
        String configStr = "[{\"alarmSwitch\":\"on\",\"alarmType\":1,\"alarmTypeDesc\":\"总运营商\",\"appId\":\"virtual_total_stat_appId\",\"appName\":\"所有商户\",\"dataType\":1,\"dataTypeDesc\":\"按人数\",\"inTimeTimeConfig\":{\"callbackSuccessRate\":70,\"confirmMobileConversionRate\":70,\"crawlSuccessRate\":70,\"endTime\":\"19:00:00\",\"inTime\":true,\"loginConversionRate\":70,\"loginSuccessRate\":70,\"processSuccessRate\":70,\"startTime\":\"06:00:00\",\"switches\":{\"sms\":\"on\",\"wechat\":\"on\",\"ivr\":\"on\",\"email\":\"on\"},\"wholeConversionRate\":90},\"intervalMins\":30,\"levelConfig\":[{\"channels\":[\"ivr\",\"email\",\"wechat\"],\"level\":\"error\"},{\"channels\":[\"sms\",\"email\",\"wechat\"],\"level\":\"warning\"},{\"channels\":[\"email\",\"wechat\"],\"level\":\"info\"}],\"previousDays\":7,\"saasEnv\":0,\"saasEnvDesc\":\"所有环境\",\"taskTimeoutSecs\":600,\"timeConfig\":[{\"callbackSuccessRate\":70,\"confirmMobileConversionRate\":70,\"crawlSuccessRate\":70,\"endTime\":\"06:00:00\",\"inTime\":false,\"loginConversionRate\":70,\"loginSuccessRate\":70,\"processSuccessRate\":70,\"startTime\":\"00:00:00\",\"switches\":{\"sms\":\"on\",\"wechat\":\"on\",\"ivr\":\"on\",\"email\":\"on\"},\"wholeConversionRate\":90},{\"callbackSuccessRate\":70,\"confirmMobileConversionRate\":70,\"crawlSuccessRate\":70,\"endTime\":\"19:00:00\",\"inTime\":true,\"loginConversionRate\":70,\"loginSuccessRate\":70,\"processSuccessRate\":70,\"startTime\":\"06:00:00\",\"switches\":{\"sms\":\"on\",\"wechat\":\"on\",\"ivr\":\"on\",\"email\":\"on\"},\"wholeConversionRate\":90},{\"callbackSuccessRate\":70,\"confirmMobileConversionRate\":70,\"crawlSuccessRate\":70,\"endTime\":\"23:59:59\",\"inTime\":false,\"loginConversionRate\":70,\"loginSuccessRate\":70,\"processSuccessRate\":70,\"startTime\":\"19:00:00\",\"switches\":{\"sms\":\"on\",\"wechat\":\"on\",\"ivr\":\"on\",\"email\":\"on\"},\"wholeConversionRate\":90}]},{\"alarmSwitch\":\"on\",\"alarmType\":1,\"alarmTypeDesc\":\"总运营商\",\"appId\":\"virtual_total_stat_appId\",\"appName\":\"所有商户\",\"dataType\":1,\"dataTypeDesc\":\"按人数\",\"inTimeTimeConfig\":{\"callbackSuccessRate\":70,\"confirmMobileConversionRate\":70,\"crawlSuccessRate\":70,\"endTime\":\"19:00:00\",\"inTime\":true,\"loginConversionRate\":70,\"loginSuccessRate\":70,\"processSuccessRate\":70,\"startTime\":\"06:00:00\",\"switches\":{\"sms\":\"on\",\"wechat\":\"on\",\"ivr\":\"on\",\"email\":\"on\"},\"wholeConversionRate\":90},\"intervalMins\":30,\"levelConfig\":[{\"channels\":[\"ivr\",\"email\",\"wechat\"],\"level\":\"error\"},{\"channels\":[\"sms\",\"email\",\"wechat\"],\"level\":\"warning\"},{\"channels\":[\"email\",\"wechat\"],\"level\":\"info\"}],\"previousDays\":7,\"saasEnv\":1,\"saasEnvDesc\":\"生产环境\",\"taskTimeoutSecs\":600,\"timeConfig\":[{\"callbackSuccessRate\":70,\"confirmMobileConversionRate\":70,\"crawlSuccessRate\":70,\"endTime\":\"06:00:00\",\"inTime\":false,\"loginConversionRate\":70,\"loginSuccessRate\":70,\"processSuccessRate\":70,\"startTime\":\"00:00:00\",\"switches\":{\"sms\":\"on\",\"wechat\":\"on\",\"ivr\":\"on\",\"email\":\"on\"},\"wholeConversionRate\":90},{\"callbackSuccessRate\":70,\"confirmMobileConversionRate\":70,\"crawlSuccessRate\":70,\"endTime\":\"19:00:00\",\"inTime\":true,\"loginConversionRate\":70,\"loginSuccessRate\":70,\"processSuccessRate\":70,\"startTime\":\"06:00:00\",\"switches\":{\"sms\":\"on\",\"wechat\":\"on\",\"ivr\":\"on\",\"email\":\"on\"},\"wholeConversionRate\":90},{\"callbackSuccessRate\":70,\"confirmMobileConversionRate\":70,\"crawlSuccessRate\":70,\"endTime\":\"23:59:59\",\"inTime\":false,\"loginConversionRate\":70,\"loginSuccessRate\":70,\"processSuccessRate\":70,\"startTime\":\"19:00:00\",\"switches\":{\"sms\":\"on\",\"wechat\":\"on\",\"ivr\":\"on\",\"email\":\"on\"},\"wholeConversionRate\":90}]},{\"alarmSwitch\":\"on\",\"alarmType\":1,\"alarmTypeDesc\":\"总运营商\",\"appId\":\"virtual_total_stat_appId\",\"appName\":\"所有商户\",\"dataType\":1,\"dataTypeDesc\":\"按人数\",\"inTimeTimeConfig\":{\"callbackSuccessRate\":70,\"confirmMobileConversionRate\":70,\"crawlSuccessRate\":70,\"endTime\":\"19:00:00\",\"inTime\":true,\"loginConversionRate\":70,\"loginSuccessRate\":70,\"processSuccessRate\":70,\"startTime\":\"06:00:00\",\"switches\":{\"sms\":\"on\",\"wechat\":\"on\",\"ivr\":\"on\",\"email\":\"on\"},\"wholeConversionRate\":90},\"intervalMins\":30,\"levelConfig\":[{\"channels\":[\"ivr\",\"email\",\"wechat\"],\"level\":\"error\"},{\"channels\":[\"sms\",\"email\",\"wechat\"],\"level\":\"warning\"},{\"channels\":[\"email\",\"wechat\"],\"level\":\"info\"}],\"previousDays\":7,\"saasEnv\":2,\"saasEnvDesc\":\"预发布环境\",\"taskTimeoutSecs\":600,\"timeConfig\":[{\"callbackSuccessRate\":70,\"confirmMobileConversionRate\":70,\"crawlSuccessRate\":70,\"endTime\":\"06:00:00\",\"inTime\":false,\"loginConversionRate\":70,\"loginSuccessRate\":70,\"processSuccessRate\":70,\"startTime\":\"00:00:00\",\"switches\":{\"sms\":\"on\",\"wechat\":\"on\",\"ivr\":\"on\",\"email\":\"on\"},\"wholeConversionRate\":90},{\"callbackSuccessRate\":70,\"confirmMobileConversionRate\":70,\"crawlSuccessRate\":70,\"endTime\":\"19:00:00\",\"inTime\":true,\"loginConversionRate\":70,\"loginSuccessRate\":70,\"processSuccessRate\":70,\"startTime\":\"06:00:00\",\"switches\":{\"sms\":\"on\",\"wechat\":\"on\",\"ivr\":\"on\",\"email\":\"on\"},\"wholeConversionRate\":90},{\"callbackSuccessRate\":70,\"confirmMobileConversionRate\":70,\"crawlSuccessRate\":70,\"endTime\":\"23:59:59\",\"inTime\":false,\"loginConversionRate\":70,\"loginSuccessRate\":70,\"processSuccessRate\":70,\"startTime\":\"19:00:00\",\"switches\":{\"sms\":\"on\",\"wechat\":\"on\",\"ivr\":\"on\",\"email\":\"on\"},\"wholeConversionRate\":90}]},{\"alarmSwitch\":\"on\",\"alarmType\":2,\"alarmTypeDesc\":\"分运营商\",\"appId\":\"virtual_total_stat_appId\",\"appName\":\"所有商户\",\"dataType\":1,\"dataTypeDesc\":\"按人数\",\"inTimeTimeConfig\":{\"callbackSuccessRate\":70,\"confirmMobileConversionRate\":70,\"crawlSuccessRate\":70,\"endTime\":\"19:00:00\",\"inTime\":true,\"loginConversionRate\":70,\"loginSuccessRate\":70,\"processSuccessRate\":70,\"startTime\":\"06:00:00\",\"switches\":{\"sms\":\"on\",\"wechat\":\"on\",\"ivr\":\"on\",\"email\":\"on\"},\"wholeConversionRate\":90},\"intervalMins\":30,\"levelConfig\":[{\"channels\":[\"ivr\",\"email\",\"wechat\"],\"level\":\"error\"},{\"channels\":[\"sms\",\"email\",\"wechat\"],\"level\":\"warning\"},{\"channels\":[\"email\",\"wechat\"],\"level\":\"info\"}],\"previousDays\":7,\"saasEnv\":0,\"saasEnvDesc\":\"所有环境\",\"taskTimeoutSecs\":600,\"timeConfig\":[{\"callbackSuccessRate\":70,\"confirmMobileConversionRate\":70,\"crawlSuccessRate\":70,\"endTime\":\"06:00:00\",\"inTime\":false,\"loginConversionRate\":70,\"loginSuccessRate\":70,\"processSuccessRate\":70,\"startTime\":\"00:00:00\",\"switches\":{\"sms\":\"on\",\"wechat\":\"on\",\"ivr\":\"on\",\"email\":\"on\"},\"wholeConversionRate\":90},{\"callbackSuccessRate\":70,\"confirmMobileConversionRate\":70,\"crawlSuccessRate\":70,\"endTime\":\"19:00:00\",\"inTime\":true,\"loginConversionRate\":70,\"loginSuccessRate\":70,\"processSuccessRate\":70,\"startTime\":\"06:00:00\",\"switches\":{\"sms\":\"on\",\"wechat\":\"on\",\"ivr\":\"on\",\"email\":\"on\"},\"wholeConversionRate\":90},{\"callbackSuccessRate\":70,\"confirmMobileConversionRate\":70,\"crawlSuccessRate\":70,\"endTime\":\"23:59:59\",\"inTime\":false,\"loginConversionRate\":70,\"loginSuccessRate\":70,\"processSuccessRate\":70,\"startTime\":\"19:00:00\",\"switches\":{\"sms\":\"on\",\"wechat\":\"on\",\"ivr\":\"on\",\"email\":\"on\"},\"wholeConversionRate\":90}]},{\"alarmSwitch\":\"on\",\"alarmType\":2,\"alarmTypeDesc\":\"分运营商\",\"appId\":\"virtual_total_stat_appId\",\"appName\":\"所有商户\",\"dataType\":1,\"dataTypeDesc\":\"按人数\",\"inTimeTimeConfig\":{\"callbackSuccessRate\":70,\"confirmMobileConversionRate\":70,\"crawlSuccessRate\":70,\"endTime\":\"19:00:00\",\"inTime\":true,\"loginConversionRate\":70,\"loginSuccessRate\":70,\"processSuccessRate\":70,\"startTime\":\"06:00:00\",\"switches\":{\"sms\":\"on\",\"wechat\":\"on\",\"ivr\":\"on\",\"email\":\"on\"},\"wholeConversionRate\":90},\"intervalMins\":30,\"levelConfig\":[{\"channels\":[\"ivr\",\"email\",\"wechat\"],\"level\":\"error\"},{\"channels\":[\"sms\",\"email\",\"wechat\"],\"level\":\"warning\"},{\"channels\":[\"email\",\"wechat\"],\"level\":\"info\"}],\"previousDays\":7,\"saasEnv\":1,\"saasEnvDesc\":\"生产环境\",\"taskTimeoutSecs\":600,\"timeConfig\":[{\"callbackSuccessRate\":70,\"confirmMobileConversionRate\":70,\"crawlSuccessRate\":70,\"endTime\":\"06:00:00\",\"inTime\":false,\"loginConversionRate\":70,\"loginSuccessRate\":70,\"processSuccessRate\":70,\"startTime\":\"00:00:00\",\"switches\":{\"sms\":\"on\",\"wechat\":\"on\",\"ivr\":\"on\",\"email\":\"on\"},\"wholeConversionRate\":90},{\"callbackSuccessRate\":70,\"confirmMobileConversionRate\":70,\"crawlSuccessRate\":70,\"endTime\":\"19:00:00\",\"inTime\":true,\"loginConversionRate\":70,\"loginSuccessRate\":70,\"processSuccessRate\":70,\"startTime\":\"06:00:00\",\"switches\":{\"sms\":\"on\",\"wechat\":\"on\",\"ivr\":\"on\",\"email\":\"on\"},\"wholeConversionRate\":90},{\"callbackSuccessRate\":70,\"confirmMobileConversionRate\":70,\"crawlSuccessRate\":70,\"endTime\":\"23:59:59\",\"inTime\":false,\"loginConversionRate\":70,\"loginSuccessRate\":70,\"processSuccessRate\":70,\"startTime\":\"19:00:00\",\"switches\":{\"sms\":\"on\",\"wechat\":\"on\",\"ivr\":\"on\",\"email\":\"on\"},\"wholeConversionRate\":90}]},{\"alarmSwitch\":\"on\",\"alarmType\":2,\"alarmTypeDesc\":\"分运营商\",\"appId\":\"virtual_total_stat_appId\",\"appName\":\"所有商户\",\"dataType\":1,\"dataTypeDesc\":\"按人数\",\"inTimeTimeConfig\":{\"callbackSuccessRate\":70,\"confirmMobileConversionRate\":70,\"crawlSuccessRate\":70,\"endTime\":\"19:00:00\",\"inTime\":true,\"loginConversionRate\":70,\"loginSuccessRate\":70,\"processSuccessRate\":70,\"startTime\":\"06:00:00\",\"switches\":{\"sms\":\"on\",\"wechat\":\"on\",\"ivr\":\"on\",\"email\":\"on\"},\"wholeConversionRate\":90},\"intervalMins\":30,\"levelConfig\":[{\"channels\":[\"ivr\",\"email\",\"wechat\"],\"level\":\"error\"},{\"channels\":[\"sms\",\"email\",\"wechat\"],\"level\":\"warning\"},{\"channels\":[\"email\",\"wechat\"],\"level\":\"info\"}],\"previousDays\":7,\"saasEnv\":2,\"saasEnvDesc\":\"预发布环境\",\"taskTimeoutSecs\":600,\"timeConfig\":[{\"callbackSuccessRate\":70,\"confirmMobileConversionRate\":70,\"crawlSuccessRate\":70,\"endTime\":\"06:00:00\",\"inTime\":false,\"loginConversionRate\":70,\"loginSuccessRate\":70,\"processSuccessRate\":70,\"startTime\":\"00:00:00\",\"switches\":{\"sms\":\"on\",\"wechat\":\"on\",\"ivr\":\"on\",\"email\":\"on\"},\"wholeConversionRate\":90},{\"callbackSuccessRate\":70,\"confirmMobileConversionRate\":70,\"crawlSuccessRate\":70,\"endTime\":\"19:00:00\",\"inTime\":true,\"loginConversionRate\":70,\"loginSuccessRate\":70,\"processSuccessRate\":70,\"startTime\":\"06:00:00\",\"switches\":{\"sms\":\"on\",\"wechat\":\"on\",\"ivr\":\"on\",\"email\":\"on\"},\"wholeConversionRate\":90},{\"callbackSuccessRate\":70,\"confirmMobileConversionRate\":70,\"crawlSuccessRate\":70,\"endTime\":\"23:59:59\",\"inTime\":false,\"loginConversionRate\":70,\"loginSuccessRate\":70,\"processSuccessRate\":70,\"startTime\":\"19:00:00\",\"switches\":{\"sms\":\"on\",\"wechat\":\"on\",\"ivr\":\"on\",\"email\":\"on\"},\"wholeConversionRate\":90}]}]\n";
        String dateStr = "2018-05-17 21:10:00";
        Date jobTime = DateUtils.parseDate(dateStr, Locale.CHINA, "yyyy-MM-dd hh:mm:ss");

        List<OperatorMonitorAlarmConfigDTO> configList = JSONObject.parseArray(configStr, OperatorMonitorAlarmConfigDTO.class);
        for (OperatorMonitorAlarmConfigDTO configDTO : configList) {
            try {
                operatorAlarmMonitorService.alarm(jobTime, configDTO, ETaskStatDataType.USER);
            }catch (Exception e){
                logger.info(e.getMessage());
                continue;
            }
        }
        System.out.println("done====");
    }

    @Test
    public void testAlarmJob() {
        Date jobTime = MonitorDateUtils.parse("2018-05-17 15:15:00");
        List<OperatorMonitorAlarmConfigDTO> configList = JSONObject.parseArray(diamondConfig.getOperatorMonitorAlarmConfig(), OperatorMonitorAlarmConfigDTO.class);
        for (OperatorMonitorAlarmConfigDTO configDTO : configList) {
            operatorAlarmMonitorService.alarm(jobTime, configDTO, ETaskStatDataType.USER);
        }
    }

    @Test
    public void testNoTaskAlarmJob() {
        Date startTime = new Date();
        Date endTime = DateUtils.addMinutes(startTime, 5);
        List<TaskExistAlarmNoSuccessTaskConfigDTO> noSuccessTaskConfigList
                = JSONObject.parseArray(diamondConfig.getTaskExistAlarmNoSuccessTaskConfig(), TaskExistAlarmNoSuccessTaskConfigDTO.class);
        for (TaskExistAlarmNoSuccessTaskConfigDTO configDTO : noSuccessTaskConfigList)
            taskExistMonitorAlarmService.alarmNoSuccessTaskWithConfig(startTime, endTime, configDTO);

        List<TaskExistAlarmNoTaskConfigDTO> noTaskConfigList
                = JSONObject.parseArray(diamondConfig.getTaskExistAlarmNoTaskConfig(), TaskExistAlarmNoTaskConfigDTO.class);
        for (TaskExistAlarmNoTaskConfigDTO configDTO : noTaskConfigList) {
            taskExistMonitorAlarmService.alarmNoTaskWithConfig(startTime, endTime, configDTO);
        }
    }


}
