package com.treefinance.saas.monitor.biz.facade;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.treefinance.saas.monitor.common.utils.DataConverterUtils;
import com.treefinance.saas.monitor.dao.entity.AsStatCallback;
import com.treefinance.saas.monitor.dao.entity.AsStatCallbackCriteria;
import com.treefinance.saas.monitor.dao.entity.AsStatDayCallback;
import com.treefinance.saas.monitor.dao.entity.AsStatDayCallbackCriteria;
import com.treefinance.saas.monitor.dao.mapper.AsStatCallbackMapper;
import com.treefinance.saas.monitor.dao.mapper.AsStatDayCallbackMapper;
import com.treefinance.saas.monitor.facade.domain.request.CallbackMsgStatAccessRequest;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResultBuilder;
import com.treefinance.saas.monitor.facade.domain.ro.stat.callback.AsStatCallbackRO;
import com.treefinance.saas.monitor.facade.domain.ro.stat.callback.AsStatDayCallbackRO;
import com.treefinance.saas.monitor.facade.exception.ParamCheckerException;
import com.treefinance.saas.monitor.facade.service.stat.CallbackMsgStatAccessFacade;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Buddha Bless , No Bug !
 *
 * @author haojiahong
 * @date 2018/3/15
 */
@Service("callbackMsgStatAccessFacade")
public class CallbackMsgStatAccessFacadeImpl implements CallbackMsgStatAccessFacade {

    private static final Logger logger = LoggerFactory.getLogger(CallbackMsgStatAccessFacade.class);

    @Autowired
    private AsStatCallbackMapper asStatCallbackMapper;
    @Autowired
    private AsStatDayCallbackMapper asStatDayCallbackMapper;


    @Override
    public MonitorResult<List<AsStatDayCallbackRO>> queryCallbackMsgStatDayAccessList(CallbackMsgStatAccessRequest request) {
        if (request == null || request.getDataType() == null || request.getBizType() == null || StringUtils.isBlank(request.getAppId())
                || request.getStartTime() == null || request.getEndTime() == null) {
            logger.error("日回调数据统计查询,输入参数appId,dataType,bizType,startTime,endTime为空,request={}", JSON.toJSONString(request));
            throw new ParamCheckerException("请求参数非法,appId,dataType,bizType,startTime,endTime必填");
        }
        logger.info("日回调数据统计查询,输入参数request={}", JSON.toJSONString(request));
        List<AsStatDayCallbackRO> result = Lists.newArrayList();

        AsStatDayCallbackCriteria criteria = new AsStatDayCallbackCriteria();
        criteria.setOrderByClause("callbackCount desc");
        criteria.createCriteria().andAppIdEqualTo(request.getAppId())
                .andDataTypeEqualTo(request.getDataType())
                .andBizTypeEqualTo(request.getBizType())
                .andDataTimeBetween(request.getStartTime(), request.getEndTime());
        List<AsStatDayCallback> list = asStatDayCallbackMapper.selectByExample(criteria);
        if (!CollectionUtils.isEmpty(list)) {
            result = DataConverterUtils.convert(list, AsStatDayCallbackRO.class);
        }
        return MonitorResultBuilder.build(result);
    }

    @Override
    public MonitorResult<List<AsStatCallbackRO>> queryCallbackMsgStatAccessList(CallbackMsgStatAccessRequest request) {
        if (request == null || request.getDataType() == null || request.getBizType() == null || StringUtils.isBlank(request.getAppId())
                || request.getStartTime() == null || request.getEndTime() == null) {
            logger.error("分时回调数据统计查询,输入参数appId,dataType,bizType,startTime,endTime为空,request={}", JSON.toJSONString(request));
            throw new ParamCheckerException("请求参数非法,appId,dataType,bizType,startTime,endTime必填");
        }
        logger.info("分时回调数据统计查询,输入参数request={}", JSON.toJSONString(request));
        List<AsStatCallbackRO> result = Lists.newArrayList();

        AsStatCallbackCriteria criteria = new AsStatCallbackCriteria();
        criteria.setOrderByClause("callbackCount desc");
        criteria.createCriteria().andAppIdEqualTo(request.getAppId())
                .andDataTypeEqualTo(request.getDataType())
                .andBizTypeEqualTo(request.getBizType())
                .andDataTimeBetween(request.getStartTime(), request.getEndTime());
        List<AsStatCallback> list = asStatCallbackMapper.selectByExample(criteria);
        if (!CollectionUtils.isEmpty(list)) {
            result = DataConverterUtils.convert(list, AsStatCallbackRO.class);
        }
        return MonitorResultBuilder.build(result);
    }
}
