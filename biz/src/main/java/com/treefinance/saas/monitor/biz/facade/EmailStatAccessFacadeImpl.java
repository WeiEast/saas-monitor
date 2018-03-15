package com.treefinance.saas.monitor.biz.facade;

import com.alibaba.fastjson.JSON;
import com.datatrees.toolkits.util.Objects;
import com.google.common.collect.Lists;
import com.treefinance.saas.monitor.common.utils.DataConverterUtils;
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

import java.util.List;

/**
 * @author chengtong
 * @date 18/3/15 10:34
 */
public class EmailStatAccessFacadeImpl implements EmailStatAccessFacade {

    private static final Logger logger = LoggerFactory.getLogger(EmailStatAccessFacadeImpl.class);

    @Autowired
    private EmailStatDayAccessMapper emailStatDayAccessMapper;
    @Autowired
    private EmailStatAccessMapper emailStatAccessMapper;

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
        List<EmailStatDayAccess> list = emailStatDayAccessMapper.selectByExample(criteria);

        if(list.isEmpty()){
            return MonitorResultBuilder.pageResult(request, Lists.newArrayList() ,0);
        }

        List<EmailStatAccessBaseRO> result = DataConverterUtils.convert(list, EmailStatAccessBaseRO.class);


        return MonitorResultBuilder.pageResult(request,result,result.size());
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
        criteria.createCriteria().andAppIdEqualTo(request.getAppId()).andEmailEqualTo(request.getEmail())
                .andDataTypeEqualTo(request.getStatType()).andDataTimeBetween(request.getStartTime(),request
                .getEndTime());

        List<EmailStatAccess> list = emailStatAccessMapper.selectByExample(criteria);

        if(list.isEmpty()){
            return MonitorResultBuilder.pageResult(request, Lists.newArrayList() ,0);
        }

        List<EmailStatAccessBaseRO> result = DataConverterUtils.convert(list, EmailStatAccessBaseRO.class);

        return MonitorResultBuilder.pageResult(request,result,result.size());
    }
}
