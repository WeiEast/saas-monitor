package com.treefinance.saas.monitor.biz.service.impl;

import com.treefinance.saas.monitor.biz.service.OperatorStatAccessUpdateService;
import com.treefinance.saas.monitor.common.domain.dto.OperatorAllStatDayAccessDTO;
import com.treefinance.saas.monitor.common.domain.dto.OperatorStatAccessDTO;
import com.treefinance.saas.monitor.common.domain.dto.OperatorStatDayAccessDTO;
import com.treefinance.saas.monitor.common.utils.DataConverterUtils;
import com.treefinance.saas.monitor.dao.entity.OperatorAllStatDayAccess;
import com.treefinance.saas.monitor.dao.entity.OperatorStatAccess;
import com.treefinance.saas.monitor.dao.entity.OperatorStatDayAccess;
import com.treefinance.saas.monitor.dao.mapper.OperatorAllStatDayAccessMapper;
import com.treefinance.saas.monitor.dao.mapper.OperatorStatAccessMapper;
import com.treefinance.saas.monitor.dao.mapper.OperatorStatDayAccessMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by haojiahong on 2017/11/3.
 */
@Service
public class OperatorStatAccessUpdateServiceImpl implements OperatorStatAccessUpdateService {

    @Autowired
    private OperatorAllStatDayAccessMapper operatorAllStatDayAccessMapper;
    @Autowired
    private OperatorStatDayAccessMapper operatorStatDayAccessMapper;
    @Autowired
    private OperatorStatAccessMapper operatorStatAccessMapper;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchInsertAllOperatorStatDayAccess(List<OperatorAllStatDayAccessDTO> list) {
        List<OperatorAllStatDayAccess> dataList = DataConverterUtils.convert(list, OperatorAllStatDayAccess.class);
        dataList.forEach(data -> operatorAllStatDayAccessMapper.insertOrUpdateBySelective(data));

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchInsertOperatorStatDayAccess(List<OperatorStatDayAccessDTO> list) {
        List<OperatorStatDayAccess> dataList = DataConverterUtils.convert(list, OperatorStatDayAccess.class);
        dataList.forEach(data -> operatorStatDayAccessMapper.insertOrUpdateBySelective(data));

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchInsertOperatorStatAccess(List<OperatorStatAccessDTO> list) {
        List<OperatorStatAccess> dataList = DataConverterUtils.convert(list, OperatorStatAccess.class);
        dataList.forEach(data -> operatorStatAccessMapper.insertOrUpdateBySelective(data));

    }
}
