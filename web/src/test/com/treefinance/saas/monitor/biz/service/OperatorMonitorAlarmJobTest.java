package com.treefinance.saas.monitor.biz.service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.treefinance.saas.monitor.app.SaasMonitorApplication;
import com.treefinance.saas.monitor.biz.config.DiamondConfig;
import com.treefinance.saas.monitor.common.domain.dto.OperatorMonitorAlarmConfigDTO;
import com.treefinance.saas.monitor.common.domain.dto.TaskExistAlarmNoSuccessTaskConfigDTO;
import com.treefinance.saas.monitor.common.domain.dto.TaskExistAlarmNoTaskConfigDTO;
import com.treefinance.saas.monitor.common.utils.MonitorDateUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;

/**
 * Buddha Bless , No Bug !
 *
 * @author haojiahong
 * @date 2018/4/11
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SaasMonitorApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class OperatorMonitorAlarmJobTest {

    @Autowired
    private DiamondConfig diamondConfig;
    @Autowired
    private OperatorMonitorGroupAlarmService operatorMonitorGroupAlarmService;
    @Autowired
    private TaskExistMonitorAlarmService taskExistMonitorAlarmService;

    @Test
    public void testAlarmJobTest() throws InterruptedException {
        List<Date> jobTimeList = Lists.newArrayList(MonitorDateUtils.parse("2016-04-25 23:55:00"),
                MonitorDateUtils.parse("2016-04-26 00:00:00"),
                MonitorDateUtils.parse("2016-04-26 00:05:00"),
                MonitorDateUtils.parse("2016-04-26 00:10:00"),
                MonitorDateUtils.parse("2016-04-26 00:15:00"),
                MonitorDateUtils.parse("2016-04-26 00:20:00"),
                MonitorDateUtils.parse("2016-04-26 00:30:00"),
                MonitorDateUtils.parse("2016-04-26 00:40:00"),
                MonitorDateUtils.parse("2016-04-26 00:50:00"));
        String configStr = diamondConfig.getOperatorMonitorAlarmConfig();
        for (Date jobTime : jobTimeList) {
            List<OperatorMonitorAlarmConfigDTO> configList = JSONObject.parseArray(configStr, OperatorMonitorAlarmConfigDTO.class);
            for (OperatorMonitorAlarmConfigDTO configDTO : configList) {
                operatorMonitorGroupAlarmService.alarm(jobTime, configDTO);
                Thread.sleep(2 * 1000);
            }
        }
        System.out.println("done====");
    }

    @Test
    public void testAlarmJob() {
        Date jobTime = MonitorDateUtils.parse("2018-05-07 15:15:00");
        List<OperatorMonitorAlarmConfigDTO> configList = JSONObject.parseArray(diamondConfig.getOperatorMonitorAlarmConfig(), OperatorMonitorAlarmConfigDTO.class);
        for (OperatorMonitorAlarmConfigDTO configDTO : configList) {
            operatorMonitorGroupAlarmService.alarm(jobTime, configDTO);
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
