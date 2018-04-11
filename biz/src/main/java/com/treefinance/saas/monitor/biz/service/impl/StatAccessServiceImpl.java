package com.treefinance.saas.monitor.biz.service.impl;

import com.google.common.collect.Lists;
import com.treefinance.saas.monitor.biz.service.StatAccessService;
import com.treefinance.saas.monitor.common.constants.MonitorConstants;
import com.treefinance.saas.monitor.common.utils.DataConverterUtils;
import com.treefinance.saas.monitor.dao.entity.*;
import com.treefinance.saas.monitor.dao.mapper.*;
import com.treefinance.saas.monitor.facade.domain.request.*;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResultBuilder;
import com.treefinance.saas.monitor.facade.domain.ro.stat.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by yh-treefinance on 2017/6/2.
 */
@Service("statAccessService")
public class StatAccessServiceImpl implements StatAccessService {

    @Autowired
    private MerchantStatDayAccessMapper merchantStatDayAccessMapper;
    @Autowired
    private MerchantStatAccessMapper merchantStatAccessMapper;
    @Autowired
    private MerchantStatBankMapper merchantStatBankMapper;
    @Autowired
    private MerchantStatEcommerceMapper merchantStatEcommerceMapper;
    @Autowired
    private MerchantStatMailMapper merchantStatMailMapper;
    @Autowired
    private MerchantStatOperatorMapper merchantStatOperatorMapper;
    @Autowired
    private SaasErrorStepDayStatMapper saasErrorStepDayStatMapper;

    @Override
    public MonitorResult<List<MerchantStatDayAccessRO>> queryDayAccessList(MerchantStatDayAccessRequest request) {
        MerchantStatDayAccessCriteria criteria = new MerchantStatDayAccessCriteria();
        criteria.setOrderByClause("dataTime desc");
        criteria.setLimit(request.getPageSize());
        criteria.setOffset(request.getOffset());
        criteria.createCriteria().andAppIdEqualTo(request.getAppId())
                .andDataTypeEqualTo(request.getDataType())
                .andDataTimeBetween(request.getStartDate(), request.getEndDate());
        long totalCount = merchantStatDayAccessMapper.countByExample(criteria);
        List<MerchantStatDayAccessRO> data = Lists.newArrayList();
        if (totalCount > 0) {
            List<MerchantStatDayAccess> list = merchantStatDayAccessMapper.selectByExample(criteria);
            data = DataConverterUtils.convert(list, MerchantStatDayAccessRO.class);
        }
        return MonitorResultBuilder.pageResult(request, data, totalCount);
    }

    @Override
    public MonitorResult<List<MerchantStatDayAccessRO>> queryDayAccessListNoPage(MerchantStatDayAccessRequest request) {
        MerchantStatDayAccessCriteria criteria = new MerchantStatDayAccessCriteria();
        criteria.setOrderByClause("dataTime desc");
        criteria.createCriteria().andAppIdEqualTo(request.getAppId())
                .andDataTypeEqualTo(request.getDataType())
                .andDataTimeBetween(request.getStartDate(), request.getEndDate());
        List<MerchantStatDayAccess> list = merchantStatDayAccessMapper.selectByExample(criteria);
        List<MerchantStatDayAccessRO> data = DataConverterUtils.convert(list, MerchantStatDayAccessRO.class);
        return MonitorResultBuilder.build(data);
    }

    @Override
    public MonitorResult<List<MerchantStatDayAccessRO>> queryAllDayAccessList(MerchantStatDayAccessRequest request) {
        MerchantStatDayAccessCriteria criteria = new MerchantStatDayAccessCriteria();
        criteria.setOrderByClause("dataTime desc");
        criteria.setLimit(request.getPageSize());
        criteria.setOffset(request.getOffset());
        criteria.createCriteria().andDataTypeEqualTo(request.getDataType())
                .andDataTimeBetween(request.getStartDate(), request.getEndDate());
        long totalCount = merchantStatDayAccessMapper.countByExample(criteria);
        List<MerchantStatDayAccessRO> data = Lists.newArrayList();
        if (totalCount > 0) {
            List<MerchantStatDayAccess> list = merchantStatDayAccessMapper.selectByExample(criteria);
            data = DataConverterUtils.convert(list, MerchantStatDayAccessRO.class);
        }
        return MonitorResultBuilder.pageResult(request, data, totalCount);
    }

    @Override
    public MonitorResult<List<MerchantStatDayAccessRO>> queryAllDayAccessListNoPage(MerchantStatDayAccessRequest request) {
        MerchantStatDayAccessCriteria criteria = new MerchantStatDayAccessCriteria();
        criteria.setOrderByClause("dataTime desc");
        MerchantStatDayAccessCriteria.Criteria innerCriteria = criteria.createCriteria();
        innerCriteria.andDataTypeEqualTo(request.getDataType())
                .andDataTimeBetween(request.getStartDate(), request.getEndDate());
        if (request.getSaasEnv() != null) {
            innerCriteria.andSaasEnvEqualTo(request.getSaasEnv());
        }
        List<MerchantStatDayAccess> list = merchantStatDayAccessMapper.selectByExample(criteria);
        List<MerchantStatDayAccessRO> data = DataConverterUtils.convert(list, MerchantStatDayAccessRO.class);
        return MonitorResultBuilder.build(data);
    }

