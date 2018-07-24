package com.treefinance.saas.monitor.biz.facade;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.monitor.biz.service.RealTimeAvgStatAccessService;
import com.treefinance.saas.monitor.common.domain.dto.RealTimeStatAccessDTO;
import com.treefinance.saas.monitor.common.utils.DataConverterUtils;
import com.treefinance.saas.monitor.facade.domain.request.BaseStatAccessRequest;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResultBuilder;
import com.treefinance.saas.monitor.facade.domain.ro.stat.RealTimeStatAccessRO;
import com.treefinance.saas.monitor.facade.exception.ParamCheckerException;
import com.treefinance.saas.monitor.facade.service.stat.RealTimeStatAccessFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Good Luck Bro , No Bug !
 *
 * @author haojiahong
 * @date 2018/6/21
 */
@Service("realTimeStatAccessFacade")
public class RealTimeStatAccessFacadeImpl implements RealTimeStatAccessFacade {

    private static final Logger logger = LoggerFactory.getLogger(RealTimeStatAccessFacade.class);

    @Autowired
    private RealTimeAvgStatAccessService realTimeAvgStatAccessService;

    @Override
    public MonitorResult<List<RealTimeStatAccessRO>> queryRealTimeStatAccess(BaseStatAccessRequest request) {

        if (request == null || request.getBizType() == null || request.getStartTime() == null || request.getEndTime() == null) {
            logger.error("任务实时监控数据统计查询,输入参数bizType,startTime,endTime,saasEnv为空,request={}", JSON.toJSONString(request));
            throw new ParamCheckerException("请求参数非法,bizType,startTime,endTime,saasEnv必填");
        }
        logger.info("任务实时监控数据统计查询,输入参数request={}", JSON.toJSONString(request));
        List<RealTimeStatAccessDTO> list = realTimeAvgStatAccessService.queryRealTimeStatAccess(request);
        List<RealTimeStatAccessRO> result = DataConverterUtils.convert(list, RealTimeStatAccessRO.class);
        return MonitorResultBuilder.build(result);
    }

    @Override
    public MonitorResult<List<RealTimeStatAccessRO>> queryAvgRealTimeStatAccess(BaseStatAccessRequest request) {
        if (request == null || request.getBizType() == null || request.getStartTime() == null || request.getEndTime() == null) {
            logger.error("任务实时监控7天平均数据统计查询,输入参数bizType,startTime,endTime,saasEnv为空,request={}", JSON.toJSONString(request));
            throw new ParamCheckerException("请求参数非法,bizType,startTime,endTime,saasEnv必填");
        }
        logger.info("任务实时监控7天平均数据统计查询,输入参数request={}", JSON.toJSONString(request));
        List<RealTimeStatAccessDTO> list = realTimeAvgStatAccessService.queryDataByConditions(request.getAppId(),
                request.getSaasEnv(), request.getBizType(), request.getStartTime(),
                request.getEndTime(), request.getIntervalMins(), request.getHiddenRecentPoint());
        List<RealTimeStatAccessRO> result = DataConverterUtils.convert(list, RealTimeStatAccessRO.class);
        return MonitorResultBuilder.build(result);

    }

}
