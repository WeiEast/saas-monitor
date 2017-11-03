package com.treefinance.saas.monitor.biz.service.impl;

import com.treefinance.saas.monitor.biz.service.OperatorStatAccessUpdateService;
import com.treefinance.saas.monitor.common.domain.dto.AllOperatorStatDayAccessDTO;
import com.treefinance.saas.monitor.common.domain.dto.OperatorStatAccessDTO;
import com.treefinance.saas.monitor.common.domain.dto.OperatorStatDayAccessDTO;
import com.treefinance.saas.monitor.common.utils.DataConverterUtils;
import com.treefinance.saas.monitor.dao.entity.AllOperatorStatDayAccess;
import com.treefinance.saas.monitor.dao.entity.OperatorStatAccess;
import com.treefinance.saas.monitor.dao.entity.OperatorStatDayAccess;
import com.treefinance.saas.monitor.dao.mapper.AllOperatorStatDayAccessMapper;
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
    private AllOperatorStatDayAccessMapper allOperatorStatDayAccessMapper;
    @Autowired
    private OperatorStatDayAccessMapper operatorStatDayAccessMapper;
    @Autowired
    private OperatorStatAccessMapper operatorStatAccessMapper;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchInsertAllOperatorStatDayAccess(List<AllOperatorStatDayAccessDTO> list) {
        List<AllOperatorStatDayAccess> dataList = DataConverterUtils.convert(list, AllOperatorStatDayAccess.class);
        dataList.forEach(data -> allOperatorStatDayAccessMapper.insertOrUpdateBySelective(data));

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
