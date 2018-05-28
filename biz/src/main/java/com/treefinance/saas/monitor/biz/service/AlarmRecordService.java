package com.treefinance.saas.monitor.biz.service;

import com.treefinance.saas.monitor.common.enumeration.EAlarmLevel;
import com.treefinance.saas.monitor.dao.entity.AlarmRecord;
import com.treefinance.saas.monitor.dao.entity.AlarmRecordCriteria;
import com.treefinance.saas.monitor.dao.entity.AlarmWorkOrder;
import com.treefinance.saas.monitor.dao.entity.WorkOrderLog;

import java.util.List;

/**
 * @author chengtong
 * @date 18/5/28 09:40
 */
public interface AlarmRecordService {

    void insert(AlarmRecord alarmRecord);

    List<AlarmRecord> selectByExample(AlarmRecordCriteria criteria);

    AlarmRecord getUnProcessedRecord(EAlarmLevel level, String summary);

    void saveAlarmRecords(AlarmWorkOrder order, AlarmRecord record, WorkOrderLog orderLog);

}
