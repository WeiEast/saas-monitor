package com.treefinance.saas.monitor.biz.facade;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.treefinance.saas.monitor.common.constants.MonitorConstants;
import com.treefinance.saas.monitor.common.utils.DataConverterUtils;
import com.treefinance.saas.monitor.common.utils.MonitorDateUtils;
import com.treefinance.saas.monitor.dao.entity.RealTimeStatAccess;
import com.treefinance.saas.monitor.dao.entity.RealTimeStatAccessCriteria;
import com.treefinance.saas.monitor.dao.mapper.RealTimeStatAccessMapper;
import com.treefinance.saas.monitor.facade.domain.request.BaseStatAccessRequest;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResultBuilder;
import com.treefinance.saas.monitor.facade.domain.ro.stat.RealTimeStatAccessRO;
import com.treefinance.saas.monitor.facade.exception.ParamCheckerException;
import com.treefinance.saas.monitor.facade.service.stat.RealTimeStatAccessFacade;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private RealTimeStatAccessMapper realTimeStatAccessMapper;

    @Override
    public MonitorResult<List<RealTimeStatAccessRO>> queryRealTimeStatAccess(BaseStatAccessRequest request) {

        if (request == null || request.getBizType() == null || request.getStartTime() == null || request.getEndTime() == null) {
            logger.error("任务实时监控数据统计查询,输入参数bizType,startTime,endTime,saasEnv为空,request={}", JSON.toJSONString(request));
            throw new ParamCheckerException("请求参数非法,bizType,startTime,endTime,saasEnv必填");
        }
        logger.info("任务实时监控数据统计查询,输入参数request={}", JSON.toJSONString(request));
        List<RealTimeStatAccessRO> result = Lists.newArrayList();

        RealTimeStatAccessCriteria criteria = new RealTimeStatAccessCriteria();
        RealTimeStatAccessCriteria.Criteria innerCriteria = criteria.createCriteria();
        if (StringUtils.isNotBlank(request.getAppId())) {
            innerCriteria.andAppIdEqualTo(request.getAppId());
        } else {
            innerCriteria.andAppIdEqualTo(MonitorConstants.VIRTUAL_TOTAL_STAT_APP_ID);
        }
        if (request.getSaasEnv() != null) {
            innerCriteria.andSaasEnvEqualTo(request.getSaasEnv());
        } else {
            innerCriteria.andSaasEnvEqualTo((byte) 0);
        }
        innerCriteria.andBizTypeEqualTo(request.getBizType())
                .andDataTimeGreaterThanOrEqualTo(request.getStartTime())
                .andDataTimeLessThan(request.getEndTime());
        List<RealTimeStatAccess> list = realTimeStatAccessMapper.selectByExample(criteria);
        if (!CollectionUtils.isEmpty(list)) {
            result = this.convertStatData(list);
            if (request.getIntervalMins() != null) {
                result = this.convertIntervalMinsData(result, request.getIntervalMins());
            }
        }
        return MonitorResultBuilder.build(result);
    }

    @Override
    public MonitorResult<List<RealTimeStatAccessRO>> queryAvgRealTimeStatAccess(BaseStatAccessRequest request) {
//        if (request == null || request.getBizType() == null || StringUtils.isBlank(request.getAppId())
//                || request.getStartTime() == null || request.getEndTime() == null || request.getSaasEnv() == null) {
//            logger.error("任务实时监控7天平均数据统计查询,输入参数appId,bizType,startTime,endTime,saasEnv为空,request={}", JSON.toJSONString(request));
//            throw new ParamCheckerException("请求参数非法,appId,bizType,startTime,endTime,saasEnv必填");
//        }
//        logger.info("任务实时监控7天平均数据统计查询,输入参数request={}", JSON.toJSONString(request));
//        List<RealTimeStatAccessRO> result = Lists.newArrayList();
//
//        RealTimeStatAccessCriteria criteria = new RealTimeStatAccessCriteria();
//        RealTimeStatAccessCriteria.Criteria innerCriteria = criteria.createCriteria();
//        if (StringUtils.isNotBlank(request.getAppId())) {
//            innerCriteria.andAppIdEqualTo(request.getAppId());
//        } else {
//            innerCriteria.andAppIdEqualTo(MonitorConstants.VIRTUAL_TOTAL_STAT_APP_ID);
//        }
//        innerCriteria.andBizTypeEqualTo(request.getBizType())
//                .andSaasEnvEqualTo(request.getSaasEnv())
//                .andDataTimeGreaterThanOrEqualTo(DateUtils.addDays(request.getStartTime(), -7))
//                .andDataTimeLessThan(DateUtils.addDays(request.getEndTime(), -1));
//        List<RealTimeStatAccess> list = realTimeStatAccessMapper.selectByExample(criteria);
//        if (!CollectionUtils.isEmpty(list)) {
//            result = this.convertStatData(list);
//            if (request.getIntervalMins() != null) {
//                result = this.convertIntervalMinsData(result, request.getIntervalMins());
//            }
//        }
//        result = computeAvgResult(result);
        return MonitorResultBuilder.build(null);

    }

    private List<RealTimeStatAccessRO> convertIntervalMinsData(List<RealTimeStatAccessRO> list, Integer intervalMins) {
        List<RealTimeStatAccessRO> result = Lists.newArrayList();
        Map<Date, List<RealTimeStatAccessRO>> map = list.stream()
                .collect(Collectors.groupingBy(data -> MonitorDateUtils.getIntervalDateTime(data.getDataTime(), intervalMins)));
        for (Map.Entry<Date, List<RealTimeStatAccessRO>> entry : map.entrySet()) {
            RealTimeStatAccessRO data = new RealTimeStatAccessRO();
            BeanUtils.copyProperties(entry.getValue().get(0), data);
            data.setDataTime(entry.getKey());

            Map<String, Integer> statDataMap = Maps.newHashMap();
            for (RealTimeStatAccessRO item : entry.getValue()) {
                Map<String, Integer> itemMap = item.getStatDataMap();
                for (Map.Entry<String, Integer> itemEntry : itemMap.entrySet()) {
                    if (statDataMap.get(itemEntry.getKey()) == null) {
                        statDataMap.put(itemEntry.getKey(), itemEntry.getValue());
                    } else {
                        Integer value = statDataMap.get(itemEntry.getKey());
                        value = value + itemEntry.getValue();
                        statDataMap.put(itemEntry.getKey(), value);
                    }
                }
            }
            data.setStatDataMap(statDataMap);
            result.add(data);
        }
        return result;
    }

    private List<RealTimeStatAccessRO> convertStatData(List<RealTimeStatAccess> list) {
        List<RealTimeStatAccessRO> result = Lists.newArrayList();
        for (RealTimeStatAccess realTimeStatAccess : list) {
            RealTimeStatAccessRO realTimeStatAccessRO = DataConverterUtils.convert(realTimeStatAccess, RealTimeStatAccessRO.class);
            if (StringUtils.isNotBlank(realTimeStatAccess.getStatData())) {
                Map<String, Integer> statDataMap = JSON.parseObject(realTimeStatAccess.getStatData(), new TypeReference<Map<String, Integer>>() {
                });
                realTimeStatAccessRO.setStatDataMap(statDataMap);
            }
            result.add(realTimeStatAccessRO);
        }
        return result;
    }
}
