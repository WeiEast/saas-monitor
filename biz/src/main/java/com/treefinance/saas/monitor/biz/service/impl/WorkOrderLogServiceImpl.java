package com.treefinance.saas.monitor.biz.service.impl;

import com.treefinance.saas.monitor.biz.service.WorkOrderLogService;
import com.treefinance.saas.monitor.dao.entity.WorkOrderLog;
import com.treefinance.saas.monitor.dao.entity.WorkOrderLogCriteria;
import com.treefinance.saas.monitor.dao.mapper.WorkOrderLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author chengtong
 * @date 18/5/30 16:11
 */
@Service
public class WorkOrderLogServiceImpl implements WorkOrderLogService{

    @Autowired
    private WorkOrderLogMapper workOrderLogMapper;

    @Override
    public List<WorkOrderLog> queryByCondition(WorkOrderLogCriteria criteria) {
        return workOrderLogMapper.selectByExample(criteria);
    }
}
