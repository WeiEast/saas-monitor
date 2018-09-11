package com.treefinance.saas.monitor.biz.facade;

import com.treefinance.saas.monitor.biz.service.AllBizTypeStatAccessService;
import com.treefinance.saas.monitor.common.enumeration.EBizType;
import com.treefinance.saas.monitor.common.enumeration.ESaasEnv;
import com.treefinance.saas.monitor.facade.domain.request.DashboardStatRequest;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResultBuilder;
import com.treefinance.saas.monitor.facade.domain.ro.DashBoardResult;
import com.treefinance.saas.monitor.facade.domain.ro.WholeConversionResult;
import com.treefinance.saas.monitor.facade.service.stat.DashBoardFacade;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * @author chengtong
 * @date 18/9/11 16:23
 */
public class DashboardFacadeImpl implements DashBoardFacade {

    @Autowired
    AllBizTypeStatAccessService allBizTypeStatAccessService;

    @Override
    public MonitorResult<DashBoardResult> queryDashboardResult(DashboardStatRequest request) {

        ESaasEnv saasEnv = ESaasEnv.getByValue(request.getBizType());

        if(saasEnv == null){
            return MonitorResultBuilder.build("无效的环境标志");
        }

        EBizType bizType  = EBizType.getBizType(request.getBizType());
        if(bizType == null){
            return MonitorResultBuilder.build("无效的业务标志");
        }

        DashBoardResult result = new DashBoardResult();

        Date startTime = request.getStartTime();
        Date endTime = request.getEndTime();


        WholeConversionResult wholeConversionResult = allBizTypeStatAccessService.calcConversionResult(bizType);

//        AppTaskStatResult appTaskStatResult = allBizTypeStatAcces

        // TODO: 18/9/11 appTaskStatResult get method service and db include;

        // TODO: 18/9/11 assemble returnResult ;

        return null;
    }
}
