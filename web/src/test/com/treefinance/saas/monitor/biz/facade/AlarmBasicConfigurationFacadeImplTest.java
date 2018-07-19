package com.treefinance.saas.monitor.biz.facade;

import com.treefinance.saas.monitor.app.SaasMonitorApplication;
import com.treefinance.saas.monitor.facade.domain.request.autoalarm.AlarmBasicConfigurationRequest;
import com.treefinance.saas.monitor.facade.service.autoalarm.AlarmBasicConfigurationFacade;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author chengtong
 * @date 18/7/19 14:37
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SaasMonitorApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class AlarmBasicConfigurationFacadeImplTest {

    @Autowired
    AlarmBasicConfigurationFacade facade;

    @Test
    public void add() {
    }

    @Test
    public void update() {
    }

    @Test
    public void queryAlaramExecuteLogByAlarmId() {
    }

    @Test
    public void queryAlarmConfigurationList() {
        AlarmBasicConfigurationRequest request = new AlarmBasicConfigurationRequest();
        request.setName("");
        request.setRunEnv((byte)0);
        facade.queryAlarmConfigurationList(request);
    }
}