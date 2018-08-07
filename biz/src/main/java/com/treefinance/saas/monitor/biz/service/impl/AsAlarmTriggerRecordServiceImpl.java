package com.treefinance.saas.monitor.biz.service.impl;

import com.treefinance.saas.monitor.biz.service.AsAlarmTriggerRecordService;
import com.treefinance.saas.monitor.dao.entity.AsAlarmTrigger;
import com.treefinance.saas.monitor.dao.entity.AsAlarmTriggerCriteria;
import com.treefinance.saas.monitor.dao.entity.AsAlarmTriggerRecord;
import com.treefinance.saas.monitor.dao.entity.AsAlarmTriggerRecordCriteria;
import com.treefinance.saas.monitor.dao.mapper.AsAlarmTriggerRecordMapper;
import com.treefinance.saas.monitor.facade.domain.request.AlarmExcuteLogRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public List<AsAlarmTriggerRecord> queryAsAlarmTriggerRecordPagination(AlarmExcuteLogRequest alarmExcuteLogRequest) {
        AsAlarmTriggerRecordCriteria asAlarmTriggerRecordCriteria = new AsAlarmTriggerRecordCriteria();
        asAlarmTriggerRecordCriteria.setOrderByClause("runTime desc");
        asAlarmTriggerRecordCriteria.setOffset(alarmExcuteLogRequest.getOffset());
        asAlarmTriggerRecordCriteria.setLimit(alarmExcuteLogRequest.getPageSize());

        if (alarmExcuteLogRequest.getStartDate() == null && alarmExcuteLogRequest.getEndDate() == null) {
            asAlarmTriggerRecordCriteria.createCriteria().andAlarmIdEqualTo(alarmExcuteLogRequest.getId());

        } else {
            asAlarmTriggerRecordCriteria.createCriteria().andAlarmIdEqualTo(alarmExcuteLogRequest.getId()).andRunTimeBetween(alarmExcuteLogRequest.getStartDate(), alarmExcuteLogRequest.getEndDate());
        }


        List<AsAlarmTriggerRecord> asAlarmTriggerRecordList = asAlarmTriggerRecordMapper.selectByExample(asAlarmTriggerRecordCriteria);


        return asAlarmTriggerRecordList;

    }


    @Override
    public List<AsAlarmTriggerRecord> queryAsAlarmTriggerRecord(AlarmExcuteLogRequest alarmExcuteLogRequest) {
        AsAlarmTriggerRecordCriteria asAlarmTriggerRecordCriteria = new AsAlarmTriggerRecordCriteria();

        if (alarmExcuteLogRequest.getStartDate() == null && alarmExcuteLogRequest.getEndDate() == null) {
            asAlarmTriggerRecordCriteria.createCriteria().andAlarmIdEqualTo(alarmExcuteLogRequest.getId());

        } else {
            asAlarmTriggerRecordCriteria.createCriteria().andAlarmIdEqualTo(alarmExcuteLogRequest.getId()).andRunTimeBetween(alarmExcuteLogRequest.getStartDate(), alarmExcuteLogRequest.getEndDate());
        }


        List<AsAlarmTriggerRecord> asAlarmTriggerRecordList = asAlarmTriggerRecordMapper.selectByExample(asAlarmTriggerRecordCriteria);


        return asAlarmTriggerRecordList;

    }
}
