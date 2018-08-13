package com.treefinance.saas.monitor.facade.service;

import com.treefinance.saas.monitor.facade.domain.request.*;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;
import com.treefinance.saas.monitor.facade.domain.ro.*;

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
     */
    MonitorResult<List<SaasWorkerRO>> querySaasWorker();


    /**
     * 查询saas工作人员的列表
     *
     * @return 数据列表
     */
    MonitorResult<List<SaasWorkerRO>> querySaasWorkerPaginate(SaasWorkerRequest request);


    /**
     * 查询saas工单的列表
     *
     * @return 数据列表
     */
    MonitorResult<List<AlarmWorkOrderRO>> queryAlarmWorkerOrder(WorkOrderRequest recordRequest);



    /**
     * 获取预警记录的列表（增加记录开始时间 持续时间等返回结果）
     *
     * @return 数据列表
     */
    MonitorResult<List<AlarmRecordRO>> queryAlarmListAndhandleMessge(AlarmRecordRequest recordRequest);




    /**
     * 更新工单的接口
     *
     * @param request 数据参数
     */
    MonitorResult<Boolean> updateWorkerOrderProcessor(UpdateWorkOrderRequest request);

    /**
     * 更新工单的接口
     *
     * @param request 数据参数
     */
    MonitorResult<Boolean> updateWorkerOrderStatus(UpdateWorkOrderRequest request);

    MonitorResult<List<WorkOrderLogRO>> queryWorkOrderLog(WorkOrderLogRequest request);

    MonitorResult<List<AlarmTypeListRO>> queryAlarmTypeList();

    /**
     * 获取预警的统计数据
     *
     * @param recordStatRequest 参数
     * @return 列表记录
     */
    MonitorResult<List<AlarmRecordStatisticRO>> queryAlarmStatistic(AlarmRecordStatRequest recordStatRequest);

}
