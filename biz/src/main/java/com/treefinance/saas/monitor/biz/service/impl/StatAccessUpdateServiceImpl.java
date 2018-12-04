package com.treefinance.saas.monitor.biz.service.impl;

import com.treefinance.saas.monitor.biz.service.StatAccessUpdateService;
import com.treefinance.saas.monitor.common.domain.dto.MerchantStatEcommerceDTO;
import com.treefinance.saas.monitor.common.domain.dto.MerchantStatMailDTO;
import com.treefinance.saas.monitor.common.domain.dto.MerchantStatOperatorDTO;
import com.treefinance.saas.monitor.common.utils.DataConverterUtils;
import com.treefinance.saas.monitor.dao.entity.MerchantStatEcommerce;
import com.treefinance.saas.monitor.dao.entity.MerchantStatMail;
import com.treefinance.saas.monitor.dao.entity.MerchantStatOperator;
import com.treefinance.saas.monitor.dao.mapper.MerchantStatAccessUpdateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by yh-treefinance on 2017/6/22.
 */
@Service("statAccessUpdateService")
public class StatAccessUpdateServiceImpl implements StatAccessUpdateService {
    @Autowired
    private MerchantStatAccessUpdateMapper merchantStatAccessUpdateMapper;


    @Override
    @Transactional(rollbackFor = Exception.class )
    public void batchInsertEcommerce(List<MerchantStatEcommerceDTO> list) {
        List<MerchantStatEcommerce> dataList = DataConverterUtils.convert(list, MerchantStatEcommerce.class);
        dataList.forEach(data -> merchantStatAccessUpdateMapper.insertOrUpdateSelectiveEcommerce(data));
    }

    @Override
    @Transactional(rollbackFor = Exception.class )
    public void batchInsertMail(List<MerchantStatMailDTO> list) {
        List<MerchantStatMail> dataList = DataConverterUtils.convert(list, MerchantStatMail.class);
        dataList.forEach(data -> merchantStatAccessUpdateMapper.insertOrUpdateSelectiveMail(data));
    }

    @Override
    @Transactional(rollbackFor = Exception.class )
    public void batchInsertOperator(List<MerchantStatOperatorDTO> list) {
        List<MerchantStatOperator> dataList = DataConverterUtils.convert(list, MerchantStatOperator.class);
        dataList.forEach(data -> merchantStatAccessUpdateMapper.insertOrUpdateSelectiveOperator(data));
    }

}
