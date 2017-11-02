package com.treefinance.saas.monitor.biz.facade;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.treefinance.saas.monitor.common.utils.DataConverterUtils;
import com.treefinance.saas.monitor.dao.entity.*;
import com.treefinance.saas.monitor.dao.mapper.AllOperatorStatDayAccessMapper;
import com.treefinance.saas.monitor.dao.mapper.OperatorStatAccessMapper;
import com.treefinance.saas.monitor.dao.mapper.OperatorStatDayAccessMapper;
import com.treefinance.saas.monitor.facade.domain.request.OperatorStatAccessRequest;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResultBuilder;
import com.treefinance.saas.monitor.facade.domain.ro.stat.operator.AllOperatorStatDayAccessRO;
import com.treefinance.saas.monitor.facade.domain.ro.stat.operator.OperatorStatAccessRO;
import com.treefinance.saas.monitor.facade.domain.ro.stat.operator.OperatorStatDayAccessRO;
import com.treefinance.saas.monitor.facade.exception.ParamCheckerException;
import com.treefinance.saas.monitor.facade.service.stat.OperatorStatAccessFacade;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author haojiahong
 * @date 2017/11/1
 */
@Service("operatorStatAccessFacade")
public class OperatorStatAccessFacadeImpl implements OperatorStatAccessFacade {

    private final static Logger logger = LoggerFactory.getLogger(OperatorStatAccessFacade.class);

    @Autowired
    private OperatorStatAccessMapper operatorStatAccessMapper;
    @Autowired
    private OperatorStatDayAccessMapper operatorStatDayAccessMapper;
    @Autowired
    private AllOperatorStatDayAccessMapper allOperatorStatDayAccessMapper;


    @Override
    public MonitorResult<List<OperatorStatDayAccessRO>> queryOperatorStatDayAccessList(OperatorStatAccessRequest request) {
        if (request == null || request.getDataDate() == null) {
            logger.error("查询各个运营商日监控统计数据,输入参数为空或者dataDate为空,request={}", JSON.toJSONString(request));
            throw new ParamCheckerException("请求参数非法");
        }
        logger.info("查询各个运营商日监控统计数据,输入参数request={}", JSON.toJSONString(request));
        List<OperatorStatDayAccessRO> result = Lists.newArrayList();

        OperatorStatDayAccessCriteria criteria = new OperatorStatDayAccessCriteria();
        criteria.setOrderByClause("confirmMobileCount desc");
        criteria.createCriteria().andDataTimeEqualTo(request.getDataDate());
        List<OperatorStatDayAccess> list = operatorStatDayAccessMapper.selectByExample(criteria);
        if (!CollectionUtils.isEmpty(list)) {
            result = DataConverterUtils.convert(list, OperatorStatDayAccessRO.class);
        }
        logger.info("查询各个运营商日监控统计数据(分页),返回结果result={}", JSON.toJSONString(result));
        return MonitorResultBuilder.build(result);
    }

    @Override
    public MonitorResult<List<OperatorStatDayAccessRO>> queryOperatorStatDayAccessListWithPage(OperatorStatAccessRequest request) {
        if (request == null || request.getDataDate() == null) {
            logger.error("查询各个运营商日监控统计数据(分页),输入参数为空,request={}", JSON.toJSONString(request));
            throw new ParamCheckerException("请求参数非法");
        }
        logger.info("查询各个运营商日监控统计数据(分页),输入参数request={}", JSON.toJSONString(request));
        List<OperatorStatDayAccessRO> result = Lists.newArrayList();

        OperatorStatDayAccessCriteria criteria = new OperatorStatDayAccessCriteria();
        criteria.setOrderByClause("confirmMobileCount desc");
        criteria.setLimit(request.getPageSize());
        criteria.setOffset(request.getOffset());
        criteria.createCriteria().andDataTimeEqualTo(request.getDataDate());
        long total = operatorStatDayAccessMapper.countByExample(criteria);
        if (total > 0) {
            List<OperatorStatDayAccess> list = operatorStatDayAccessMapper.selectPaginationByExample(criteria);
            result = DataConverterUtils.convert(list, OperatorStatDayAccessRO.class);
        }
        logger.info("查询各个运营商日监控统计数据(分页),返回结果result={}", JSON.toJSONString(result));
        return MonitorResultBuilder.pageResult(request, result, total);
    }

