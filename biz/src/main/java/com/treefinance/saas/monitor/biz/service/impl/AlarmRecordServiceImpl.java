package com.treefinance.saas.monitor.biz.service.impl;

import com.treefinance.saas.monitor.biz.service.AlarmRecordService;
import com.treefinance.saas.monitor.common.enumeration.EAlarmLevel;
import com.treefinance.saas.monitor.common.enumeration.EAlarmRecordStatus;
import com.treefinance.saas.monitor.dao.entity.AlarmRecord;
import com.treefinance.saas.monitor.dao.entity.AlarmRecordCriteria;
import com.treefinance.saas.monitor.dao.entity.AlarmWorkOrder;
import com.treefinance.saas.monitor.dao.entity.WorkOrderLog;
import com.treefinance.saas.monitor.dao.mapper.AlarmRecordMapper;
import com.treefinance.saas.monitor.dao.mapper.AlarmWorkOrderMapper;
import com.treefinance.saas.monitor.dao.mapper.WorkOrderLogMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author chengtong
 * @date 18/5/28 09:41
 */
@Service
public class AlarmRecordServiceImpl implements AlarmRecordService {

    private static final Logger logger = LoggerFactory.getLogger(AlarmRecordServiceImpl.class);
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
    public List<AlarmRecord> queryPaginateByCondition(AlarmRecordCriteria criteria) {
        return alarmRecordMapper.selectPaginationByExample(criteria);
    }

    @Override
    public List<AlarmRecord> queryAllUnprocessed(AlarmRecord alarmRecord) {
        AlarmRecordCriteria criteria = new AlarmRecordCriteria();
        criteria.createCriteria().andContentEqualTo(String.valueOf(alarmRecord.getId())).andLevelEqualTo(alarmRecord
                .getLevel()).andSummaryEqualTo(alarmRecord.getSummary()).andIsProcessedEqualTo(EAlarmRecordStatus.UNPROCESS.getCode());

        return alarmRecordMapper.selectByExample(criteria);
    }

    @Override
    public List<AlarmRecord> queryByCondition(AlarmRecordCriteria criteria) {
        return alarmRecordMapper.selectByExample(criteria);
    }

    @Override
    public AlarmRecord getFirstStatusRecord(EAlarmLevel level, String summary, EAlarmRecordStatus status) {
        AlarmRecordCriteria criteria = new AlarmRecordCriteria();
        criteria.setOrderByClause("dataTime asc");
        criteria.setLimit(1);
        criteria.createCriteria().andLevelEqualTo(level.name()).andSummaryEqualTo(summary).andIsProcessedEqualTo(status.getCode());
        List<AlarmRecord> records = queryByCondition(criteria);
        if (records.isEmpty()) {
            return null;
        }
        return records.get(0);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveAlarmRecords(AlarmWorkOrder order, AlarmRecord record, WorkOrderLog orderLog) {
        if (record != null) {
            alarmRecordMapper.insert(record);
        }
        if (orderLog != null) {
            workOrderLogMapper.insert(orderLog);
        }
        if (order != null) {
            workOrderMapper.insert(order);
        }
    }

    @Override
    public long countByExample(AlarmRecordCriteria criteria) {
        return alarmRecordMapper.countByExample(criteria);
    }


    @Override
    public AlarmRecord getByPrimaryKey(Long id) {
        return alarmRecordMapper.selectByPrimaryKey(id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void repairAlarmRecord(AlarmWorkOrder order, AlarmRecord record, WorkOrderLog orderLog) {
        alarmRecordMapper.updateByPrimaryKey(record);

        if (order != null) {
            workOrderMapper.updateByPrimaryKey(order);
        }
        if (orderLog != null) {
            workOrderLogMapper.insert(orderLog);
        }
    }


    @Override
    public List<AlarmRecord> queryTodayErrorList(String bizType, Date startTime, Date endTime, Integer offset, Integer pageSize) {
        return alarmRecordMapper.queryAlarmRecordInBizType(bizType, startTime, endTime, offset, pageSize);
    }

    @Override
    public Integer countAlarmRecordInBizType(String bizType, Date startTime, Date endTime) {
        return alarmRecordMapper.countInBizType(bizType, startTime, endTime);
    }
}
