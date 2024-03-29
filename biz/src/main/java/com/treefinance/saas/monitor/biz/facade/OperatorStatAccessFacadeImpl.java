package com.treefinance.saas.monitor.biz.facade;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.treefinance.b2b.saas.util.SaasDateUtils;
import com.treefinance.saas.monitor.common.constants.MonitorConstants;
import com.treefinance.saas.monitor.common.domain.dto.OperatorStatAccessDTO;
import com.treefinance.saas.monitor.context.component.AbstractFacade;
import com.treefinance.saas.monitor.dao.entity.OperatorAllStatAccess;
import com.treefinance.saas.monitor.dao.entity.OperatorAllStatAccessCriteria;
import com.treefinance.saas.monitor.dao.entity.OperatorAllStatDayAccess;
import com.treefinance.saas.monitor.dao.entity.OperatorAllStatDayAccessCriteria;
import com.treefinance.saas.monitor.dao.entity.OperatorStatAccess;
import com.treefinance.saas.monitor.dao.entity.OperatorStatAccessCriteria;
import com.treefinance.saas.monitor.dao.entity.OperatorStatDayAccess;
import com.treefinance.saas.monitor.dao.entity.OperatorStatDayAccessCriteria;
import com.treefinance.saas.monitor.dao.mapper.OperatorAllStatAccessMapper;
import com.treefinance.saas.monitor.dao.mapper.OperatorAllStatDayAccessMapper;
import com.treefinance.saas.monitor.dao.mapper.OperatorStatAccessMapper;
import com.treefinance.saas.monitor.dao.mapper.OperatorStatDayAccessMapper;
import com.treefinance.saas.monitor.facade.domain.request.OperatorStatAccessRequest;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResultBuilder;
import com.treefinance.saas.monitor.facade.domain.ro.stat.operator.OperatorAllStatAccessRO;
import com.treefinance.saas.monitor.facade.domain.ro.stat.operator.OperatorAllStatDayAccessRO;
import com.treefinance.saas.monitor.facade.domain.ro.stat.operator.OperatorStatAccessRO;
import com.treefinance.saas.monitor.facade.domain.ro.stat.operator.OperatorStatDayAccessRO;
import com.treefinance.saas.monitor.facade.exception.ParamCheckerException;
import com.treefinance.saas.monitor.facade.service.stat.OperatorStatAccessFacade;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author haojiahong
 * @date 2017/11/1
 */
@Service("operatorStatAccessFacade")
public class OperatorStatAccessFacadeImpl extends AbstractFacade implements OperatorStatAccessFacade {

    private final static Logger logger = LoggerFactory.getLogger(OperatorStatAccessFacade.class);

    @Autowired
    private OperatorStatAccessMapper operatorStatAccessMapper;
    @Autowired
    private OperatorStatDayAccessMapper operatorStatDayAccessMapper;
    @Autowired
    private OperatorAllStatAccessMapper operatorAllStatAccessMapper;
    @Autowired
    private OperatorAllStatDayAccessMapper operatorAllStatDayAccessMapper;


    @Override
    public MonitorResult<List<OperatorStatDayAccessRO>> queryOperatorStatDayAccessList(OperatorStatAccessRequest request) {
        if (request == null || request.getDataDate() == null || request.getStatType() == null) {
            logger.error("查询各个运营商日监控统计数据,输入参数为空或者dataDate,statType为空,request={}", JSON.toJSONString(request));
            throw new ParamCheckerException("请求参数非法");
        }
        logger.info("查询各个运营商日监控统计数据,输入参数request={}", JSON.toJSONString(request));
        List<OperatorStatDayAccessRO> result = Lists.newArrayList();

        OperatorStatDayAccessCriteria criteria = new OperatorStatDayAccessCriteria();
        criteria.setOrderByClause("confirmMobileCount desc");
        criteria.createCriteria().andDataTimeEqualTo(request.getDataDate()).andDataTypeEqualTo(request.getStatType());
        List<OperatorStatDayAccess> list = operatorStatDayAccessMapper.selectByExample(criteria);
        if (!CollectionUtils.isEmpty(list)) {
            result = convert(list, OperatorStatDayAccessRO.class);
        }
        logger.info("查询各个运营商日监控统计数据(不分页),返回结果result={}", JSON.toJSONString(result));
        return MonitorResultBuilder.build(result);
    }

