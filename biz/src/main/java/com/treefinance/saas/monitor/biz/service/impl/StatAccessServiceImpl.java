package com.treefinance.saas.monitor.biz.service.impl;

import com.google.common.collect.Lists;
import com.treefinance.saas.monitor.biz.service.StatAccessService;
import com.treefinance.saas.monitor.common.utils.DataConverterUtils;
import com.treefinance.saas.monitor.dao.entity.*;
import com.treefinance.saas.monitor.dao.mapper.*;
import com.treefinance.saas.monitor.facade.domain.request.*;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResultBuilder;
import com.treefinance.saas.monitor.facade.domain.ro.stat.*;
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

    @Override
    public MonitorResult<List<MerchantStatDayAccessRO>> queryDayAccessList(MerchantStatDayAccessRequest request) {
        MerchantStatDayAccessCriteria criteria = new MerchantStatDayAccessCriteria();
        criteria.setOrderByClause("dataTime desc");
        criteria.setLimit(request.getPageSize());
        criteria.setOffset(request.getOffset());
        criteria.createCriteria().andAppIdEqualTo(request.getAppId())
                .andDataTypeEqualTo(request.getDataType())
                .andDataTimeBetween(request.getStartDate(), request.getEndDate());
        int totalCount = merchantStatDayAccessMapper.countByExample(criteria);
        List<MerchantStatDayAccessRO> data = Lists.newArrayList();
        if (totalCount > 0){
            List<MerchantStatDayAccess> list = merchantStatDayAccessMapper.selectByExample(criteria);
            data = DataConverterUtils.convert(list, MerchantStatDayAccessRO.class);
        }
        return MonitorResultBuilder.pageResult(request, data, totalCount);
    }

    @Override
    public MonitorResult<List<MerchantStatAccessRO>> queryAccessList(MerchantStatAccessRequest request) {
        MerchantStatAccessCriteria criteria = new MerchantStatAccessCriteria();
        criteria.setOrderByClause("lastUpdateTime asc");
        criteria.createCriteria().andAppIdEqualTo(request.getAppId())
                .andDataTypeEqualTo(request.getDataType())
                .andDataTimeBetween(request.getStartDate(), request.getEndDate());
        List<MerchantStatAccess> list = merchantStatAccessMapper.selectByExample(criteria);
        List<MerchantStatAccessRO> data = DataConverterUtils.convert(list, MerchantStatAccessRO.class);
        return MonitorResultBuilder.build(data);
    }

    @Override
    public MonitorResult<List<MerchantStatBankRO>> queryBankList(MerchantStatBankRequest request) {
        MerchantStatBankCriteria criteria = new MerchantStatBankCriteria();
        criteria.setOrderByClause("lastUpdateTime asc");
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
        criteria.setOrderByClause("lastUpdateTime asc");
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
        criteria.setOrderByClause("lastUpdateTime asc");
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
        criteria.setOrderByClause("lastUpdateTime asc");
        criteria.createCriteria().andAppIdEqualTo(request.getAppId())
                .andOperaterIdEqualTo(request.getOperaterId())
                .andDataTimeBetween(request.getStartDate(), request.getEndDate());
        List<MerchantStatOperator> list = merchantStatOperatorMapper.selectByExample(criteria);
        List<MerchantStatOperatorRO> data = DataConverterUtils.convert(list, MerchantStatOperatorRO.class);
        return MonitorResultBuilder.build(data);
    }
}