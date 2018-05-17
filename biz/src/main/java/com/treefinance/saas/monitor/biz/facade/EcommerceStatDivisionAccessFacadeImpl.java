package com.treefinance.saas.monitor.biz.facade;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.treefinance.saas.monitor.common.domain.dto.EcommerceTimeShareDTO;
import com.treefinance.saas.monitor.common.utils.DataConverterUtils;
import com.treefinance.saas.monitor.common.utils.MonitorDateUtils;
import com.treefinance.saas.monitor.dao.ecommerce.EcommerceDetailAccessDao;
import com.treefinance.saas.monitor.dao.entity.EcommerceAllStatAccess;
import com.treefinance.saas.monitor.dao.entity.EcommerceAllStatDayAccess;
import com.treefinance.saas.monitor.facade.domain.request.EcommerceDetailAccessRequest;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResultBuilder;
import com.treefinance.saas.monitor.facade.domain.ro.stat.ecommerce.EcommerceAllDetailRO;
import com.treefinance.saas.monitor.facade.service.stat.EcommerceStatDivisionAccessFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author:guoguoyun
 * @date:Created in 2018/1/15上午11:16
 */


@Service("ecommerceStatDivisionAccessFacade")
public class EcommerceStatDivisionAccessFacadeImpl implements EcommerceStatDivisionAccessFacade {

    private final static Logger logger = LoggerFactory.getLogger(EcommerceStatDivisionAccessFacadeImpl.class);

    @Autowired
    EcommerceDetailAccessDao ecommerceDetailAccessDao;


    /**
     * 查询电商总分时监控统计数据
     *
     * @param request
     * @return
     */
    @Override
    public MonitorResult<List<EcommerceAllDetailRO>> queryEcommerceAllDetailAccessList(EcommerceDetailAccessRequest request) {
        if (request == null || request.getDataDate() == null || request.getStatType() == null || request.getSourceType() == null) {
            logger.error("查询电商日监控分时统计数据,输入参数为空或者dataDate,statType,sourceType,appId为空,request={}", JSON.toJSONString(request));
            return new MonitorResult("传入参数为空");
        }
        logger.info("查询电商日监控分时统计数据,输入参数request={}", JSON.toJSONString(request));

        List<EcommerceAllDetailRO> result = new ArrayList<>();
        EcommerceTimeShareDTO ecommerceTimeShareDTO = DataConverterUtils.convert(request, EcommerceTimeShareDTO.class);
        List<EcommerceAllStatAccess> allStatAccessList = ecommerceDetailAccessDao.getEcommerceAllDetailList(ecommerceTimeShareDTO);

        if (CollectionUtils.isEmpty(allStatAccessList)) {
            return MonitorResultBuilder.build(result);
        }
        //调用changeIntervalDataTimeEcommerceAllStatAccess，进行按给定的分钟段统计数据
        List<EcommerceAllStatAccess> changeList = this.changeIntervalDataTimeEcommerceAllStatAccess(allStatAccessList, request.getIntervalMins());
        changeList = changeList.stream().sorted((o1, o2) -> o2.getDataTime().compareTo(o1.getDataTime())).collect(Collectors.toList());

        for (EcommerceAllStatAccess ecommerceAllStatAccess : changeList) {

            EcommerceAllDetailRO ecommerceAllDetailRO = DataConverterUtils.convert(ecommerceAllStatAccess, EcommerceAllDetailRO.class);
            ecommerceAllDetailRO.setOneClickLoginConversionRate(calcRate(ecommerceAllStatAccess.getEntryCount(), ecommerceAllStatAccess.getOneClickLoginCount()));
            ecommerceAllDetailRO.setLoginConversionRate(calcRate(ecommerceAllStatAccess.getOneClickLoginCount(), ecommerceAllStatAccess.getStartLoginCount()));
            ecommerceAllDetailRO.setLoginSuccessRate(calcRate(ecommerceAllStatAccess.getStartLoginCount(), ecommerceAllStatAccess.getLoginSuccessCount()));
            ecommerceAllDetailRO.setCrawlSuccessRate(calcRate(ecommerceAllStatAccess.getLoginSuccessCount(), ecommerceAllStatAccess.getCrawlSuccessCount()));
            ecommerceAllDetailRO.setProcessSuccessRate(calcRate(ecommerceAllStatAccess.getCrawlSuccessCount(), ecommerceAllStatAccess.getProcessSuccessCount()));
            ecommerceAllDetailRO.setCallbackSuccessRate(calcRate(ecommerceAllStatAccess.getProcessSuccessCount(), ecommerceAllStatAccess.getCallbackSuccessCount()));
            ecommerceAllDetailRO.setTaskUserRatio(calcRatio(ecommerceAllStatAccess.getUserCount(), ecommerceAllStatAccess.getTaskCount()));
            ecommerceAllDetailRO.setWholeConversionRate(calcRate(ecommerceAllStatAccess.getEntryCount(), ecommerceAllStatAccess.getCallbackSuccessCount()));
            result.add(ecommerceAllDetailRO);


        }


        return MonitorResultBuilder.build(result);
    }

