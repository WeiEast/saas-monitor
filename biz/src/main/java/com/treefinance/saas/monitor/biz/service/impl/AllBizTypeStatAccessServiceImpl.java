package com.treefinance.saas.monitor.biz.service.impl;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.monitor.biz.service.AllBizTypeStatAccessService;
import com.treefinance.saas.monitor.common.constants.AlarmConstants;
import com.treefinance.saas.monitor.common.enumeration.EBizType;
import com.treefinance.saas.monitor.common.enumeration.ESaasEnv;
import com.treefinance.saas.monitor.common.enumeration.ETaskStatDataType;
import com.treefinance.saas.monitor.common.utils.MonitorDateUtils;
import com.treefinance.saas.monitor.dao.entity.*;
import com.treefinance.saas.monitor.dao.mapper.EcommerceAllStatAccessMapper;
import com.treefinance.saas.monitor.dao.mapper.EmailStatAccessMapper;
import com.treefinance.saas.monitor.dao.mapper.MerchantStatDayAccessMapper;
import com.treefinance.saas.monitor.dao.mapper.OperatorStatAccessMapper;
import com.treefinance.saas.monitor.exception.BizException;
import com.treefinance.saas.monitor.facade.domain.ro.AppTaskStatResult;
import com.treefinance.saas.monitor.facade.domain.ro.WholeConversionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author chengtong
 * @date 18/9/11 17:04
 */
@Service
public class AllBizTypeStatAccessServiceImpl implements AllBizTypeStatAccessService {

    private static final Logger logger = LoggerFactory.getLogger(AllBizTypeStatAccessServiceImpl.class);
    private static final BigDecimal HUNDRED = new BigDecimal(100);

    @Autowired
    OperatorStatAccessMapper operatorStatAccessMapper;
    @Autowired
    EmailStatAccessMapper emailStatAccessMapper;
    @Autowired
    EcommerceAllStatAccessMapper ecommerceAllStatAccessMapper;

    @Autowired
    MerchantStatDayAccessMapper merchantStatDayAccessMapper;


    @Override
    public WholeConversionResult calcConversionResult(EBizType bizType, ESaasEnv eSaasEnv) {

        WholeConversionResult result = new WholeConversionResult();

        Date now = new Date();

        Date yesterday = MonitorDateUtils.addTimeUnit(now, Calendar.DATE, -1);

        Date start = MonitorDateUtils.getDayStartTime(MonitorDateUtils.addTimeUnit(now, Calendar.DATE, -7));

        CalculateModel model = new CalculateModel();

        switch (bizType) {
            case EMAIL:
                getEmailBizCalcModel(now, yesterday, start, model);
                break;
            case FUND:
                logger.info("目前不支持的公积金类型数据");
                throw new BizException("not supported bizType");
            case OPERATOR:
                getOperatorBizCalcModel(now, yesterday, start, model, eSaasEnv);
                break;
            case ECOMMERCE:
                getEcommerceBizCalcModel(now, yesterday, start, model, eSaasEnv);
                break;
            default:
                throw new BizException("not supported bizType");
        }

        result.setIsIncrease(model.increase.compareTo(BigDecimal.ZERO) >= 0 ? 1 : 0);
        result.setRateToday(model.rateToday.toString() + "%");
        result.setCompareRate(String.valueOf(Math.abs(model.increase.doubleValue())) + "%");
        result.setRateYesterday(model.rateYesterday.toString() + "%");

        return result;
    }

    private void getEmailBizCalcModel(Date now, Date yesterday, Date start, CalculateModel model) {
        EmailStatAccessCriteria criteria = new EmailStatAccessCriteria();

        criteria.createCriteria().andAppIdEqualTo(AlarmConstants.VIRTUAL_TOTAL_STAT_APP_ID)
                .andDataTypeEqualTo(ETaskStatDataType.USER.getCode()).andDataTimeGreaterThanOrEqualTo(start);

        List<EmailStatAccess> list = emailStatAccessMapper.selectByExample(criteria);

        for (EmailStatAccess emailStatAccess : list) {

            if (MonitorDateUtils.isSameDay(emailStatAccess.getDataTime(), now)) {
                model.succToday += emailStatAccess.getCallbackSuccessCount();
                model.totalToday += emailStatAccess.getEntryCount();
                continue;
            }

            model.succCount += emailStatAccess.getCallbackSuccessCount();
            model.totalCount += emailStatAccess.getEntryCount();

            if (MonitorDateUtils.isSameDay(emailStatAccess.getDataTime(), yesterday)) {
                model.succYesterday += emailStatAccess.getCallbackSuccessCount();
                model.totalYesterday += emailStatAccess.getEntryCount();
            }

        }

        modelPostProcess(model);
    }

