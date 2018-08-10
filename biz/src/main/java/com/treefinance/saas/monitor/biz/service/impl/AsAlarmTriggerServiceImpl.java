package com.treefinance.saas.monitor.biz.service.impl;

import com.treefinance.saas.monitor.biz.service.AsAlarmTriggerService;
import com.treefinance.saas.monitor.dao.entity.AsAlarmTrigger;
import com.treefinance.saas.monitor.dao.entity.AsAlarmTriggerCriteria;
import com.treefinance.saas.monitor.dao.mapper.AsAlarmTriggerMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @author:guoguoyun
 * @date:Created in 2018/7/18下午8:16
 */
@Service
public class AsAlarmTriggerServiceImpl implements AsAlarmTriggerService {
    private static final Logger logger = LoggerFactory.getLogger(AsAlarmTriggerService.class);

    @Autowired
    private AsAlarmTriggerMapper asAlarmTriggerMapper;

    @Override
    public List<AsAlarmTrigger> getAsAlarmTriggerByPrimaryKey(List<Long> ids) {

        AsAlarmTriggerCriteria asAlarmTriggerCriteria = new AsAlarmTriggerCriteria();
        asAlarmTriggerCriteria.createCriteria().andIdIn(ids);
        return   asAlarmTriggerMapper.selectByExample(asAlarmTriggerCriteria);
    }
}
