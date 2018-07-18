package com.treefinance.saas.monitor.biz.service.impl;

import com.treefinance.saas.monitor.biz.service.AsAlarmTriggerRecordService;
import com.treefinance.saas.monitor.dao.entity.AsAlarmTriggerRecord;
import com.treefinance.saas.monitor.dao.mapper.AsAlarmTriggerRecordMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author:guoguoyun
 * @date:Created in 2018/7/18下午8:30
 */
@Service
public class AsAlarmTriggerRecordServiceImpl implements AsAlarmTriggerRecordService {

    private static final Logger logger = LoggerFactory.getLogger(AsAlarmTriggerRecordService.class);
    @Autowired
    AsAlarmTriggerRecordMapper asAlarmTriggerRecordMapper;


    @Override
    public AsAlarmTriggerRecord getAsAlarmTriggerRecordById(long id) {
        return asAlarmTriggerRecordMapper.selectByPrimaryKey(id);

    }
}