    private void getOperatorBizCalcModel(Date now, Date yesterday, Date start, CalculateModel model, ESaasEnv eSaasEnv) {
        OperatorStatAccessCriteria criteria = new OperatorStatAccessCriteria();

        criteria.createCriteria().andAppIdEqualTo(AlarmConstants.VIRTUAL_TOTAL_STAT_APP_ID)
                .andDataTypeEqualTo(ETaskStatDataType.USER.getCode()).andDataTimeGreaterThanOrEqualTo(start).andSaasEnvEqualTo
                ((byte) eSaasEnv.getValue());

        List<OperatorStatAccess> list = operatorStatAccessMapper.selectByExample(criteria);

        for (OperatorStatAccess operatorStatAccess : list) {

            if (MonitorDateUtils.isSameDay(operatorStatAccess.getDataTime(), now)) {
                model.succToday += operatorStatAccess.getCallbackSuccessCount();
                model.totalToday += operatorStatAccess.getEntryCount();
                continue;
            }

            model.succCount += operatorStatAccess.getCallbackSuccessCount();
            model.totalCount += operatorStatAccess.getEntryCount();

            if (MonitorDateUtils.isSameDay(operatorStatAccess.getDataTime(), yesterday)) {
                model.succYesterday += operatorStatAccess.getCallbackSuccessCount();
                model.totalYesterday += operatorStatAccess.getEntryCount();
            }

        }

        modelPostProcess(model);

    }

    private void modelPostProcess(CalculateModel model) {
        if(model.totalToday == 0){
            model.rateToday = BigDecimal.ZERO;
        }else{
            model.rateToday = new BigDecimal(model.succToday).multiply(HUNDRED).divide(new BigDecimal(model
                            .totalToday), 2, RoundingMode.HALF_UP);
        }

        if(model.totalYesterday == 0){
            model.rateYesterday = BigDecimal.ZERO;
        }else{
            model.rateYesterday = new BigDecimal(model.succYesterday).multiply(HUNDRED).divide(new BigDecimal(model
                            .totalYesterday), 2,
                    RoundingMode.HALF_UP);
        }

        if(model.totalCount == 0){
            model.average = BigDecimal.ZERO;
        }else {
            model.average = new BigDecimal(model.succCount).multiply(HUNDRED).divide(new BigDecimal(model.totalCount)
                    , 2,
                    RoundingMode.HALF_UP);
        }

        if(model.average.compareTo(BigDecimal.ZERO) <= 0){
            model.increase = BigDecimal.ZERO;
        }else {
            model.increase = model.rateToday.subtract(model.average).multiply(HUNDRED).divide(model.average, 2,
                    RoundingMode
                    .HALF_UP);
        }

        logger.info("计算工具当前数据model={}", JSON.toJSONString(model));

    }

    private void getEcommerceBizCalcModel(Date now, Date yesterday, Date start, CalculateModel model, ESaasEnv eSaasEnv) {
        EcommerceAllStatAccessCriteria criteria = new EcommerceAllStatAccessCriteria();

        criteria.createCriteria().andAppIdEqualTo(AlarmConstants.VIRTUAL_TOTAL_STAT_APP_ID)
                .andDataTypeEqualTo(ETaskStatDataType.USER.getCode()).andDataTimeGreaterThanOrEqualTo(start).andSaasEnvEqualTo
                ((byte) eSaasEnv.getValue());

        List<EcommerceAllStatAccess> list = ecommerceAllStatAccessMapper.selectByExample(criteria);

        for (EcommerceAllStatAccess operatorStatAccess : list) {

            if (MonitorDateUtils.isSameDay(operatorStatAccess.getDataTime(), now)) {
                model.succToday += operatorStatAccess.getCallbackSuccessCount();
                model.totalToday += operatorStatAccess.getEntryCount();
                continue;
            }

            model.succCount += operatorStatAccess.getCallbackSuccessCount();
            model.totalCount += operatorStatAccess.getEntryCount();


            if (MonitorDateUtils.isSameDay(operatorStatAccess.getDataTime(), yesterday)) {
                model.succYesterday += operatorStatAccess.getCallbackSuccessCount();
                model.totalYesterday += operatorStatAccess.getEntryCount();
            }

        }

        modelPostProcess(model);

    }