    @Override
    public MonitorResult<List<MerchantStatAccessRO>> queryAccessList(MerchantStatAccessRequest request) {
        MerchantStatAccessCriteria criteria = new MerchantStatAccessCriteria();
        criteria.setOrderByClause("dataTime asc");
        criteria.createCriteria().andAppIdEqualTo(request.getAppId())
                .andDataTypeEqualTo(request.getDataType())
                .andDataTimeBetween(request.getStartDate(), request.getEndDate());
        List<MerchantStatAccess> list = merchantStatAccessMapper.selectByExample(criteria);
        List<MerchantStatAccessRO> data = DataConverterUtils.convert(list, MerchantStatAccessRO.class);
        return MonitorResultBuilder.build(data);
    }

    @Override
    public MonitorResult<List<MerchantStatAccessRO>> queryAllAccessList(MerchantStatAccessRequest request) {
        MerchantStatAccessCriteria criteria = new MerchantStatAccessCriteria();
        criteria.setOrderByClause("dataTime asc");
        MerchantStatAccessCriteria.Criteria innerCriteria = criteria.createCriteria();
        innerCriteria.andDataTypeEqualTo(request.getDataType())
                .andDataTimeBetween(request.getStartDate(), request.getEndDate());
        if (request.getSaasEnv() != null) {
            innerCriteria.andSaasEnvEqualTo(request.getSaasEnv());
        }
        if (StringUtils.isNotBlank(request.getAppId())) {
            innerCriteria.andAppIdEqualTo(MonitorConstants.VIRTUAL_TOTAL_STAT_APP_ID);
        }
        List<MerchantStatAccess> list = merchantStatAccessMapper.selectByExample(criteria);
        List<MerchantStatAccessRO> data = DataConverterUtils.convert(list, MerchantStatAccessRO.class);
        return MonitorResultBuilder.build(data);
    }

    @Override
    public MonitorResult<List<MerchantStatBankRO>> queryBankList(MerchantStatBankRequest request) {
        MerchantStatBankCriteria criteria = new MerchantStatBankCriteria();
        criteria.setOrderByClause("dataTime asc");
        criteria.createCriteria().andAppIdEqualTo(request.getAppId())
                .andBankIdEqualTo(request.getBankId())
                .andDataTimeBetween(request.getStartDate(), request.getEndDate());
        List<MerchantStatBank> list = merchantStatBankMapper.selectByExample(criteria);

        List<MerchantStatBankRO> data = DataConverterUtils.convert(list, MerchantStatBankRO.class);
        return MonitorResultBuilder.build(data);
    }

    @Override
    public MonitorResult<List<MerchantStatEcommerceRO>> queryEcommerceList(MerchantStatEcommerceRequest request) {
        MerchantStatEcommerceCriteria criteria = new MerchantStatEcommerceCriteria();
        criteria.setOrderByClause("dataTime asc");
        criteria.createCriteria().andAppIdEqualTo(request.getAppId())
                .andEcommerceIdEqualTo(request.getEcommerceId())
                .andDataTimeBetween(request.getStartDate(), request.getEndDate());
        List<MerchantStatEcommerce> list = merchantStatEcommerceMapper.selectByExample(criteria);

        List<MerchantStatEcommerceRO> data = DataConverterUtils.convert(list, MerchantStatEcommerceRO.class);
        return MonitorResultBuilder.build(data);
    }

    @Override
    public MonitorResult<List<MerchantStatMailRO>> queryMailList(MerchantStatMailRequest request) {
        MerchantStatMailCriteria criteria = new MerchantStatMailCriteria();
        criteria.setOrderByClause("dataTime asc");
        criteria.createCriteria().andAppIdEqualTo(request.getAppId())
                .andMailCodeEqualTo(request.getMailCode())
                .andDataTimeBetween(request.getStartDate(), request.getEndDate());
        List<MerchantStatMail> list = merchantStatMailMapper.selectByExample(criteria);

        List<MerchantStatMailRO> data = DataConverterUtils.convert(list, MerchantStatMailRO.class);
        return MonitorResultBuilder.build(data);
    }

    @Override
    public MonitorResult<List<MerchantStatOperatorRO>> queryOperatorList(MerchantStatOperaterRequest request) {
        MerchantStatOperatorCriteria criteria = new MerchantStatOperatorCriteria();
        criteria.setOrderByClause("dataTime asc");
        MerchantStatOperatorCriteria.Criteria operatorCriteria = criteria.createCriteria();
        operatorCriteria.andAppIdEqualTo(request.getAppId())
                .andDataTimeBetween(request.getStartDate(), request.getEndDate());
        if (StringUtils.isNotBlank(request.getOperaterId())) {
            operatorCriteria.andOperaterIdEqualTo(request.getOperaterId());
        }
        List<MerchantStatOperator> list = merchantStatOperatorMapper.selectByExample(criteria);

        List<MerchantStatOperatorRO> data = Lists.newArrayList();
        if (CollectionUtils.isEmpty(list)) {
            return MonitorResultBuilder.build(data);
        }
        data = DataConverterUtils.convert(list, MerchantStatOperatorRO.class);
        return MonitorResultBuilder.build(data);
    }

    @Override
    public MonitorResult<List<SaasErrorStepDayStatRO>> querySaasErrorStepDayStatListNoPage(SaasErrorStepDayStatRequest request) {
        SaasErrorStepDayStatCriteria criteria = new SaasErrorStepDayStatCriteria();
        criteria.setOrderByClause("dataTime desc");
        criteria.createCriteria().andDataTypeEqualTo(request.getDataType())
                .andDataTimeBetween(request.getStartDate(), request.getEndDate());
        List<SaasErrorStepDayStat> list = saasErrorStepDayStatMapper.selectByExample(criteria);
        List<SaasErrorStepDayStatRO> data = DataConverterUtils.convert(list, SaasErrorStepDayStatRO.class);
        return MonitorResultBuilder.build(data);
    }
}
