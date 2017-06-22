package com.treefinance.saas.monitor.biz.service.impl;

import com.treefinance.saas.monitor.biz.service.AccessHistoryService;
import com.treefinance.saas.monitor.common.domain.dto.MerchantAccessHistoryDTO;
import com.treefinance.saas.monitor.common.utils.DataConverterUtils;
import com.treefinance.saas.monitor.dao.entity.MerchantAccessHistory;
import com.treefinance.saas.monitor.dao.mapper.MerchantAccessHistoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by yh-treefinance on 2017/6/22.
 */
@Service
public class AccessHistoryServiceImpl implements AccessHistoryService {
    @Autowired
    private MerchantAccessHistoryMapper merchantAccessHistoryMapper;

    @Override
    public void insertAccessHistory(MerchantAccessHistoryDTO dto) {
        MerchantAccessHistory history = DataConverterUtils.convert(dto, MerchantAccessHistory.class);
        merchantAccessHistoryMapper.insert(history);
    }
}
