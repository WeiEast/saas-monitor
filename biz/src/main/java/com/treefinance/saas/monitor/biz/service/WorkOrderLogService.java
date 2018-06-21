package com.treefinance.saas.monitor.biz.service;

import com.treefinance.saas.monitor.dao.entity.WorkOrderLog;
import com.treefinance.saas.monitor.dao.entity.WorkOrderLogCriteria;

import java.util.List;

/**
 * @author chengtong
 * @date 18/5/30 16:10
 */
public interface WorkOrderLogService  {

    List<WorkOrderLog> queryByCondition(WorkOrderLogCriteria criteria);


}
