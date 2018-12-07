package com.treefinance.saas.monitor.facade.service.stat;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.monitor.app.SaasMonitorApplication;
import com.treefinance.saas.monitor.util.MonitorDateUtils;
import com.treefinance.saas.monitor.facade.domain.request.EcommerceDetailAccessRequest;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;
import com.treefinance.saas.monitor.facade.domain.ro.stat.ecommerce.EcommerceAllDetailRO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;

/**
 * @author haojiahong
 * @date 2018/7/13
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SaasMonitorApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class EcommerceStatAccessFacadeTest {
    @Autowired
    EcommerceStatDivisionAccessFacade ecommerceStatDivisionAccessFacade;


    @Test
    public void test_queryEcommerceAllDetailAccessList() {
        EcommerceDetailAccessRequest request = new EcommerceDetailAccessRequest();
        request.setAppId("test_oPYvTsBGGKgVBBTe");
        request.setSaasEnv((byte) 0);
        request.setSourceType((byte) 0);
        request.setStatType((byte) 0);
        request.setDataDate(MonitorDateUtils.getDayStartTime(new Date()));

        MonitorResult<List<EcommerceAllDetailRO>> result = ecommerceStatDivisionAccessFacade.queryEcommerceAllDetailAccessList(request);
        System.out.println(JSON.toJSONString(result));
    }

}
