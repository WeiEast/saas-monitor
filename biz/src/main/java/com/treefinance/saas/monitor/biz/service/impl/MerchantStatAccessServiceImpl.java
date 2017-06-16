package com.treefinance.saas.monitor.biz.service.impl;

import com.treefinance.saas.monitor.biz.service.MerchantStatAccessService;
import com.treefinance.saas.monitor.common.domain.dto.*;
import com.treefinance.saas.monitor.common.utils.DataConverterUtils;
import com.treefinance.saas.monitor.dao.entity.*;
import com.treefinance.saas.monitor.dao.mapper.*;
import com.treefinance.saas.monitor.facade.domain.request.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by yh-treefinance on 2017/6/2.
 */
@Service("merchantStatAccessService")
public class MerchantStatAccessServiceImpl implements MerchantStatAccessService {
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
    public List<MerchantStatAccessDTO> queryAccessList(MerchantStatAccessRequest request) {
        MerchantStatAccessCriteria criteria = new MerchantStatAccessCriteria();
        criteria.setOrderByClause("lastUpdateTime asc");
        criteria.createCriteria().andAppIdEqualTo(request.getAppId())
                .andDataTypeEqualTo(request.getDataType())
                .andDataTimeBetween(request.getStartDate(), request.getEndDate());
        List<MerchantStatAccess> list = merchantStatAccessMapper.selectByExample(criteria);
        return DataConverterUtils.convert(list, MerchantStatAccessDTO.class);
    }

    @Override
    public List<MerchantStatBankDTO> queryBankList(MerchantStatBankRequest request) {
        MerchantStatBankCriteria criteria = new MerchantStatBankCriteria();
        criteria.setOrderByClause("lastUpdateTime asc");
        criteria.createCriteria().andAppIdEqualTo(request.getAppId())
                .andBankIdEqualTo(request.getBankId())
                .andDataTimeBetween(request.getStartDate(), request.getEndDate());
        List<MerchantStatBank> list = merchantStatBankMapper.selectByExample(criteria);
        return DataConverterUtils.convert(list, MerchantStatBankDTO.class);
    }

    @Override
    public List<MerchantStatEcommerceDTO> queryEcommerceList(MerchantStatEcommerceRequest request) {
        MerchantStatEcommerceCriteria criteria = new MerchantStatEcommerceCriteria();
        criteria.setOrderByClause("lastUpdateTime asc");
        criteria.createCriteria().andAppIdEqualTo(request.getAppId())
                .andEcommerceIdEqualTo(request.getEcommerceId())
                .andDataTimeBetween(request.getStartDate(), request.getEndDate());
        List<MerchantStatEcommerce> list = merchantStatEcommerceMapper.selectByExample(criteria);
        return DataConverterUtils.convert(list, MerchantStatEcommerceDTO.class);
    }

    @Override
    public List<MerchantStatMailDTO> queryMailList(MerchantStatMailRequest request) {
        MerchantStatMailCriteria criteria = new MerchantStatMailCriteria();
        criteria.setOrderByClause("lastUpdateTime asc");
        criteria.createCriteria().andAppIdEqualTo(request.getAppId())
                .andMailCodeEqualTo(request.getMailCode())
                .andDataTimeBetween(request.getStartDate(), request.getEndDate());
        List<MerchantStatMail> list = merchantStatMailMapper.selectByExample(criteria);
        return DataConverterUtils.convert(list, MerchantStatMailDTO.class);
    }

    @Override
    public List<MerchantStatOperatorDTO> queryOperatorList(MerchantStatOperaterRequest request) {
        MerchantStatOperatorCriteria criteria = new MerchantStatOperatorCriteria();
        criteria.setOrderByClause("lastUpdateTime asc");
        criteria.createCriteria().andAppIdEqualTo(request.getAppId())
                .andOperaterIdEqualTo(request.getOperaterId())
                .andDataTimeBetween(request.getStartDate(), request.getEndDate());
        List<MerchantStatOperator> list = merchantStatOperatorMapper.selectByExample(criteria);
        return DataConverterUtils.convert(list, MerchantStatOperatorDTO.class);
    }


    @Override
    public void batchInsertStatAccess(List<MerchantStatAccessDTO> list) {
        List<MerchantStatAccess> dataList = DataConverterUtils.convert(list, MerchantStatAccess.class);
        dataList.forEach(data -> merchantStatAccessMapper.insertSelective(data));
    }

    @Override
    public void batchInsertBankList(List<MerchantStatBankDTO> list) {
        List<MerchantStatBank> dataList = DataConverterUtils.convert(list, MerchantStatBank.class);
        dataList.forEach(data -> merchantStatBankMapper.insertSelective(data));
    }

    @Override
    public void batchInsertEcommerce(List<MerchantStatEcommerceDTO> list) {
        List<MerchantStatEcommerce> dataList = DataConverterUtils.convert(list, MerchantStatEcommerce.class);
        dataList.forEach(data -> merchantStatEcommerceMapper.insertSelective(data));
    }

    @Override
    public void batchInsertMail(List<MerchantStatMailDTO> list) {
        List<MerchantStatMail> dataList = DataConverterUtils.convert(list, MerchantStatMail.class);
        dataList.forEach(data -> merchantStatMailMapper.insertSelective(data));
    }

    @Override
    public void batchInsertOperator(List<MerchantStatOperatorDTO> list) {
        List<MerchantStatOperator> dataList = DataConverterUtils.convert(list, MerchantStatOperator.class);
        dataList.forEach(data -> merchantStatOperatorMapper.insertSelective(data));
    }

}
