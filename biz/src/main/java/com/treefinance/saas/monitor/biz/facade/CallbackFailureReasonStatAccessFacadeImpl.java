package com.treefinance.saas.monitor.biz.facade;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.treefinance.saas.monitor.common.utils.DataConverterUtils;
import com.treefinance.saas.monitor.common.utils.MonitorDateUtils;
import com.treefinance.saas.monitor.dao.entity.CallbackFailureReasonStatAccess;
import com.treefinance.saas.monitor.dao.entity.CallbackFailureReasonStatAccessCriteria;
import com.treefinance.saas.monitor.dao.entity.CallbackFailureReasonStatDayAccess;
import com.treefinance.saas.monitor.dao.entity.CallbackFailureReasonStatDayAccessCriteria;
import com.treefinance.saas.monitor.dao.mapper.CallbackFailureReasonStatAccessMapper;
import com.treefinance.saas.monitor.dao.mapper.CallbackFailureReasonStatDayAccessMapper;
import com.treefinance.saas.monitor.facade.domain.request.CallbackFailureReasonStatAccessRequest;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResultBuilder;
import com.treefinance.saas.monitor.facade.domain.ro.stat.callback.CallbackFailureReasonStatAccessRO;
import com.treefinance.saas.monitor.facade.domain.ro.stat.callback.CallbackFailureReasonStatDayAccessRO;
import com.treefinance.saas.monitor.facade.exception.ParamCheckerException;
import com.treefinance.saas.monitor.facade.service.stat.CallbackFailureReasonStatAccessFacade;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Good Luck Bro , No Bug !
 *
 * @author haojiahong
 * @date 2018/6/12
 */
@Component("callbackFailureReasonStatAccessFacade")
public class CallbackFailureReasonStatAccessFacadeImpl implements CallbackFailureReasonStatAccessFacade {

    private static final Logger logger = LoggerFactory.getLogger(CallbackFailureReasonStatAccessFacade.class);

    @Autowired
    private CallbackFailureReasonStatAccessMapper callbackFailureReasonStatAccessMapper;
    @Autowired
    private CallbackFailureReasonStatDayAccessMapper callbackFailureReasonStatDayAccessMapper;


    @Override
    public MonitorResult<List<CallbackFailureReasonStatDayAccessRO>> queryCallbackFailureReasonStatDayAccessList(CallbackFailureReasonStatAccessRequest request) {
        if (request == null || request.getDataType() == null || request.getBizType() == null || request.getSaasEnv() == null
                || StringUtils.isBlank(request.getAppId()) || request.getStartTime() == null || request.getEndTime() == null) {

            logger.error("日回调失败具体原因数据统计查询,输入参数appId,dataType,bizType,saasEnv,startTime,endTime为空,request={}", JSON.toJSONString(request));
            throw new ParamCheckerException("请求参数非法,appId,dataType,bizType,saasEnv,startTime,endTime必填");
        }
        logger.info("日回调失败具体原因数据统计查询,输入参数request={}", JSON.toJSONString(request));
        List<CallbackFailureReasonStatDayAccessRO> result = Lists.newArrayList();

        CallbackFailureReasonStatDayAccessCriteria criteria = new CallbackFailureReasonStatDayAccessCriteria();
        criteria.createCriteria().andAppIdEqualTo(request.getAppId())
                .andSaasEnvEqualTo(request.getSaasEnv())
                .andDataTypeEqualTo(request.getDataType())
                .andBizTypeEqualTo(request.getBizType())
                .andDataTimeGreaterThanOrEqualTo(request.getStartTime())
                .andDataTimeLessThan(request.getEndTime())
                .andGroupCodeEqualTo(request.getGroupCode());

        List<CallbackFailureReasonStatDayAccess> list = callbackFailureReasonStatDayAccessMapper.selectByExample(criteria);
        if (!CollectionUtils.isEmpty(list)) {
            result = DataConverterUtils.convert(list, CallbackFailureReasonStatDayAccessRO.class);
        }
        return MonitorResultBuilder.build(result);
    }

    @Override
    public MonitorResult<List<CallbackFailureReasonStatAccessRO>> queryCallbackFailureReasonStatAccessList(CallbackFailureReasonStatAccessRequest request) {
        if (request == null || request.getDataType() == null || request.getBizType() == null || request.getSaasEnv() == null
                || StringUtils.isBlank(request.getAppId()) || request.getStartTime() == null || request.getEndTime() == null
                || request.getIntervalMins() == null) {

            logger.error("分时回调失败具体原因数据统计查询,输入参数appId,dataType,bizType,saasEnv,startTime,endTime,intervalMins为空,request={}", JSON.toJSONString(request));
            throw new ParamCheckerException("请求参数非法,appId,dataType,bizType,saasEnv,startTime,endTime,intervalMins必填");
        }
        logger.info("分时回调失败具体原因数据统计查询,输入参数request={}", JSON.toJSONString(request));
        List<CallbackFailureReasonStatAccessRO> result = Lists.newArrayList();

        CallbackFailureReasonStatAccessCriteria criteria = new CallbackFailureReasonStatAccessCriteria();
        criteria.createCriteria().andAppIdEqualTo(request.getAppId())
                .andSaasEnvEqualTo(request.getSaasEnv())
                .andDataTypeEqualTo(request.getDataType())
                .andBizTypeEqualTo(request.getBizType())
                .andGroupCodeEqualTo(request.getGroupCode())
                .andDataTimeGreaterThanOrEqualTo(request.getStartTime())
                .andDataTimeLessThan(request.getEndTime());

        List<CallbackFailureReasonStatAccess> list = callbackFailureReasonStatAccessMapper.selectByExample(criteria);
        List<CallbackFailureReasonStatAccess> changeList = this.changeIntervalDataTimeList(list, request.getIntervalMins());

        if (!CollectionUtils.isEmpty(changeList)) {
            result = DataConverterUtils.convert(changeList, CallbackFailureReasonStatAccessRO.class);
        }
        return MonitorResultBuilder.build(result);
    }

    private List<CallbackFailureReasonStatAccess> changeIntervalDataTimeList(List<CallbackFailureReasonStatAccess> list, Integer intervalMins) {
        Map<Date, List<CallbackFailureReasonStatAccess>> map = list.stream().collect(Collectors.groupingBy(data -> MonitorDateUtils.getIntervalDateTime(data.getDataTime(), intervalMins)));
        List<CallbackFailureReasonStatAccess> resultList = Lists.newArrayList();
        for (Map.Entry<Date, List<CallbackFailureReasonStatAccess>> entry : map.entrySet()) {
            if (CollectionUtils.isEmpty(entry.getValue())) {
                continue;
            }
            CallbackFailureReasonStatAccess data = new CallbackFailureReasonStatAccess();
            BeanUtils.copyProperties(entry.getValue().get(0), data);
            data.setDataTime(entry.getKey());
            List<CallbackFailureReasonStatAccess> entryList = entry.getValue();
            int totalCount = 0, unKnownReasonCount = 0, personalReasonCount = 0;
            for (CallbackFailureReasonStatAccess item : entryList) {
                totalCount = totalCount + item.getTotalCount();
                unKnownReasonCount = unKnownReasonCount + item.getUnKnownReasonCount();
                personalReasonCount = personalReasonCount + item.getPersonalReasonCount();
            }
            data.setTotalCount(totalCount);
            data.setUnKnownReasonCount(unKnownReasonCount);
            data.setPersonalReasonCount(personalReasonCount);
            resultList.add(data);
        }
        return resultList;
    }
}
