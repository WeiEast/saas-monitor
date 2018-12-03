package com.treefinance.saas.monitor.biz.facade;

import com.treefinance.saas.monitor.app.SaasMonitorApplication;
import com.treefinance.saas.monitor.dao.entity.AlarmRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author chengtong
 * @date 18/6/11 19:35
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SaasMonitorApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class AlarmRecordFacadeImplTest {

    @Autowired
    AlarmRecordFacadeImpl alarmRecordFacade;


    @Test
    public void getUnProcessedAndSameTypeRecords() {
        AlarmRecord alarmRecord = new AlarmRecord();

        alarmRecord.setSummary("operator_alarm:warning:4:1:CHINA_10010:AS_PS");
        alarmRecord.setContent("190906684108320768");

        alarmRecordFacade.getUnProcessedAndSameTypeRecords(alarmRecord);
    }

}