    private List<EcommerceAllStatAccess> changeIntervalDataTimeEcommerceAllStatAccess(List<EcommerceAllStatAccess> list, final Integer intervalMins) {
        Map<Date, List<EcommerceAllStatAccess>> map = list.stream().collect(Collectors.groupingBy(data -> MonitorDateUtils.getIntervalDateTime(data.getDataTime(), intervalMins)));
        List<EcommerceAllStatAccess> resultList = Lists.newArrayList();
        for (Map.Entry<Date, List<EcommerceAllStatAccess>> entry : map.entrySet()) {
            if (CollectionUtils.isEmpty(entry.getValue())) {
                continue;
            }
            EcommerceAllStatAccess data = new EcommerceAllStatAccess();
            BeanUtils.copyProperties(entry.getValue().get(0), data);
            data.setDataTime(entry.getKey());
            List<EcommerceAllStatAccess> entryList = entry.getValue();
            int userCount = 0, taskCount = 0, entryCount = 0, oneClickLoginCount = 0,
                    startLoginCount = 0, loginSuccessCount = 0, crawlSuccessCount = 0,
                    processSuccessCount = 0, callbackSuccessCount = 0;
            for (EcommerceAllStatAccess item : entryList) {
                userCount = userCount + item.getUserCount();
                taskCount = taskCount + item.getTaskCount();
                entryCount = entryCount + item.getEntryCount();
                oneClickLoginCount = oneClickLoginCount + item.getOneClickLoginCount();
                startLoginCount = startLoginCount + item.getStartLoginCount();
                loginSuccessCount = loginSuccessCount + item.getLoginSuccessCount();
                crawlSuccessCount = crawlSuccessCount + item.getCrawlSuccessCount();
                processSuccessCount = processSuccessCount + item.getProcessSuccessCount();
                callbackSuccessCount = callbackSuccessCount + item.getCallbackSuccessCount();
            }
            data.setUserCount(userCount);
            data.setTaskCount(taskCount);
            data.setEntryCount(entryCount);
            data.setOneClickLoginCount(oneClickLoginCount);
            data.setStartLoginCount(startLoginCount);
            data.setLoginSuccessCount(loginSuccessCount);
            data.setCrawlSuccessCount(crawlSuccessCount);
            data.setProcessSuccessCount(processSuccessCount);
            data.setCallbackSuccessCount(callbackSuccessCount);
            resultList.add(data);
        }
        return resultList;
    }