    @Override
    public AppTaskStatResult getAppTaskStatResult(EBizType bizType, ESaasEnv eSaasEnv) {

        Date now = new Date();
        Date yesterday = MonitorDateUtils.addTimeUnit(now, Calendar.DATE, -1);

        Date start = MonitorDateUtils.getDayStartTime(MonitorDateUtils.addTimeUnit(now, Calendar.DATE, -7));

        Byte dataType = bizType.getCode();

        AppTaskStatResult appTaskStatResult = new AppTaskStatResult();

        MerchantStatDayAccessCriteria criteria = new MerchantStatDayAccessCriteria();

        criteria.createCriteria().andAppIdEqualTo(AlarmConstants.VIRTUAL_TOTAL_STAT_APP_ID).andDataTypeEqualTo
                (dataType).andSaasEnvEqualTo((byte) eSaasEnv.getValue()).andDataTimeGreaterThanOrEqualTo(start);

        List<MerchantStatDayAccess> list = merchantStatDayAccessMapper.selectByExample(criteria);

        CalculateModel model = new CalculateModel();

        int count = 0;

        for (MerchantStatDayAccess operatorStatAccess : list) {

            if (MonitorDateUtils.isSameDay(operatorStatAccess.getDataTime(), now)) {
                model.totalToday += operatorStatAccess.getTotalCount();
                continue;
            }

            model.totalCount += operatorStatAccess.getTotalCount();
            count++;

            if (MonitorDateUtils.isSameDay(operatorStatAccess.getDataTime(), yesterday)) {
                model.totalYesterday += operatorStatAccess.getTotalCount();
            }
        }
        BigDecimal compare;
        if(count == 0){
            compare = BigDecimal.ZERO;
        }else {
            BigDecimal average = new BigDecimal(model.totalCount).divide(new BigDecimal(count), 4, RoundingMode
                    .HALF_UP);
            compare = new BigDecimal(model.totalToday).subtract(average).divide(average, 4, RoundingMode
                    .HALF_UP).multiply(HUNDRED);
        }

        logger.info("商户任务数的计算model:{},count:{}", model,count);
        logger.info("计算商户任务数据：{}", JSON.toJSONString(model));


        appTaskStatResult.setTaskNumYesterday(model.totalYesterday);
        appTaskStatResult.setTaskNumToday(model.totalToday);
        appTaskStatResult.setIsIncrease(compare.compareTo(BigDecimal.ZERO) >= 0 ? 1 : 0);
        appTaskStatResult.setCompareRate(String.valueOf(Math.abs(compare.doubleValue())+ "%"));

        return appTaskStatResult;
    }

    public static class CalculateModel {

        String name;

        int succCount;

        int totalCount;

        int totalToday;

        int succToday;

        BigDecimal rateToday;

        int totalYesterday;

        int succYesterday;

        BigDecimal rateYesterday;

        BigDecimal average;

        BigDecimal increase;


        public String getName() {
            return name;
        }

        public int getSuccCount() {
            return succCount;
        }

        public int getTotalCount() {
            return totalCount;
        }

        public int getTotalToday() {
            return totalToday;
        }

        public int getSuccToday() {
            return succToday;
        }

        public BigDecimal getRateToday() {
            return rateToday;
        }

        public int getTotalYesterday() {
            return totalYesterday;
        }

        public int getSuccYesterday() {
            return succYesterday;
        }

        public BigDecimal getRateYesterday() {
            return rateYesterday;
        }

        public BigDecimal getAverage() {
            return average;
        }

        public BigDecimal getIncrease() {
            return increase;
        }
    }


}
