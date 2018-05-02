package com.treefinance.saas.monitor.biz.service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.treefinance.saas.monitor.app.SaasMonitorApplication;
import com.treefinance.saas.monitor.biz.config.DiamondConfig;
import com.treefinance.saas.monitor.common.domain.dto.OperatorMonitorAlarmConfigDTO;
import com.treefinance.saas.monitor.common.utils.MonitorDateUtils;
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

}