    /**
     * 查询电商整体监控统计数据(分页)
     *
     * @param request
     * @return
     */
    @Override
    public MonitorResult<List<EcommerceAllDetailRO>> queryEcommerceAllAccessList(EcommerceDetailAccessRequest request) {
        if (request == null || request.getStartDate() == null || request.getEndDate() == null || request.getStatType() == null) {
            logger.error("查询电商日监控整体统计数据,输入参数为空或者startDate,endDate,statType,request={}", JSON.toJSONString(request));
            return new MonitorResult("传入参数为空");
        }
        logger.info("查询电商日监控整体统计数据,输入参数request={}", JSON.toJSONString(request));

        List<EcommerceAllDetailRO> result = new ArrayList<>();
        EcommerceTimeShareDTO ecommerceTimeShareDTO = DataConverterUtils.convert(request, EcommerceTimeShareDTO.class);

        long total = ecommerceDetailAccessDao.countByExample(ecommerceTimeShareDTO);
        if (total > 0) {
            List<EcommerceAllStatDayAccess> allStatAccessList = ecommerceDetailAccessDao.getEcommerceAllList(ecommerceTimeShareDTO);


            for (EcommerceAllStatDayAccess ecommerceAllStatDayAccess : allStatAccessList) {

                EcommerceAllDetailRO ecommerceAllDetailRO = DataConverterUtils.convert(ecommerceAllStatDayAccess, EcommerceAllDetailRO.class);
                ecommerceAllDetailRO.setOneClickLoginConversionRate(calcRate(ecommerceAllStatDayAccess.getEntryCount(), ecommerceAllStatDayAccess.getOneClickLoginCount()));
                ecommerceAllDetailRO.setLoginConversionRate(calcRate(ecommerceAllStatDayAccess.getOneClickLoginCount(), ecommerceAllStatDayAccess.getStartLoginCount()));
                ecommerceAllDetailRO.setLoginSuccessRate(calcRate(ecommerceAllStatDayAccess.getStartLoginCount(), ecommerceAllStatDayAccess.getLoginSuccessCount()));
                ecommerceAllDetailRO.setCrawlSuccessRate(calcRate(ecommerceAllStatDayAccess.getLoginSuccessCount(), ecommerceAllStatDayAccess.getCrawlSuccessCount()));
                ecommerceAllDetailRO.setProcessSuccessRate(calcRate(ecommerceAllStatDayAccess.getCrawlSuccessCount(), ecommerceAllStatDayAccess.getProcessSuccessCount()));
                ecommerceAllDetailRO.setCallbackSuccessRate(calcRate(ecommerceAllStatDayAccess.getProcessSuccessCount(), ecommerceAllStatDayAccess.getCallbackSuccessCount()));
                ecommerceAllDetailRO.setTaskUserRatio(calcRatio(ecommerceAllStatDayAccess.getUserCount(), ecommerceAllStatDayAccess.getTaskCount()));
                ecommerceAllDetailRO.setWholeConversionRate(calcRate(ecommerceAllStatDayAccess.getEntryCount(), ecommerceAllStatDayAccess.getCallbackSuccessCount()));
                result.add(ecommerceAllDetailRO);


            }
        }


        return MonitorResultBuilder.pageResult(request, result, total);


    }


//    /**
//     * 查询电商详细分时监控统计数据
//     *
//     * @param request
//     * @return
//     */
//    MonitorResult<List<EcommerceAllDetailRO>> queryEcommerceEachDetailAccessList(EcommerceDetailAccessRequest request) {
//
//        if (request == null || request.getStartDate() == null || request.getEndDate() == null || request.getGroupCode() == null || request.getAppId() == null ||
//                request.getDataDate() == null || request.getStatType() == null) {
//            logger.error("查询电商日监控分时分段条件统计数据,输入参数为空或者startDate,endDate,groupCode,statType,appId为空,request={}", JSON.toJSONString(request));
//            throw new ParamCheckerException("请求参数非法");
//        }
//        logger.info("查询电商日监控分时分段条件统计数据,输入参数request={}", JSON.toJSONString(request));
//        Date startDate = request.getStartDate();
//        Date endDate =  request.getEndDate();
//        String groupCode = request.getGroupCode();
//        Byte statType = request.getStatType();
//        String appId = request.getAppId();
//        List<EcommerceAllDetailRO> result = new ArrayList<>();
//        List<EcommerceAllStatAccess> allStatAccessList = ecommerceDetailAccessDao.getEcommerceEachDetailList(startDate,endDate,groupCode, statType, appId);
//        if (!CollectionUtils.isEmpty(allStatAccessList)) {
//            result = DataConverterUtils.convert(allStatAccessList, EcommerceAllDetailRO.class);
//        }
//        logger.info("查询电商日监控分时分段条件统计数据(分页),返回结果result={}", JSON.toJSONString(result));
//
//        return MonitorResultBuilder.build(result);
//
//    }

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


}
