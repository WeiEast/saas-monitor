package com.treefinance.saas.monitor.biz.service.impl;

import com.treefinance.saas.monitor.biz.service.SaasStatAccessUpdateService;
import com.treefinance.saas.monitor.common.domain.dto.SaasStatAccessDTO;
import com.treefinance.saas.monitor.common.domain.dto.SaasStatDayAccessDTO;
import com.treefinance.saas.monitor.common.utils.DataConverterUtils;
import com.treefinance.saas.monitor.dao.entity.SaasStatAccess;
import com.treefinance.saas.monitor.dao.entity.SaasStatDayAccess;
import com.treefinance.saas.monitor.dao.mapper.SaasStatAccessUpdateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by yh-treefinance on 2017/6/22.
 */
@Service("saasStatAccessUpdateService")
public class SaasStatAccessUpdateServiceImpl implements SaasStatAccessUpdateService {
    @Autowired
    private SaasStatAccessUpdateMapper saasStatAccessUpdateMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchInsertStatAccess(List<SaasStatAccessDTO> list) {
        List<SaasStatAccess> dataList = DataConverterUtils.convert(list, SaasStatAccess.class);
        dataList.forEach(data -> saasStatAccessUpdateMapper.insertOrUpdateSelectiveTotal(data));
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchInsertStaDayAccess(List<SaasStatDayAccessDTO> list) {
        List<SaasStatDayAccess> dataList = DataConverterUtils.convert(list, SaasStatDayAccess.class);
        dataList.forEach(data -> saasStatAccessUpdateMapper.insertOrUpdateSelectiveDayTotal(data));
    }

}
