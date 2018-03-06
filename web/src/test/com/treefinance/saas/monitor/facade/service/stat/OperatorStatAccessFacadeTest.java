package com.treefinance.saas.monitor.facade.service.stat;

import com.treefinance.saas.monitor.app.SaasMonitorApplication;
import com.treefinance.saas.monitor.facade.domain.request.OperatorStatAccessRequest;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.ParseException;
import java.util.Locale;

/**
 * @Author: chengtong
 * @Date: 18/3/1 16:39
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SaasMonitorApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class OperatorStatAccessFacadeTest {

    @Autowired
    OperatorStatAccessFacade operatorStatAccessFacade;

    @Test
    public void queryOperatorStatDayAccessList() {
    }

    @Test
    public void queryOperatorStatDayAccessListWithPage() {
    }

    @Test
    public void queryOperatorStatHourAccessListWithPage() {
    }

    @Test
    public void queryOneOperatorStatDayAccessListWithPage() {
    }

    @Test
    public void queryOperatorStatAccessList() {
    }

    @Test
    public void queryAllOperatorStatDayAccessList() {
    }

    @Test
    public void querySupplierAllOperatorStatDayAccessList() throws ParseException {
        OperatorStatAccessRequest request = new OperatorStatAccessRequest();

        request.setStatType(new Byte("0"));
        request.setStartDate(DateUtils.parseDate("2018-01-01 00:00:00", Locale.CHINA,"yyyy-MM-dd hh:mm:ss"));
    }

    @Test
    public void queryAllOperatorStatDayAccessListWithPage() {
    }

    @Test
    public void queryAllOperatorStaAccessList() {
    }

    @Test
    public void queryOperatorStatAccessListByExample() {
    }
}