    @Override
    public MonitorResult<List<OperatorStatDayAccessRO>> queryOperatorStatDayAccessListWithPage(OperatorStatAccessRequest request) {
        if (request == null || request.getDataDate() == null || request.getStatType() == null
                || StringUtils.isBlank(request.getAppId()) || request.getSaasEnv() == null) {
            logger.error("查询各个运营商日监控统计数据(分页),输入参数dataDate,statType,appId,saasEnv不能为空,request={}", JSON.toJSONString(request));
            throw new ParamCheckerException("请求参数非法");
        }
        logger.info("查询各个运营商日监控统计数据(分页),输入参数request={}", JSON.toJSONString(request));
        List<OperatorStatDayAccessRO> result = Lists.newArrayList();

        OperatorStatDayAccessCriteria criteria = new OperatorStatDayAccessCriteria();
        criteria.setOrderByClause("confirmMobileCount desc");
        criteria.setLimit(request.getPageSize());
        criteria.setOffset(request.getOffset());
        OperatorStatDayAccessCriteria.Criteria innerCriteria = criteria.createCriteria();
        innerCriteria.andAppIdEqualTo(request.getAppId())
                .andDataTimeEqualTo(request.getDataDate())
                .andSaasEnvEqualTo(request.getSaasEnv())
                .andDataTypeEqualTo(request.getStatType())
                .andGroupCodeNotEqualTo(MonitorConstants.VIRTUAL_TOTAL_STAT_OPERATOR);
        if (StringUtils.isNotBlank(request.getGroupName())) {
            innerCriteria.andGroupNameLike("%" + request.getGroupName() + "%");
        }
        long total = operatorStatDayAccessMapper.countByExample(criteria);
        if (total > 0) {
            List<OperatorStatDayAccess> list = operatorStatDayAccessMapper.selectPaginationByExample(criteria);
            for (OperatorStatDayAccess data : list) {
                OperatorStatDayAccessRO ro = convert(data, OperatorStatDayAccessRO.class);
                ro.setLoginConversionRate(calcRate(data.getConfirmMobileCount(), data.getStartLoginCount()));
                ro.setLoginSuccessRate(calcRate(data.getStartLoginCount(), data.getLoginSuccessCount()));
                ro.setCrawlSuccessRate(calcRate(data.getLoginSuccessCount(), data.getCrawlSuccessCount()));
                ro.setProcessSuccessRate(calcRate(data.getCrawlSuccessCount(), data.getProcessSuccessCount()));
                ro.setCallbackSuccessRate(calcRate(data.getProcessSuccessCount(), data.getCallbackSuccessCount()));
                ro.setTaskUserRatio(calcRatio(data.getUserCount(), data.getTaskCount()));
                //add average 某一个运营商某七天的数据

                ro.setDayAverage(getPreviousAverage(request.getAppId(), request.getSaasEnv(), request.getStatType(), ro
                        .getGroupCode(), request.getDataDate(), request.getGroupName()));

                result.add(ro);
            }
        }
        logger.info("查询各个运营商日监控统计数据(分页),返回结果result={}", JSON.toJSONString(result));
        return MonitorResultBuilder.pageResult(request, result, total);
    }

