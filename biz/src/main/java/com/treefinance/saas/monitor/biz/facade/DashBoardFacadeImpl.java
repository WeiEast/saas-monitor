package com.treefinance.saas.monitor.biz.facade;

import com.treefinance.saas.monitor.biz.service.AlarmRecordService;
import com.treefinance.saas.monitor.biz.service.AllBizTypeStatAccessService;
import com.treefinance.saas.monitor.biz.service.OperatorStatAccessService;
import com.treefinance.saas.monitor.common.enumeration.EBizType;
import com.treefinance.saas.monitor.common.enumeration.ESaasEnv;
import com.treefinance.saas.monitor.common.utils.MonitorDateUtils;
import com.treefinance.saas.monitor.facade.domain.request.DashboardStatRequest;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResultBuilder;
import com.treefinance.saas.monitor.facade.domain.ro.AppTaskStatResult;
import com.treefinance.saas.monitor.facade.domain.ro.DashBoardResult;
import com.treefinance.saas.monitor.facade.domain.ro.WholeConversionResult;
import com.treefinance.saas.monitor.facade.service.stat.DashBoardFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author chengtong
 * @date 18/9/11 16:23
 */
@Service("dashBoardFacade")
public class DashBoardFacadeImpl implements DashBoardFacade {

    @Autowired
    AllBizTypeStatAccessService allBizTypeStatAccessService;
    @Autowired
    AlarmRecordService alarmRecordService;
    @Autowired
    OperatorStatAccessService operatorStatAccessService;

    @Override
    public MonitorResult<DashBoardResult> queryDashboardResult(DashboardStatRequest request) {

        ESaasEnv saasEnv = ESaasEnv.getByValue(request.getSaasEnv());

        if(saasEnv == null){
            return MonitorResultBuilder.build("无效的环境标志");
        }

        EBizType bizType  = EBizType.getBizType(request.getBizType());
        if(bizType == null){
            return MonitorResultBuilder.build("无效的业务标志");
        }

        DashBoardResult result = new DashBoardResult();

        WholeConversionResult wholeConversionResult = allBizTypeStatAccessService.calcConversionResult(bizType, saasEnv);

        AppTaskStatResult appTaskStatResult = allBizTypeStatAccessService.getAppTaskStatResult(bizType,saasEnv);

        // TODO: 18/9/11 appTaskStatResult get method service and db include;
        List<String> list = operatorStatAccessService.queryDecreasedOperator(saasEnv);

        // TODO: 18/9/11 assemble returnResult ;
        Integer count = alarmRecordService.countAlarmRecordInBizType(bizType.name().toLowerCase(),
                MonitorDateUtils.getOClockTime(new Date()), new Date());


        result.setWholeConversionResult(wholeConversionResult);
        result.setOperators(list);
        result.setAppTaskStatResult(appTaskStatResult);
        result.setCount(count);

        return MonitorResultBuilder.build(result);
    }

}