package com.treefinance.saas.monitor.facade.service.stat;

import com.treefinance.saas.monitor.app.SaasMonitorApplication;
import com.treefinance.saas.monitor.common.enumeration.EBizType;
import com.treefinance.saas.monitor.common.enumeration.ESaasEnv;
import com.treefinance.saas.monitor.facade.domain.request.DashboardStatRequest;
import com.treefinance.saas.monitor.facade.service.BaseTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author chengtong
 * @date 18/9/13 10:21
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SaasMonitorApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class DashBoardFacadeTest extends BaseTest{

    @Autowired
    private DashBoardFacade dashBoardFacade;

    @Test
    public void queryDashboardResult() {
        DashboardStatRequest request = new DashboardStatRequest();

        request.setBizType(EBizType.ECOMMERCE.getCode());
        request.setSaasEnv((byte)ESaasEnv.ALL.getValue());


        result = dashBoardFacade.queryDashboardResult(request);

    }
}