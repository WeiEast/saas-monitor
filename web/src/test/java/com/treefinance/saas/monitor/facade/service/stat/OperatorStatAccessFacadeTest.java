package com.treefinance.saas.monitor.facade.service.stat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.treefinance.saas.monitor.app.SaasMonitorApplication;
import com.treefinance.saas.monitor.common.enumeration.ESaasEnv;
import com.treefinance.saas.monitor.util.MonitorDateUtils;
import com.treefinance.saas.monitor.facade.domain.request.OperatorStatAccessRequest;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;
import com.treefinance.saas.monitor.facade.domain.ro.stat.operator.OperatorStatAccessRO;
import com.treefinance.saas.monitor.facade.domain.ro.stat.operator.OperatorStatDayAccessRO;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.ParseException;
import java.util.List;
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
        OperatorStatAccessRequest request = new OperatorStatAccessRequest();

        request.setStatType((byte)1);
        request.setAppId("virtual_total_stat_appId");
        request.setDataDate(MonitorDateUtils.parse("2018-06-19 00:00:00"));
        request.setSaasEnv((byte)0);

        MonitorResult<List<OperatorStatDayAccessRO>> result = operatorStatAccessFacade.queryOperatorStatDayAccessListWithPage(request);
        System.err.println(JSON.toJSONString(result, SerializerFeature.DisableCircularReferenceDetect));
    }

    @Test
    public void queryOperatorStatHourAccessListWithPage() {
        OperatorStatAccessRequest request = new OperatorStatAccessRequest();

        request.setAppId("virtual_total_stat_appId");
        request.setDataDate(MonitorDateUtils.parse("2018-06-15 15:30:00"));
        request.setSaasEnv((byte)ESaasEnv.ALL.getValue());
        request.setStatType((byte)1);
        request.setIntervalMins(30);

        request.setPageNumber((byte)1);
        request.setPageSize(20);

        MonitorResult<List<OperatorStatAccessRO>> result = operatorStatAccessFacade.queryOperatorStatHourAccessListWithPage(request);

        System.err.println(JSON.toJSONString(result, SerializerFeature.DisableCircularReferenceDetect));

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