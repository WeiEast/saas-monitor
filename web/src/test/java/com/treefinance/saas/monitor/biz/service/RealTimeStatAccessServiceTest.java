package com.treefinance.saas.monitor.biz.service;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.monitor.app.SaasMonitorApplication;
import com.treefinance.saas.monitor.common.domain.dto.RealTimeStatAccessDTO;
import com.treefinance.saas.monitor.common.utils.MonitorDateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;

/**
 * Good Luck Bro , No Bug !
 *
 * @author haojiahong
 * @date 2018/6/27
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SaasMonitorApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class RealTimeStatAccessServiceTest {
    @Autowired
    private RealTimeAvgStatAccessService realTimeAvgStatAccessService;


    @Test
    public void test_queryDataByConditions() {
        String startTimeStr = "2018-06-27 16:00:00";
        Date startTime = MonitorDateUtils.parse(startTimeStr);
        String endTimeStr = "2018-06-27 17:00:00";
        Date endTime = MonitorDateUtils.parse(endTimeStr);
        List<RealTimeStatAccessDTO> list = realTimeAvgStatAccessService.queryDataByConditions("virtual_total_stat_appId", (byte) 0, (byte) 3, startTime, endTime, 10, (byte) 1);
        System.out.println("result--" + JSON.toJSONString(list));
    }

    @Test
    public void test_saveDataOnFixedTime() {
        realTimeAvgStatAccessService.saveDataOnFixedTime();
    }

}
