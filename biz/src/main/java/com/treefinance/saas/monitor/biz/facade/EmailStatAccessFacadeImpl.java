package com.treefinance.saas.monitor.biz.facade;

import com.alibaba.fastjson.JSON;
import com.datatrees.toolkits.util.Objects;
import com.google.common.collect.Lists;
import com.treefinance.saas.monitor.biz.config.DiamondConfig;
import com.treefinance.saas.monitor.common.utils.DataConverterUtils;
import com.treefinance.saas.monitor.common.utils.StatisticCalcUtil;
import com.treefinance.saas.monitor.dao.entity.EmailStatAccess;
import com.treefinance.saas.monitor.dao.entity.EmailStatAccessCriteria;
import com.treefinance.saas.monitor.dao.entity.EmailStatDayAccess;
import com.treefinance.saas.monitor.dao.entity.EmailStatDayAccessCriteria;
import com.treefinance.saas.monitor.dao.mapper.EmailStatAccessMapper;
import com.treefinance.saas.monitor.dao.mapper.EmailStatDayAccessMapper;
import com.treefinance.saas.monitor.facade.domain.request.EmailStatAccessRequest;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResultBuilder;
import com.treefinance.saas.monitor.facade.domain.ro.stat.email.EmailStatAccessBaseRO;
import com.treefinance.saas.monitor.facade.exception.ParamCheckerException;
import com.treefinance.saas.monitor.facade.service.stat.EmailStatAccessFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * @author chengtong
 * @date 18/3/15 10:34
 */
@Service("emailStatAccessFacade")
public class EmailStatAccessFacadeImpl implements EmailStatAccessFacade {

    private static final Logger logger = LoggerFactory.getLogger(EmailStatAccessFacadeImpl.class);

    @Autowired
    private EmailStatDayAccessMapper emailStatDayAccessMapper;
    @Autowired
    private EmailStatAccessMapper emailStatAccessMapper;
    @Autowired
    private DiamondConfig diamondConfig;

    @Override
    public MonitorResult<List<EmailStatAccessBaseRO>> queryEmailStatDayAccessList(EmailStatAccessRequest request) {

        if (Objects.isEmpty(request.getAppId()) || Objects.isEmpty(request.getEmail()) || Objects.isEmpty(request
                .getEndTime()) || Objects.isEmpty(request.getStartTime()) || Objects.isEmpty(request.getStatType())) {
            logger.info("请求邮箱监控数据日表，请求参数不能为空，{}", JSON.toJSONString(request));
            throw new ParamCheckerException("请求参数非法");
        }

        EmailStatDayAccessCriteria criteria = new EmailStatDayAccessCriteria();
        criteria.setLimit(request.getPageSize());
        criteria.setOffset(request.getOffset());
        criteria.createCriteria().andAppIdEqualTo(request.getAppId()).andEmailEqualTo(request.getEmail())
                .andDataTypeEqualTo(request.getStatType()).andDataTimeBetween(request.getStartTime(),request
                .getEndTime());
        List<EmailStatDayAccess> list = emailStatDayAccessMapper.selectPaginationByExample(criteria);

        long total = emailStatDayAccessMapper.countByExample(criteria);

        if(list.isEmpty()){
            return MonitorResultBuilder.pageResult(request, Lists.newArrayList() ,0);
        }

        List<EmailStatAccessBaseRO> result = DataConverterUtils.convert(list, EmailStatAccessBaseRO.class);

        calculateRate(result);


        return MonitorResultBuilder.pageResult(request,result,total);
    }

    @Override
    public MonitorResult<List<EmailStatAccessBaseRO>> queryEmailStatDayAccessListDetail(EmailStatAccessRequest request) {
        if (Objects.isEmpty(request.getAppId()) || Objects.isEmpty(request.getEmail()) || Objects.isEmpty(request
                .getEndTime()) || Objects.isEmpty(request.getStartTime()) || Objects.isEmpty(request.getStatType())) {
            logger.info("请求邮箱监控数据日表，请求参数不能为空，{}", JSON.toJSONString(request));
            throw new ParamCheckerException("请求参数非法");
        }

        EmailStatAccessCriteria criteria = new EmailStatAccessCriteria();


        criteria.setOrderByClause("datatime desc");
        criteria.setLimit(request.getPageSize());
        criteria.setOffset(request.getOffset());

        criteria.createCriteria().andAppIdEqualTo(request.getAppId()).andEmailEqualTo(request.getEmail())
                .andDataTypeEqualTo(request.getStatType()).andDataTimeBetween(request.getStartTime(),request
                .getEndTime());

        List<EmailStatAccess> list = emailStatAccessMapper.selectPaginationByExample(criteria);
        long total = emailStatAccessMapper.countByExample(criteria);
        if(list.isEmpty()){
            return MonitorResultBuilder.pageResult(request, Lists.newArrayList() ,0);
        }

        List<EmailStatAccessBaseRO> result = DataConverterUtils.convert(list, EmailStatAccessBaseRO.class);

        calculateRate(result);

        return MonitorResultBuilder.pageResult(request,result,total);
    }

    private void calculateRate(List<EmailStatAccessBaseRO> result) {
        for (EmailStatAccessBaseRO ro :result){

            ro.setLoginConversionRate(StatisticCalcUtil.calcRate(ro.getStartLoginCount(),ro.getEntryCount()));
            ro.setLoginSuccessRate(StatisticCalcUtil.calcRate(ro.getLoginSuccessCount(),ro.getStartLoginCount()));
            ro.setCrawlSuccessRate(StatisticCalcUtil.calcRate(ro.getCrawlSuccessCount(),ro.getLoginSuccessCount()));
            ro.setProcessSuccessRate(StatisticCalcUtil.calcRate(ro.getProcessSuccessCount(),ro.getCrawlSuccessCount()));
            ro.setCallbackSuccessRate(StatisticCalcUtil.calcRate(ro.getCallbackSuccessCount(),ro.getProcessSuccessCount()));
            ro.setWholeConversionRate(StatisticCalcUtil.calcRate(ro.getCallbackSuccessCount(),ro.getEntryCount()));

            ro.setTaskUserRatio(StatisticCalcUtil.calcRate(ro.getUserCount(),ro.getTaskCount()));
        }
    }

    @Override
    public MonitorResult<List<String>> queryEmailSupportList(EmailStatAccessRequest request) {

        return MonitorResultBuilder.build(Arrays.asList(diamondConfig.getSupportEmails().split(",")));
    }
}
