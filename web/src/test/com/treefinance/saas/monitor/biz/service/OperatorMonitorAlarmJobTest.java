package com.treefinance.saas.monitor.biz.service;

import com.treefinance.saas.monitor.app.SaasMonitorApplication;
import com.treefinance.saas.monitor.biz.config.DiamondConfig;
import com.treefinance.saas.monitor.biz.task.OperatorMonitorAlarmJob;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
    private OperatorMonitorAlarmJob operatorMonitorAlarmJob;

    @Test
    public void testAlarmJob() {
        operatorMonitorAlarmJob.execute(null);
    }

}
