package com.treefinance.saas.monitor.biz.service.impl;

import com.treefinance.saas.monitor.biz.service.AllBizTypeStatAccessService;
import com.treefinance.saas.monitor.common.constants.AlarmConstants;
import com.treefinance.saas.monitor.common.enumeration.EBizType;
import com.treefinance.saas.monitor.common.enumeration.ETaskStatDataType;
import com.treefinance.saas.monitor.common.utils.MonitorDateUtils;
import com.treefinance.saas.monitor.dao.entity.*;
import com.treefinance.saas.monitor.dao.mapper.EcommerceAllStatAccessMapper;
import com.treefinance.saas.monitor.dao.mapper.EmailStatAccessMapper;
import com.treefinance.saas.monitor.dao.mapper.OperatorStatAccessMapper;
import com.treefinance.saas.monitor.exception.BizException;
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

    private static final Logger logger = LoggerFactory.getLogger(OperatorAlarmTemplateImpl.class);

    @Autowired
    OperatorStatAccessMapper operatorStatAccessMapper;
    @Autowired
    EmailStatAccessMapper emailStatAccessMapper;
    @Autowired
    EcommerceAllStatAccessMapper ecommerceAllStatAccessMapper;


    @Override
    public WholeConversionResult calcConversionResult(EBizType bizType) {

        WholeConversionResult result = new WholeConversionResult();

        Date now = new Date();
        Date yesterday = MonitorDateUtils.addTimeUnit(now, Calendar.DATE, -1);

        Date start = MonitorDateUtils.getOClockTime(MonitorDateUtils.addTimeUnit(now, Calendar.DATE,-7 ));

        CalculateModel model = new CalculateModel();

        switch (bizType) {
            case EMAIL:
                getEmailBizCalcModel(now, yesterday, start, model);
                break;
            case FUND:
                logger.info("目前不支持的公积金类型数据");
                throw new BizException("not supported bizType");
            case OPERATOR:
                getOperatorBizCalcModel(now, yesterday, start, model);
                break;
            case ECOMMERCE:
                getEcommerceBizCalcModel(now, yesterday, start, model);
                break;
            default:
                throw new BizException("not supported bizType");
        }

        result.setIsIncrease(model.increase.compareTo(BigDecimal.ZERO)>=0?1:0);
        result.setRateToday(model.rateToday.toString() + "%");
        result.setCompareRate(String.valueOf(Math.abs(model.increase.doubleValue())) + "%");
        result.setRateYesterday(model.rateYesterday.toString() + "%");
        return  result;
    }

    private void getEmailBizCalcModel(Date now, Date yesterday, Date start, CalculateModel model) {
        EmailStatAccessCriteria criteria = new EmailStatAccessCriteria();

        criteria.createCriteria().andAppIdEqualTo(AlarmConstants.VIRTUAL_TOTAL_STAT_APP_ID)
                .andDataTypeEqualTo(ETaskStatDataType.USER.getCode()).andDataTimeGreaterThan(start);

        List<EmailStatAccess> list = emailStatAccessMapper.selectByExample(criteria);

        for(EmailStatAccess emailStatAccess:list){
            model.succCount += emailStatAccess.getCallbackSuccessCount();
            model.totalCount += emailStatAccess.getEntryCount();

            if(MonitorDateUtils.isSameDay(emailStatAccess.getDataTime(), now)){
                model.succToday += emailStatAccess.getCallbackSuccessCount();
                model.totalToday += emailStatAccess.getEntryCount();
            }

            if(MonitorDateUtils.isSameDay(emailStatAccess.getDataTime(), yesterday)){
                model.succYesterday += emailStatAccess.getCallbackSuccessCount();
                model.totalYesterday += emailStatAccess.getEntryCount();
            }

        }

        model.rateToday = new BigDecimal(model.succToday).divide(new BigDecimal(model.totalToday), 2,
                RoundingMode.HALF_UP);
        model.rateYesterday = new BigDecimal(model.succYesterday).divide(new BigDecimal(model.totalYesterday), 2,
                RoundingMode.HALF_UP);
        model.average = new BigDecimal(model.succCount).divide(new BigDecimal(model.totalCount), 2,
                RoundingMode.HALF_UP);
        model.increase = model.rateToday.subtract(model.average).divide(model.average, 2, RoundingMode
                .HALF_UP);
    }

    private void getOperatorBizCalcModel(Date now, Date yesterday, Date start, CalculateModel model) {
        OperatorStatAccessCriteria criteria = new OperatorStatAccessCriteria();

        criteria.createCriteria().andAppIdEqualTo(AlarmConstants.VIRTUAL_TOTAL_STAT_APP_ID)
                .andDataTypeEqualTo(ETaskStatDataType.USER.getCode()).andDataTimeGreaterThan(start);

        List<OperatorStatAccess> list = operatorStatAccessMapper.selectByExample(criteria);

        for(OperatorStatAccess operatorStatAccess:list){
            model.succCount += operatorStatAccess.getCallbackSuccessCount();
            model.totalCount += operatorStatAccess.getEntryCount();

            if(MonitorDateUtils.isSameDay(operatorStatAccess.getDataTime(), now)){
                model.succToday += operatorStatAccess.getCallbackSuccessCount();
                model.totalToday += operatorStatAccess.getEntryCount();
            }

            if(MonitorDateUtils.isSameDay(operatorStatAccess.getDataTime(), yesterday)){
                model.succYesterday += operatorStatAccess.getCallbackSuccessCount();
                model.totalYesterday += operatorStatAccess.getEntryCount();
            }

        }

        model.rateToday = new BigDecimal(model.succToday).divide(new BigDecimal(model.totalToday), 2,
                RoundingMode.HALF_UP);
        model.rateYesterday = new BigDecimal(model.succYesterday).divide(new BigDecimal(model.totalYesterday), 2,
                RoundingMode.HALF_UP);
        model.average = new BigDecimal(model.succCount).divide(new BigDecimal(model.totalCount), 2,
                RoundingMode.HALF_UP);
        model.increase = model.rateToday.subtract(model.average).divide(model.average, 2, RoundingMode
                .HALF_UP);
    }

    private void getEcommerceBizCalcModel(Date now, Date yesterday, Date start, CalculateModel model) {
        EcommerceAllStatAccessCriteria criteria = new EcommerceAllStatAccessCriteria();

        criteria.createCriteria().andAppIdEqualTo(AlarmConstants.VIRTUAL_TOTAL_STAT_APP_ID)
                .andDataTypeEqualTo(ETaskStatDataType.USER.getCode()).andDataTimeGreaterThan(start);

        List<EcommerceAllStatAccess> list = ecommerceAllStatAccessMapper.selectByExample(criteria);

        for(EcommerceAllStatAccess operatorStatAccess:list){
            model.succCount += operatorStatAccess.getCallbackSuccessCount();
            model.totalCount += operatorStatAccess.getEntryCount();

            if(MonitorDateUtils.isSameDay(operatorStatAccess.getDataTime(), now)){
                model.succToday += operatorStatAccess.getCallbackSuccessCount();
                model.totalToday += operatorStatAccess.getEntryCount();
            }

            if(MonitorDateUtils.isSameDay(operatorStatAccess.getDataTime(), yesterday)){
                model.succYesterday += operatorStatAccess.getCallbackSuccessCount();
                model.totalYesterday += operatorStatAccess.getEntryCount();
            }

        }

        model.rateToday = new BigDecimal(model.succToday).divide(new BigDecimal(model.totalToday), 2,
                RoundingMode.HALF_UP);
        model.rateYesterday = new BigDecimal(model.succYesterday).divide(new BigDecimal(model.totalYesterday), 2,
                RoundingMode.HALF_UP);
        model.average = new BigDecimal(model.succCount).divide(new BigDecimal(model.totalCount), 2,
                RoundingMode.HALF_UP);
        model.increase = model.rateToday.subtract(model.average).divide(model.average, 2, RoundingMode
                .HALF_UP);
    }

    public class CalculateModel{

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

    }



}
