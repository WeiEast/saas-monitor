package com.treefinance.saas.monitor.biz.service;

import com.treefinance.saas.monitor.dao.entity.AlarmRecord;
import com.treefinance.saas.monitor.dao.entity.AlarmWorkOrder;
import com.treefinance.saas.monitor.dao.entity.AlarmWorkOrderCriteria;
import com.treefinance.saas.monitor.dao.entity.WorkOrderLog;
import com.treefinance.saas.monitor.facade.domain.ro.AlarmTypeListRO;

import java.util.List;

/**
 * @author chengtong
 * @date 18/5/30 15:16
 */
public interface AlarmWorkOrderService {

    List<AlarmWorkOrder> queryPaginateByCondition(AlarmWorkOrderCriteria criteria);

    List<AlarmWorkOrder> queryByCondition(AlarmWorkOrderCriteria criteria);

    long countByCondition(AlarmWorkOrderCriteria criteria);

    AlarmWorkOrder getByPrimaryKey(Long id);

    void updateOrder(AlarmWorkOrder order, WorkOrderLog log);

    void updateOrder(AlarmWorkOrder order, WorkOrderLog log, List<AlarmRecord> records);

    AlarmWorkOrder getByRecordId(Long recordId);

    List<AlarmTypeListRO> queryAlarmTypeList();

}
