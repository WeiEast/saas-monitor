package com.treefinance.saas.monitor.biz.service.impl;

import com.treefinance.saas.monitor.biz.service.AsAlarmService;
import com.treefinance.saas.monitor.dao.entity.AsAlarm;
import com.treefinance.saas.monitor.dao.entity.AsAlarmCriteria;
import com.treefinance.saas.monitor.dao.mapper.AsAlarmMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author:guoguoyun
 * @date:Created in 2018/7/19上午10:57
 */
@Service
public class AsAlarmServiceImpl implements AsAlarmService {

    @Autowired
    AsAlarmMapper asAlarmMapper;

    @Override
    public AsAlarm getAsAlarmByPrimaryKey(long id) {
        return asAlarmMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<AsAlarm> selectPaginationByExample(AsAlarmCriteria criteria) {
        return asAlarmMapper.selectPaginationByExample(criteria);
    }
}
