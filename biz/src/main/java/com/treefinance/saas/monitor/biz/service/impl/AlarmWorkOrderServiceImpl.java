package com.treefinance.saas.monitor.biz.service.impl;

import com.treefinance.saas.monitor.biz.service.AlarmWorkOrderService;
import com.treefinance.saas.monitor.dao.entity.AlarmRecord;
import com.treefinance.saas.monitor.dao.entity.AlarmWorkOrder;
import com.treefinance.saas.monitor.dao.entity.AlarmWorkOrderCriteria;
import com.treefinance.saas.monitor.dao.entity.WorkOrderLog;
import com.treefinance.saas.monitor.dao.mapper.AlarmRecordMapper;
import com.treefinance.saas.monitor.dao.mapper.AlarmWorkOrderMapper;
import com.treefinance.saas.monitor.dao.mapper.WorkOrderLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author chengtong
 * @date 18/5/30 15:18
 */
@Service
public class AlarmWorkOrderServiceImpl implements AlarmWorkOrderService {

    @Autowired
    AlarmWorkOrderMapper alarmWorkOrderMapper;
    @Autowired
    WorkOrderLogMapper workOrderLogMapper;
    @Autowired
    AlarmRecordMapper alarmRecordMapper;

    @Override
    public List<AlarmWorkOrder> queryByCondition(AlarmWorkOrderCriteria criteria) {
        return alarmWorkOrderMapper.selectByExample(criteria);
    }

    @Override
    public List<AlarmWorkOrder> queryPaginateByCondition(AlarmWorkOrderCriteria criteria) {
        return alarmWorkOrderMapper.selectPaginationByExample(criteria);
    }

    @Override
    public long countByCondition(AlarmWorkOrderCriteria criteria) {

        return alarmWorkOrderMapper.countByExample(criteria);
    }

    @Override
    public AlarmWorkOrder getByPrimaryKey(Long id) {
        return alarmWorkOrderMapper.selectByPrimaryKey(id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateOrder(AlarmWorkOrder order, WorkOrderLog log) {
        alarmWorkOrderMapper.updateByPrimaryKey(order);
        workOrderLogMapper.insert(log);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOrder(AlarmWorkOrder order, WorkOrderLog log, List<AlarmRecord> records) {
        alarmWorkOrderMapper.updateByPrimaryKey(order);
        workOrderLogMapper.insert(log);
        alarmRecordMapper.batchUpdateByPrimaryKey(records);
    }

    @Override
    public AlarmWorkOrder getByRecordId(Long recordId) {
        AlarmWorkOrderCriteria criteria = new AlarmWorkOrderCriteria();

        criteria.createCriteria().andRecordIdEqualTo(recordId);

        List<AlarmWorkOrder> list = alarmWorkOrderMapper.selectByExample(criteria);
        if (list.isEmpty()) return null;
        return list.get(0);
    }
}