    @Override
    public MonitorResult<List<OperatorStatDayAccessRO>> queryOneOperatorStatDayAccessListWithPage(OperatorStatAccessRequest request) {
        if (request == null || request.getStartDate() == null || request.getEndDate() == null || StringUtils.isBlank(request.getGroupCode())) {
            logger.error("查询某一个运营商日监控统计数据(分页),输入参数为空,request={}", JSON.toJSONString(request));
            throw new ParamCheckerException("请求参数非法");
        }
        logger.info("查询某一个运营商日监控统计数据(分页),输入参数request={}", JSON.toJSONString(request));
        List<OperatorStatDayAccessRO> result = Lists.newArrayList();

        OperatorStatDayAccessCriteria criteria = new OperatorStatDayAccessCriteria();
        criteria.setOrderByClause("dataTime desc");
        criteria.setLimit(request.getPageSize());
        criteria.setOffset(request.getOffset());
        criteria.createCriteria().andDataTimeBetween(request.getStartDate(), request.getEndDate());
        long total = operatorStatDayAccessMapper.countByExample(criteria);
        if (total > 0) {
            List<OperatorStatDayAccess> list = operatorStatDayAccessMapper.selectByExample(criteria);
            result = DataConverterUtils.convert(list, OperatorStatDayAccessRO.class);
        }
        logger.info("查询某一个运营商日监控统计数据(分页),返回结果result={}", JSON.toJSONString(result));
        return MonitorResultBuilder.pageResult(request, result, total);
    }

    @Override
    public MonitorResult<List<OperatorStatAccessRO>> queryOperatorStatAccessList(OperatorStatAccessRequest request) {
        if (request == null || request.getStartDate() == null || request.getEndDate() == null || StringUtils.isBlank(request.getGroupCode())) {
            logger.error("查询各个运营商小时监控统计数据,输入参数为空,request={}", JSON.toJSONString(request));
            throw new ParamCheckerException("请求参数非法");
        }
        logger.info("查询各个运营商小时监控统计数据,输入参数request={}", JSON.toJSONString(request));
        List<OperatorStatAccessRO> result = Lists.newArrayList();
        OperatorStatAccessCriteria criteria = new OperatorStatAccessCriteria();
        criteria.setOrderByClause("dataTime desc");
        criteria.createCriteria().andGroupCodeEqualTo(request.getGroupCode()).andDataTimeBetween(request.getStartDate(), request.getEndDate());
        List<OperatorStatAccess> list = operatorStatAccessMapper.selectByExample(criteria);
        if (!CollectionUtils.isEmpty(list)) {
            result = DataConverterUtils.convert(list, OperatorStatAccessRO.class);
        }
        logger.info("查询各个运营商小时监控统计数据,输出结果result={}", JSON.toJSONString(result));
        return MonitorResultBuilder.build(result);
    }

    @Override
    public MonitorResult<List<AllOperatorStatDayAccessRO>> queryAllOperatorStatDayAccessList(OperatorStatAccessRequest request) {
        if (request == null || request.getStartDate() == null || request.getEndDate() == null) {
            logger.error("查询所有运营商日监控统计数据,输入参数为空,request={}", JSON.toJSONString(request));
            throw new ParamCheckerException("请求参数非法");
        }
        logger.info("查询所有运营商日监控统计数据,输入参数request={}", JSON.toJSONString(request));
        List<AllOperatorStatDayAccessRO> result = Lists.newArrayList();
        AllOperatorStatDayAccessCriteria criteria = new AllOperatorStatDayAccessCriteria();
        criteria.setOrderByClause("dataTime desc");
        criteria.createCriteria().andDataTimeBetween(request.getStartDate(), request.getEndDate());
        List<AllOperatorStatDayAccess> list = allOperatorStatDayAccessMapper.selectByExample(criteria);
        if (!CollectionUtils.isEmpty(list)) {
            result = DataConverterUtils.convert(list, AllOperatorStatDayAccessRO.class);
        }
        logger.info("查询所有运营商日监控统计数据,输出结果result={}", JSON.toJSONString(result));
        return MonitorResultBuilder.build(result);
    }

    @Override
    public MonitorResult<List<AllOperatorStatDayAccessRO>> queryAllOperatorStatDayAccessListWithPage(OperatorStatAccessRequest request) {
        if (request == null || request.getStartDate() == null || request.getEndDate() == null) {
            logger.error("查询所有运营商日监控统计数据(分页),输入参数为空,request={}", JSON.toJSONString(request));
            throw new ParamCheckerException("请求参数非法");
        }
        logger.info("查询所有运营商日监控统计数据(分页),输入参数request={}", JSON.toJSONString(request));
        List<AllOperatorStatDayAccessRO> result = Lists.newArrayList();
        AllOperatorStatDayAccessCriteria criteria = new AllOperatorStatDayAccessCriteria();
        criteria.setOrderByClause("dataTime desc");
        criteria.setLimit(request.getPageSize());
        criteria.setOffset(request.getOffset());
        criteria.createCriteria().andDataTimeBetween(request.getStartDate(), request.getEndDate());
        long total = allOperatorStatDayAccessMapper.countByExample(criteria);
        if (total > 0) {
            List<AllOperatorStatDayAccess> list = allOperatorStatDayAccessMapper.selectPaginationByExample(criteria);
            result = DataConverterUtils.convert(list, AllOperatorStatDayAccessRO.class);
        }
        logger.info("查询所有运营商日监控统计数据(分页),输出结果result={}", JSON.toJSONString(result));
        return MonitorResultBuilder.pageResult(request, result, total);
    }

}
