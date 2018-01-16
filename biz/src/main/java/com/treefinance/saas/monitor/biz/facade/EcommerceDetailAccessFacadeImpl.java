package com.treefinance.saas.monitor.biz.facade;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.monitor.common.utils.DataConverterUtils;
import com.treefinance.saas.monitor.dao.ecommerce.EcommerceDetailAccessDao;
import com.treefinance.saas.monitor.dao.entity.EcommerceAllStatAccess;
import com.treefinance.saas.monitor.facade.domain.request.EcommerceDetailAccessRequest;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResultBuilder;
import com.treefinance.saas.monitor.facade.domain.ro.stat.ecommerce.EcommerceAllDetailRO;
import com.treefinance.saas.monitor.facade.exception.ParamCheckerException;
import com.treefinance.saas.monitor.facade.service.stat.EcommerceDetailAccessFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author:guoguoyun
 * @date:Created in 2018/1/15上午11:16
 */

@Service
public class EcommerceDetailAccessFacadeImpl implements EcommerceDetailAccessFacade {

    private final static Logger logger = LoggerFactory.getLogger(EcommerceDetailAccessFacadeImpl.class);

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
        if (request == null || request.getDataDate() == null || request.getStatType() == null || request.getAppId() == null) {
            logger.error("查询电商日监控分时统计数据,输入参数为空或者dataDate,statType,appId为空,request={}", JSON.toJSONString(request));
            throw new ParamCheckerException("请求参数非法");
        }
        logger.info("查询电商日监控分时统计数据,输入参数request={}", JSON.toJSONString(request));
        Date dataDate = request.getDataDate();
        Byte statType = request.getStatType();
        String appId = request.getAppId();
        List<EcommerceAllDetailRO> result = new ArrayList<>();
        List<EcommerceAllStatAccess> allStatAccessList = ecommerceDetailAccessDao.getEcommerceAllDetailList(dataDate, statType, appId);
        if (CollectionUtils.isEmpty(allStatAccessList)) {
           return  MonitorResultBuilder.build(result);
        }
        for(EcommerceAllStatAccess ecommerceAllStatAccess:allStatAccessList){

            EcommerceAllDetailRO ecommerceAllDetailRO = DataConverterUtils.convert(ecommerceAllStatAccess,EcommerceAllDetailRO.class);
            ecommerceAllDetailRO.setLoginConversionRate(calcRate(ecommerceAllStatAccess.getEntryCount(),ecommerceAllStatAccess.getStartLoginCount()));
            ecommerceAllDetailRO.setLoginSuccessRate(calcRate(ecommerceAllStatAccess.getStartLoginCount(),ecommerceAllStatAccess.getLoginSuccessCount()));
            ecommerceAllDetailRO.setCrawlSuccessRate(calcRate(ecommerceAllStatAccess.getLoginSuccessCount(),ecommerceAllStatAccess.getCrawlSuccessCount()));
            ecommerceAllDetailRO.setProcessSuccessRate(calcRate(ecommerceAllStatAccess.getCrawlSuccessCount(),ecommerceAllStatAccess.getProcessSuccessCount()));
            ecommerceAllDetailRO.setCallbackSuccessRate(calcRate(ecommerceAllStatAccess.getProcessSuccessCount(),ecommerceAllStatAccess.getCallbackSuccessCount()));
            ecommerceAllDetailRO.setTaskUserRatio(calcRatio(ecommerceAllStatAccess.getUserCount(),ecommerceAllStatAccess.getTaskCount()));
            ecommerceAllDetailRO.setWholeConversionRate(calcRate(ecommerceAllStatAccess.getEntryCount(),ecommerceAllStatAccess.getCallbackSuccessCount()));


        }
        logger.info("查询电商日监控分时统计数据,返回结果result={}", JSON.toJSONString(result));

        return MonitorResultBuilder.build(result);
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
