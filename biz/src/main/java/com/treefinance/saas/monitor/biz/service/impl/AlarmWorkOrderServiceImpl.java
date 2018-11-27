package com.treefinance.saas.monitor.biz.service.impl;

import com.treefinance.saas.monitor.biz.service.AlarmWorkOrderService;
import com.treefinance.saas.monitor.dao.entity.*;
import com.treefinance.saas.monitor.dao.mapper.AlarmRecordMapper;
import com.treefinance.saas.monitor.dao.mapper.AlarmWorkOrderMapper;
import com.treefinance.saas.monitor.dao.mapper.AsAlarmMapper;
import com.treefinance.saas.monitor.dao.mapper.WorkOrderLogMapper;
import com.treefinance.saas.monitor.facade.domain.ro.AlarmTypeListRO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    @Autowired
    AsAlarmMapper asAlarmMapper;

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
        for (AlarmRecord record:records){
            alarmRecordMapper.updateByPrimaryKey(record);
        }
    }

    @Override
    public AlarmWorkOrder getByRecordId(Long recordId) {
        AlarmWorkOrderCriteria criteria = new AlarmWorkOrderCriteria();

        criteria.createCriteria().andRecordIdEqualTo(recordId);

        List<AlarmWorkOrder> list = alarmWorkOrderMapper.selectByExample(criteria);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public List<AlarmTypeListRO> queryAlarmTypeList() {
        AsAlarmCriteria criteria = new AsAlarmCriteria();
        criteria.setDistinct(true);

        List<AsAlarm> list = asAlarmMapper.selectByExample(criteria);
        List<AlarmTypeListRO> returnList = new ArrayList<>();
        for(AsAlarm asAlarm :list){
            AlarmTypeListRO ro = new AlarmTypeListRO();
            ro.setName(asAlarm.getName());
            ro.setValue(asAlarm.getName());
            returnList.add(ro);
        }

        return returnList;
    }
}
