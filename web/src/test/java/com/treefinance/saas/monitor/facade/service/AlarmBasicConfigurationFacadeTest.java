package com.treefinance.saas.monitor.facade.service;

import com.treefinance.saas.monitor.app.SaasMonitorApplication;
import com.treefinance.saas.monitor.app.SaasMonitorApplication;
import com.treefinance.saas.monitor.common.enumeration.EAlarmType;
import com.treefinance.saas.monitor.common.enumeration.EOrderStatus;
import com.treefinance.saas.monitor.dao.entity.SaasWorker;
import com.treefinance.saas.monitor.facade.domain.request.*;
import com.treefinance.saas.monitor.facade.domain.ro.SaasWorkerRO;
import com.treefinance.saas.monitor.facade.service.autoalarm.AlarmBasicConfigurationFacade;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;

/**
 * @author:guoguoyun
 * @date:Created in 2018/7/24下午2:39
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SaasMonitorApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class AlarmBasicConfigurationFacadeTest {

    @Autowired
    AlarmBasicConfigurationFacade alarmBasicConfigurationFacade;

    @Test
    public void queryWorkerNameByDate(){
        List<SaasWorkerRO> list = alarmBasicConfigurationFacade.queryWorkerNameByDate(new Date()).getData();
        for(SaasWorkerRO saasWorkerRO:list)
        {
            System.out.println("测试的通知人员为"+saasWorkerRO.getName());

        }

    }
}
