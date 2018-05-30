package com.treefinance.saas.monitor.biz.service.impl;

import com.treefinance.saas.monitor.biz.service.AlarmRecordService;
import com.treefinance.saas.monitor.common.enumeration.EAlarmLevel;
import com.treefinance.saas.monitor.dao.entity.AlarmRecord;
import com.treefinance.saas.monitor.dao.entity.AlarmRecordCriteria;
import com.treefinance.saas.monitor.dao.entity.AlarmWorkOrder;
import com.treefinance.saas.monitor.dao.entity.WorkOrderLog;
import com.treefinance.saas.monitor.dao.mapper.AlarmRecordMapper;
import com.treefinance.saas.monitor.dao.mapper.AlarmWorkOrderMapper;
import com.treefinance.saas.monitor.dao.mapper.WorkOrderLogMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author chengtong
 * @date 18/5/28 09:41
 */
@Service
public class AlarmRecordServiceImpl implements AlarmRecordService {

    @Resource
    private AlarmRecordMapper alarmRecordMapper;
    @Resource
    private AlarmWorkOrderMapper workOrderMapper;
    @Resource
    private WorkOrderLogMapper workOrderLogMapper;


    @Override
    public void insert(AlarmRecord alarmRecord) {
        alarmRecordMapper.insert(alarmRecord);
    }


    @Override
    public List<AlarmRecord> queryByCondition(AlarmRecordCriteria criteria) {
        return alarmRecordMapper.selectByExample(criteria);
    }

    @Override
    public AlarmRecord getUnProcessedRecord(EAlarmLevel level,String summary){
        AlarmRecordCriteria criteria = new AlarmRecordCriteria();
        criteria.setOrderByClause("dataTime asc");
        criteria.createCriteria().andLevelEqualTo(level.name()).andSummaryEqualTo(summary);
        List<AlarmRecord> records = queryByCondition(criteria);
        if(records.isEmpty()){
            return null;
        }
        return records.get(0);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveAlarmRecords(AlarmWorkOrder order, AlarmRecord record, WorkOrderLog orderLog) {
        alarmRecordMapper.insert(record);
        workOrderLogMapper.insert(orderLog);
        workOrderMapper.insert(order);
    }

    @Override
    public long countByExample(AlarmRecordCriteria criteria) {
        return alarmRecordMapper.countByExample(criteria);
    }


    @Override
    public AlarmRecord getByPrimaryKey(Long id) {
        return alarmRecordMapper.selectByPrimaryKey(id);
    }
}
