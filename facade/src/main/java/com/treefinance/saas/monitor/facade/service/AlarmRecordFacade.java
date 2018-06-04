package com.treefinance.saas.monitor.facade.service;

import com.treefinance.saas.monitor.facade.domain.request.AlarmRecordRequest;
import com.treefinance.saas.monitor.facade.domain.request.UpdateWorkOrderRequest;
import com.treefinance.saas.monitor.facade.domain.request.WorkOrderLogRequest;
import com.treefinance.saas.monitor.facade.domain.request.WorkOrderRequest;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;
import com.treefinance.saas.monitor.facade.domain.ro.AlarmRecordRO;
import com.treefinance.saas.monitor.facade.domain.ro.AlarmWorkOrderRO;
import com.treefinance.saas.monitor.facade.domain.ro.SaasWorkerRO;
import com.treefinance.saas.monitor.facade.domain.ro.WorkOrderLogRO;

import java.util.List;

/**
 * @author chengtong
 * @date 18/5/30 11:16
 */
public interface AlarmRecordFacade {


    /**
     * 查询预警记录的列表
     *
     * @param recordRequest 参数
     * @return 数据列表
     */
    MonitorResult<List<AlarmRecordRO>> queryAlarmRecord(AlarmRecordRequest recordRequest);


    /**
     * 查询saas工作人员的列表
     *
     * @return 数据列表
     * */
    MonitorResult<List<SaasWorkerRO>> querySaasWorker();

    /**
     * 查询saas工单的列表
     *
     * @return 数据列表
     * */
    MonitorResult<List<AlarmWorkOrderRO>> queryAlarmWorkerOrder(WorkOrderRequest recordRequest);

    /**
     * 更新工单的接口
     * @param request 数据参数
     *
     * */
    MonitorResult<Boolean> updateWorkerOrderProcessor(UpdateWorkOrderRequest request);

    /**
     * 更新工单的接口
     * @param request 数据参数
     *
     * */
    MonitorResult<Boolean> updateWorkerOrderStatus(UpdateWorkOrderRequest request);




    MonitorResult<List<WorkOrderLogRO>> queryWorkOrderLog(WorkOrderLogRequest request);
}