    private BigDecimal getPreviousAverage(String appId, byte saasEnv, byte dataType, String groupCode, Date initDate,
                                          String groupName) {
        Date startDate = com.treefinance.toolkit.util.DateUtils.minusDays(initDate, 7);

        OperatorStatDayAccessCriteria criteria = new OperatorStatDayAccessCriteria();
        criteria.setOrderByClause("confirmMobileCount desc");
        OperatorStatDayAccessCriteria.Criteria innerCriteria = criteria.createCriteria();
        innerCriteria.andAppIdEqualTo(appId)
                .andDataTimeGreaterThanOrEqualTo(startDate)
                .andDataTimeLessThan(initDate)
                .andGroupCodeEqualTo(groupCode)
                .andSaasEnvEqualTo(saasEnv)
                .andDataTypeEqualTo(dataType)
                .andGroupCodeNotEqualTo(MonitorConstants.VIRTUAL_TOTAL_STAT_OPERATOR);
        if (StringUtils.isNotBlank(groupName)) {
            innerCriteria.andGroupNameLike("%" + groupName + "%");
        }

        List<OperatorStatDayAccess> list = operatorStatDayAccessMapper.selectByExample(criteria);

        if (list.isEmpty()) {
            return BigDecimal.ZERO;
        }

        Integer callbackCount = 0;
        Integer comfirmCount = 0;

        for (OperatorStatDayAccess operatorStatDayAccess : list) {
            callbackCount += operatorStatDayAccess.getCallbackSuccessCount();
            comfirmCount += operatorStatDayAccess.getConfirmMobileCount();
        }

        if (comfirmCount.equals(0)) {
            return BigDecimal.ZERO;
        }

        return new BigDecimal(callbackCount).divide(new BigDecimal(comfirmCount), 2, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100));
    }


    @Override
    public MonitorResult<List<OperatorStatAccessRO>> queryOperatorStatHourAccessListWithPage(OperatorStatAccessRequest request) {
        if (request == null || request.getDataDate() == null || request.getStatType() == null || request.getSaasEnv() == null
                || StringUtils.isBlank(request.getAppId()) || request.getIntervalMins() == null) {
            logger.error("查询各个运营商小时监控统计数据(分页),输入参数dataDate,statType,saasEnv,appId,intervalMins为空,request={}", JSON.toJSONString(request));
            throw new ParamCheckerException("请求参数非法");
        }
        logger.info("查询各个运营商小时监控统计数据(分页),输入参数request={}", JSON.toJSONString(request));
        List<OperatorStatAccessRO> result = Lists.newArrayList();

        OperatorStatAccessCriteria criteria = new OperatorStatAccessCriteria();
        OperatorStatAccessCriteria.Criteria innerCriteria = criteria.createCriteria();
        Date startTime = request.getDataDate();
        Date endTime = com.treefinance.toolkit.util.DateUtils.plusMinutes(request.getDataDate(),
            request.getIntervalMins());
        innerCriteria.andAppIdEqualTo(request.getAppId())
                .andDataTypeEqualTo(request.getStatType())
                .andSaasEnvEqualTo(request.getSaasEnv())
                .andDataTimeGreaterThanOrEqualTo(startTime)
                .andDataTimeLessThan(endTime)
                .andGroupCodeNotEqualTo(MonitorConstants.VIRTUAL_TOTAL_STAT_OPERATOR);
        if (StringUtils.isNotBlank(request.getGroupName())) {
            innerCriteria.andGroupNameLike("%" + request.getGroupName() + "%");
        }

        long total = operatorStatAccessMapper.countByExample(criteria);
        if (total == 0) {
            return MonitorResultBuilder.pageResult(request, result, 0L);
        }

        //数据库5分钟一个点,无法用数据库分页,这里手动分页
        List<OperatorStatAccess> list = operatorStatAccessMapper.selectByExample(criteria);
        List<OperatorStatAccessDTO> changeList = this.changeSumGroupCodeOperatorStatAccess(list, startTime);
        changeList = changeList.stream().sorted((o1, o2) -> o2.getConfirmMobileCount().compareTo(o1.getConfirmMobileCount())).collect(Collectors.toList());
        List<OperatorStatAccessDTO> pageList;
        int limit = request.getOffset() + request.getPageSize();
        if (limit > changeList.size()) {
            if (request.getOffset() > changeList.size()) {
                pageList = Lists.newArrayList();
            } else {
                pageList = changeList.subList(request.getOffset(), changeList.size());
            }
        } else {
            pageList = changeList.subList(request.getOffset(), limit);
        }
        for (OperatorStatAccessDTO data : pageList) {
            OperatorStatAccessRO ro = convert(data, OperatorStatAccessRO.class);
            ro.setAverage(getAverageWholeConversion(request, ro.getGroupCode()));
            result.add(ro);
        }
        return MonitorResultBuilder.pageResult(request, result, changeList.size());
    }

    /**
     * 分运营商 分时监控 过去七天的总转化率的平均值
     */
    private BigDecimal getAverageWholeConversion(OperatorStatAccessRequest request, String groupCode) {

        List<Date> dates = SaasDateUtils.getPreviousOClockTime(request.getDataDate(), 7);

        AtomicReference<Integer> callbackCount = new AtomicReference<>(0);
        AtomicReference<Integer> entryCount = new AtomicReference<>(0);

        for (Date startTime : dates) {
            OperatorStatAccessCriteria criteria = new OperatorStatAccessCriteria();
            OperatorStatAccessCriteria.Criteria innerCriteria = criteria.createCriteria();
            Date endTime = DateUtils.addMinutes(startTime, request.getIntervalMins());
            innerCriteria.andAppIdEqualTo(request.getAppId())
                    .andDataTypeEqualTo(request.getStatType())
                    .andGroupCodeEqualTo(groupCode)
                    .andSaasEnvEqualTo(request.getSaasEnv())
                    .andDataTimeGreaterThanOrEqualTo(startTime)
                    .andDataTimeLessThan(endTime)
                    .andGroupCodeNotEqualTo(MonitorConstants.VIRTUAL_TOTAL_STAT_OPERATOR);
            if (StringUtils.isNotBlank(request.getGroupName())) {
                innerCriteria.andGroupNameLike("%" + request.getGroupName() + "%");
            }

            List<OperatorStatAccess> operatorStatAccesses = operatorStatAccessMapper.selectByExample(criteria);
            if (operatorStatAccesses.isEmpty()) {
                continue;
            }

            operatorStatAccesses.forEach(operatorStatAccess -> {
                callbackCount.updateAndGet(v -> v + operatorStatAccess
                        .getCallbackSuccessCount());
                entryCount.updateAndGet(v -> v + operatorStatAccess.getConfirmMobileCount());
            });
        }
        logger.info("获取{}过去七天的平均值,成功数：{}，总数：{}", groupCode, callbackCount.get(), entryCount.get());

        if (entryCount.get() == 0) {
            return BigDecimal.ZERO;
        }

        return new BigDecimal(callbackCount.get()).divide(new BigDecimal(entryCount.get()), 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
    }


    private List<OperatorStatAccessDTO> changeSumGroupCodeOperatorStatAccess(List<OperatorStatAccess> list, Date startTime) {
        Map<String, List<OperatorStatAccess>> map = list.stream().collect(Collectors.groupingBy(OperatorStatAccess::getGroupCode));
        List<OperatorStatAccessDTO> resultList = Lists.newArrayList();
        for (Map.Entry<String, List<OperatorStatAccess>> entry : map.entrySet()) {
            if (CollectionUtils.isEmpty(entry.getValue())) {
                continue;
            }
            OperatorStatAccessDTO data = new OperatorStatAccessDTO();
            BeanUtils.copyProperties(entry.getValue().get(0), data);
            data.setDataTime(startTime);
            List<OperatorStatAccess> entryList = entry.getValue();
            int userCount = 0, taskCount = 0, entryCount = 0, confirmMobileCount = 0,
                    startLoginCount = 0, loginSuccessCount = 0, crawlSuccessCount = 0,
                    processSuccessCount = 0, callbackSuccessCount = 0;
            for (OperatorStatAccess item : entryList) {
                userCount = userCount + item.getUserCount();
                taskCount = taskCount + item.getTaskCount();
                confirmMobileCount = confirmMobileCount + item.getConfirmMobileCount();
                startLoginCount = startLoginCount + item.getStartLoginCount();
                loginSuccessCount = loginSuccessCount + item.getLoginSuccessCount();
                crawlSuccessCount = crawlSuccessCount + item.getCrawlSuccessCount();
                processSuccessCount = processSuccessCount + item.getProcessSuccessCount();
                callbackSuccessCount = callbackSuccessCount + item.getCallbackSuccessCount();
            }
            data.setUserCount(userCount);
            data.setTaskCount(taskCount);
            data.setConfirmMobileCount(confirmMobileCount);
            data.setStartLoginCount(startLoginCount);
            data.setLoginSuccessCount(loginSuccessCount);
            data.setCrawlSuccessCount(crawlSuccessCount);
            data.setProcessSuccessCount(processSuccessCount);
            data.setCallbackSuccessCount(callbackSuccessCount);
            data.setLoginConversionRate(calcRate(confirmMobileCount, startLoginCount));
            data.setLoginSuccessRate(calcRate(startLoginCount, loginSuccessCount));
            data.setCrawlSuccessRate(calcRate(loginSuccessCount, crawlSuccessCount));
            data.setProcessSuccessRate(calcRate(crawlSuccessCount, processSuccessCount));
            data.setCallbackSuccessRate(calcRate(processSuccessCount, callbackSuccessCount));
            resultList.add(data);
        }
        return resultList;
    }

    @Override
    public MonitorResult<List<OperatorStatDayAccessRO>> queryOneOperatorStatDayAccessListWithPage(OperatorStatAccessRequest request) {
        if (request == null || request.getStartDate() == null || request.getEndDate() == null || request.getStatType() == null
                || StringUtils.isBlank(request.getGroupCode()) || StringUtils.isBlank(request.getAppId()) || request.getSaasEnv() == null) {
            logger.error("查询某一个运营商日监控统计数据(分页),输入参数为空,request={}", JSON.toJSONString(request));
            throw new ParamCheckerException("请求参数非法");
        }
        logger.info("查询某一个运营商日监控统计数据(分页),输入参数request={}", JSON.toJSONString(request));
        List<OperatorStatDayAccessRO> result = Lists.newArrayList();

        OperatorStatDayAccessCriteria criteria = new OperatorStatDayAccessCriteria();
        criteria.setOrderByClause("dataTime desc");
        criteria.setLimit(request.getPageSize());
        criteria.setOffset(request.getOffset());
        criteria.createCriteria().andAppIdEqualTo(request.getAppId())
                .andGroupCodeEqualTo(request.getGroupCode())
                .andDataTypeEqualTo(request.getStatType())
                .andSaasEnvEqualTo(request.getSaasEnv())
                .andDataTimeBetween(request.getStartDate(), request.getEndDate());
        long total = operatorStatDayAccessMapper.countByExample(criteria);
        if (total > 0) {
            List<OperatorStatDayAccess> list = operatorStatDayAccessMapper.selectPaginationByExample(criteria);
            for (OperatorStatDayAccess data : list) {
                OperatorStatDayAccessRO ro = convert(data, OperatorStatDayAccessRO.class);
                ro.setTaskUserRatio(calcRatio(data.getUserCount(), data.getTaskCount()));
                ro.setLoginConversionRate(calcRate(data.getConfirmMobileCount(), data.getStartLoginCount()));
                ro.setLoginSuccessRate(calcRate(data.getStartLoginCount(), data.getLoginSuccessCount()));
                ro.setCrawlSuccessRate(calcRate(data.getLoginSuccessCount(), data.getCrawlSuccessCount()));
                ro.setProcessSuccessRate(calcRate(data.getCrawlSuccessCount(), data.getProcessSuccessCount()));
                ro.setCallbackSuccessRate(calcRate(data.getProcessSuccessCount(), data.getCallbackSuccessCount()));
                result.add(ro);
            }
        }
        logger.info("查询某一个运营商日监控统计数据(分页),返回结果result={}", JSON.toJSONString(result));
        return MonitorResultBuilder.pageResult(request, result, total);
    }

    @Override
    public MonitorResult<List<OperatorStatAccessRO>> queryOperatorStatAccessList(OperatorStatAccessRequest request) {
        if (request == null || request.getStartDate() == null || request.getEndDate() == null || request.getStatType() == null || request.getSaasEnv() == null
                || StringUtils.isBlank(request.getGroupCode()) || StringUtils.isBlank(request.getAppId()) || request.getIntervalMins() == null) {
            logger.error("查询各个运营商小时监控统计数据,输入参数为空,request={}", JSON.toJSONString(request));
            throw new ParamCheckerException("请求参数非法");
        }
        logger.info("查询各个运营商小时监控统计数据,输入参数request={}", JSON.toJSONString(request));
        List<OperatorStatAccessRO> result = Lists.newArrayList();
        OperatorStatAccessCriteria criteria = new OperatorStatAccessCriteria();
        criteria.setOrderByClause("dataTime desc");
        criteria.createCriteria()
                .andAppIdEqualTo(request.getAppId())
                .andGroupCodeEqualTo(request.getGroupCode())
                .andDataTypeEqualTo(request.getStatType())
                .andSaasEnvEqualTo(request.getSaasEnv())
            .andDataTimeBetween(com.treefinance.toolkit.util.DateUtils.getStartTimeOfDay(request.getStartDate()), com.treefinance.toolkit.util.DateUtils
                .getEndTimeOfDay(request.getEndDate()));
        List<OperatorStatAccess> list = operatorStatAccessMapper.selectByExample(criteria);
        if (CollectionUtils.isEmpty(list)) {
            return MonitorResultBuilder.build(result);
        }
        List<OperatorStatAccessDTO> changeList = this.changeIntervalDataTimeOperatorStatAccess(list, request.getIntervalMins());
        for (OperatorStatAccessDTO data : changeList) {
            OperatorStatAccessRO ro = convert(data, OperatorStatAccessRO.class);
            if (ro != null) {
                ro.setTaskUserRatio(calcRatio(data.getUserCount(), data.getTaskCount()));
                result.add(ro);
            }
        }
        logger.info("查询各个运营商小时监控统计数据,输出结果result={}", JSON.toJSONString(result));
        return MonitorResultBuilder.build(result);
    }


    private List<OperatorStatAccessDTO> changeIntervalDataTimeOperatorStatAccess(List<OperatorStatAccess> list, final Integer intervalMins) {
        Map<Date, List<OperatorStatAccess>> map = list.stream().collect(Collectors.groupingBy(data -> SaasDateUtils.getIntervalDateTime(data.getDataTime(), intervalMins)));
        List<OperatorStatAccessDTO> resultList = Lists.newArrayList();
        for (Map.Entry<Date, List<OperatorStatAccess>> entry : map.entrySet()) {
            if (CollectionUtils.isEmpty(entry.getValue())) {
                continue;
            }
            OperatorStatAccessDTO data = new OperatorStatAccessDTO();
            BeanUtils.copyProperties(entry.getValue().get(0), data);
            data.setDataTime(entry.getKey());
            List<OperatorStatAccess> entryList = entry.getValue();
            int userCount = 0, taskCount = 0, entryCount = 0, confirmMobileCount = 0,
                    startLoginCount = 0, loginSuccessCount = 0, crawlSuccessCount = 0,
                    processSuccessCount = 0, callbackSuccessCount = 0;
            for (OperatorStatAccess item : entryList) {
                userCount = userCount + item.getUserCount();
                taskCount = taskCount + item.getTaskCount();
                confirmMobileCount = confirmMobileCount + item.getConfirmMobileCount();
                startLoginCount = startLoginCount + item.getStartLoginCount();
                loginSuccessCount = loginSuccessCount + item.getLoginSuccessCount();
                crawlSuccessCount = crawlSuccessCount + item.getCrawlSuccessCount();
                processSuccessCount = processSuccessCount + item.getProcessSuccessCount();
                callbackSuccessCount = callbackSuccessCount + item.getCallbackSuccessCount();
            }
            data.setUserCount(userCount);
            data.setTaskCount(taskCount);
            data.setConfirmMobileCount(confirmMobileCount);
            data.setStartLoginCount(startLoginCount);
            data.setLoginSuccessCount(loginSuccessCount);
            data.setCrawlSuccessCount(crawlSuccessCount);
            data.setProcessSuccessCount(processSuccessCount);
            data.setCallbackSuccessCount(callbackSuccessCount);
            data.setLoginConversionRate(calcRate(confirmMobileCount, startLoginCount));
            data.setLoginSuccessRate(calcRate(startLoginCount, loginSuccessCount));
            data.setCrawlSuccessRate(calcRate(loginSuccessCount, crawlSuccessCount));
            data.setProcessSuccessRate(calcRate(crawlSuccessCount, processSuccessCount));
            data.setCallbackSuccessRate(calcRate(processSuccessCount, callbackSuccessCount));
            resultList.add(data);
        }
        return resultList;
    }

    @Override
    public MonitorResult<List<OperatorAllStatDayAccessRO>> queryAllOperatorStatDayAccessList(OperatorStatAccessRequest request) {
        if (request == null || request.getStartDate() == null || request.getEndDate() == null || request.getStatType() == null) {
            logger.error("查询所有运营商日监控统计数据,输入参数startDate,endDate,statType不能为空,request={}", JSON.toJSONString(request));
            throw new ParamCheckerException("请求参数非法");
        }
        logger.info("查询所有运营商日监控统计数据,输入参数request={}", JSON.toJSONString(request));
        List<OperatorAllStatDayAccessRO> result = Lists.newArrayList();
        OperatorStatDayAccessCriteria criteria = new OperatorStatDayAccessCriteria();
        criteria.setOrderByClause("dataTime desc");
        criteria.createCriteria().andSaasEnvEqualTo((byte) 0)
                .andDataTypeEqualTo(request.getStatType())
                .andGroupCodeEqualTo(MonitorConstants.VIRTUAL_TOTAL_STAT_OPERATOR)
                .andDataTimeBetween(request.getStartDate(), request.getEndDate());

        List<OperatorStatDayAccess> list = operatorStatDayAccessMapper.selectByExample(criteria);
        if (!CollectionUtils.isEmpty(list)) {
            result = convert(list, OperatorAllStatDayAccessRO.class);
        }
        logger.info("查询所有运营商日监控统计数据,输出结果result={}", JSON.toJSONString(result));
        return MonitorResultBuilder.build(result);
    }

    @Override
    public MonitorResult<List<OperatorAllStatDayAccessRO>> queryAllOperatorStatDayAccessListWithPage(OperatorStatAccessRequest request) {
        if (request == null || request.getStartDate() == null || request.getEndDate() == null
                || request.getStatType() == null || StringUtils.isBlank(request.getAppId()) || request.getSaasEnv() == null) {
            logger.error("查询所有运营商日监控统计数据(分页),输入参数startDate,endDate,statType,appId,saasEnv不能空,request={}", JSON.toJSONString(request));
            throw new ParamCheckerException("请求参数非法");
        }
        logger.info("查询所有运营商日监控统计数据(分页),输入参数request={}", JSON.toJSONString(request));
        List<OperatorAllStatDayAccessRO> result = Lists.newArrayList();
        OperatorStatDayAccessCriteria criteria = new OperatorStatDayAccessCriteria();
        criteria.setOrderByClause("dataTime desc");
        criteria.setLimit(request.getPageSize());
        criteria.setOffset(request.getOffset());
        OperatorStatDayAccessCriteria.Criteria innerCriteria = criteria.createCriteria();
        innerCriteria.andAppIdEqualTo(request.getAppId())
                .andDataTypeEqualTo(request.getStatType())
                .andSaasEnvEqualTo(request.getSaasEnv())
                .andGroupCodeEqualTo(MonitorConstants.VIRTUAL_TOTAL_STAT_OPERATOR)
                .andDataTimeBetween(request.getStartDate(), request.getEndDate());

        long total = operatorStatDayAccessMapper.countByExample(criteria);
        if (total > 0) {
            List<OperatorStatDayAccess> list = operatorStatDayAccessMapper.selectPaginationByExample(criteria);
            for (OperatorStatDayAccess data : list) {
                OperatorAllStatDayAccessRO ro = convert(data, OperatorAllStatDayAccessRO.class);
                ro.setWholeConversionRate(calcRate(data.getEntryCount(), data.getCallbackSuccessCount()));
                ro.setConfirmMobileConversionRate(calcRate(data.getEntryCount(), data.getConfirmMobileCount()));
                ro.setLoginConversionRate(calcRate(data.getConfirmMobileCount(), data.getStartLoginCount()));
                ro.setLoginSuccessRate(calcRate(data.getStartLoginCount(), data.getLoginSuccessCount()));
                ro.setCrawlSuccessRate(calcRate(data.getLoginSuccessCount(), data.getCrawlSuccessCount()));
                ro.setProcessSuccessRate(calcRate(data.getCrawlSuccessCount(), data.getProcessSuccessCount()));
                ro.setCallbackSuccessRate(calcRate(data.getProcessSuccessCount(), data.getCallbackSuccessCount()));
                ro.setTaskUserRatio(calcRatio(data.getUserCount(), data.getTaskCount()));
                result.add(ro);
            }
        }
        logger.info("查询所有运营商日监控统计数据(分页),输出结果result={}", JSON.toJSONString(result));
        return MonitorResultBuilder.pageResult(request, result, total);
    }

    @Override
    public MonitorResult<List<OperatorAllStatAccessRO>> queryAllOperatorStaAccessList(OperatorStatAccessRequest request) {
        if (request == null || request.getStartDate() == null || request.getEndDate() == null || request.getSaasEnv() == null
                || request.getStatType() == null || StringUtils.isBlank(request.getAppId()) || request.getIntervalMins() == null) {
            logger.error("查询所有运营商日监控统计数据(分页),输入参数startDate,endDate,saasEnv,statType,appId,intervalMins不能为空,request={}",
                    JSON.toJSONString(request));
            throw new ParamCheckerException("请求参数非法");
        }
        logger.info("查询所有运营商分时监控统计数据,输入参数request={}", JSON.toJSONString(request));
        List<OperatorAllStatAccessRO> result = Lists.newArrayList();
        OperatorStatAccessCriteria criteria = new OperatorStatAccessCriteria();
        criteria.createCriteria().andAppIdEqualTo(request.getAppId())
                .andDataTypeEqualTo(request.getStatType())
                .andGroupCodeEqualTo(MonitorConstants.VIRTUAL_TOTAL_STAT_OPERATOR)
                .andSaasEnvEqualTo(request.getSaasEnv())
                .andDataTimeBetween(request.getStartDate(), request.getEndDate());

        List<OperatorStatAccess> list = operatorStatAccessMapper.selectByExample(criteria);
        if (CollectionUtils.isEmpty(list)) {
            return MonitorResultBuilder.build(result);
        }
        List<OperatorStatAccessDTO> changeList = this.changeIntervalDataTimeOperatorAllStatAccess(list, request.getIntervalMins());
        changeList = changeList.stream().sorted((o1, o2) -> o2.getDataTime().compareTo(o1.getDataTime())).collect(Collectors.toList());
        for (OperatorStatAccessDTO data : changeList) {
            OperatorAllStatAccessRO ro = convert(data, OperatorAllStatAccessRO.class);
            ro.setTaskUserRatio(calcRatio(data.getUserCount(), data.getTaskCount()));
            ro.setWholeConversionRate(calcRate(data.getEntryCount(), data.getCallbackSuccessCount()));
            result.add(ro);
        }
        return MonitorResultBuilder.build(result);
    }

    @Override
    public MonitorResult<List<OperatorStatAccessRO>> queryOperatorStatAccessListByExample(OperatorStatAccessRequest request) {
        logger.info("查询各个运营商小时监控统计数据,输入参数request={}", JSON.toJSONString(request));
        List<OperatorStatAccessRO> result = Lists.newArrayList();
        OperatorStatAccessCriteria criteria = new OperatorStatAccessCriteria();
        criteria.setOrderByClause("dataTime desc");
        OperatorStatAccessCriteria.Criteria innerCriteria = criteria.createCriteria();
        innerCriteria.andSaasEnvEqualTo((byte) 0);
        if (StringUtils.isNotBlank(request.getAppId())) {
            innerCriteria.andAppIdEqualTo(request.getAppId());
        }
        if (StringUtils.isNotBlank(request.getGroupCode())) {
            innerCriteria.andGroupCodeEqualTo(request.getGroupCode());
        }
        if (request.getStatType() != null) {
            innerCriteria.andDataTypeEqualTo(request.getStatType());
        }
        if (request.getStartDate() != null && request.getEndDate() != null) {
            innerCriteria.andDataTimeGreaterThanOrEqualTo(request.getStartDate()).andDataTimeLessThan(request.getEndDate());
        }
        List<OperatorStatAccess> list = operatorStatAccessMapper.selectByExample(criteria);
        if (CollectionUtils.isEmpty(list)) {
            return MonitorResultBuilder.build(result);
        }
        for (OperatorStatAccess data : list) {
            data.setDataTime(SaasDateUtils.getIntervalDateTime(data.getDataTime(), request.getIntervalMins()));
            OperatorStatAccessRO ro = convert(data, OperatorStatAccessRO.class);
            if (ro != null) {
                ro.setTaskUserRatio(calcRatio(data.getUserCount(), data.getTaskCount()));
                result.add(ro);
            }
        }
        logger.info("查询各个运营商小时监控统计数据,输出结果result={}", JSON.toJSONString(result));
        return MonitorResultBuilder.build(result);

    }


    private List<OperatorStatAccessDTO> changeIntervalDataTimeOperatorAllStatAccess(List<OperatorStatAccess> list, final Integer intervalMins) {
        Map<Date, List<OperatorStatAccess>> map = list.stream().collect(Collectors.groupingBy(data -> SaasDateUtils.getIntervalDateTime(data.getDataTime(), intervalMins)));
        List<OperatorStatAccessDTO> resultList = Lists.newArrayList();
        for (Map.Entry<Date, List<OperatorStatAccess>> entry : map.entrySet()) {
            if (CollectionUtils.isEmpty(entry.getValue())) {
                continue;
            }
            OperatorStatAccessDTO data = new OperatorStatAccessDTO();
            BeanUtils.copyProperties(entry.getValue().get(0), data);
            data.setDataTime(entry.getKey());
            List<OperatorStatAccess> entryList = entry.getValue();
            int userCount = 0, taskCount = 0, entryCount = 0, confirmMobileCount = 0,
                    startLoginCount = 0, loginSuccessCount = 0, crawlSuccessCount = 0,
                    processSuccessCount = 0, callbackSuccessCount = 0;
            for (OperatorStatAccess item : entryList) {
                userCount = userCount + item.getUserCount();
                taskCount = taskCount + item.getTaskCount();
                entryCount = entryCount + item.getEntryCount();
                confirmMobileCount = confirmMobileCount + item.getConfirmMobileCount();
                startLoginCount = startLoginCount + item.getStartLoginCount();
                loginSuccessCount = loginSuccessCount + item.getLoginSuccessCount();
                crawlSuccessCount = crawlSuccessCount + item.getCrawlSuccessCount();
                processSuccessCount = processSuccessCount + item.getProcessSuccessCount();
                callbackSuccessCount = callbackSuccessCount + item.getCallbackSuccessCount();
            }
            data.setUserCount(userCount);
            data.setTaskCount(taskCount);
            data.setEntryCount(entryCount);
            data.setConfirmMobileCount(confirmMobileCount);
            data.setStartLoginCount(startLoginCount);
            data.setLoginSuccessCount(loginSuccessCount);
            data.setCrawlSuccessCount(crawlSuccessCount);
            data.setProcessSuccessCount(processSuccessCount);
            data.setCallbackSuccessCount(callbackSuccessCount);
            data.setConfirmMobileConversionRate(calcRate(entryCount, confirmMobileCount));
            data.setLoginConversionRate(calcRate(confirmMobileCount, startLoginCount));
            data.setLoginSuccessRate(calcRate(startLoginCount, loginSuccessCount));
            data.setCrawlSuccessRate(calcRate(loginSuccessCount, crawlSuccessCount));
            data.setProcessSuccessRate(calcRate(crawlSuccessCount, processSuccessCount));
            data.setCallbackSuccessRate(calcRate(processSuccessCount, callbackSuccessCount));
            resultList.add(data);
        }
        return resultList;
    }

    /**
     * 计算比率
     *
     * @param a 分母
     * @param b 分子
     * @return
     */
    private BigDecimal calcRate(Integer a, Integer b) {
        if (Integer.valueOf(0).compareTo(a) == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal rate = BigDecimal.valueOf(b, 2)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(a, 2), 2, BigDecimal.ROUND_HALF_UP);
        return rate;
    }

    /**
     * 计算比例
     *
     * @param a 分母
     * @param b 分子
     * @return
     */
    private BigDecimal calcRatio(Integer a, Integer b) {
        if (Integer.valueOf(0).compareTo(a) == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal rate = BigDecimal.valueOf(b, 1)
                .divide(BigDecimal.valueOf(a, 1), 1, BigDecimal.ROUND_HALF_UP);
        return rate;
    }


    @Override
    public MonitorResult<Boolean> initHistoryData4OperatorStatAccess(OperatorStatAccessRequest request) {
        Date startDate = request.getStartDate();
        Date endDate = request.getEndDate();
        OperatorAllStatAccessCriteria criteria = new OperatorAllStatAccessCriteria();
        OperatorAllStatAccessCriteria.Criteria innerCriteria = criteria.createCriteria();
        innerCriteria.andAppIdEqualTo(MonitorConstants.VIRTUAL_TOTAL_STAT_APP_ID)
                .andDataTypeEqualTo((byte) 1)
                .andDataTimeBetween(startDate, endDate);
        List<OperatorAllStatAccess> list = operatorAllStatAccessMapper.selectByExample(criteria);
        List<List<OperatorAllStatAccess>> partLists = Lists.partition(list, 50);
        for (List<OperatorAllStatAccess> parts : partLists) {
            List<OperatorStatAccess> resultList = Lists.newArrayList();
            for (OperatorAllStatAccess operatorAllStatAccess : parts) {
                OperatorStatAccess operatorStatAccess = convert(operatorAllStatAccess, OperatorStatAccess.class);
                operatorStatAccess.setGroupCode(MonitorConstants.VIRTUAL_TOTAL_STAT_OPERATOR);
                operatorStatAccess.setGroupName(MonitorConstants.VIRTUAL_TOTAL_STAT_OPERATOR_NAME);
                operatorStatAccess.setSaasEnv((byte) 0);
                resultList.add(operatorStatAccess);
            }
            operatorStatAccessMapper.batchInsert(resultList);
        }


        OperatorAllStatDayAccessCriteria criteriaDay = new OperatorAllStatDayAccessCriteria();
        OperatorAllStatDayAccessCriteria.Criteria innerCriteriaDay = criteriaDay.createCriteria();
        innerCriteriaDay.andAppIdEqualTo(MonitorConstants.VIRTUAL_TOTAL_STAT_APP_ID)
                .andDataTypeEqualTo((byte) 1)
                .andDataTimeBetween(startDate, endDate);
        List<OperatorAllStatDayAccess> listDay = operatorAllStatDayAccessMapper.selectByExample(criteriaDay);
        List<List<OperatorAllStatDayAccess>> dayPartLists = Lists.partition(listDay, 50);
        for (List<OperatorAllStatDayAccess> parts : dayPartLists) {
            List<OperatorStatDayAccess> resultList = Lists.newArrayList();
            for (OperatorAllStatDayAccess operatorAllStatDayAccess : parts) {
                OperatorStatDayAccess operatorStatAccess = convert(operatorAllStatDayAccess, OperatorStatDayAccess.class);
                operatorStatAccess.setGroupCode(MonitorConstants.VIRTUAL_TOTAL_STAT_OPERATOR);
                operatorStatAccess.setGroupName(MonitorConstants.VIRTUAL_TOTAL_STAT_OPERATOR_NAME);
                operatorStatAccess.setSaasEnv((byte) 0);
                resultList.add(operatorStatAccess);
            }
            operatorStatDayAccessMapper.batchInsert(resultList);
        }


        return MonitorResultBuilder.build(true);
    }
}
