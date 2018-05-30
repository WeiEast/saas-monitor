package com.treefinance.saas.monitor.biz.facade;

import com.treefinance.commonservice.uid.UidGenerator;
import com.treefinance.saas.monitor.biz.service.AlarmRecordService;
import com.treefinance.saas.monitor.biz.service.AlarmWorkOrderService;
import com.treefinance.saas.monitor.biz.service.SaasWorkerService;
import com.treefinance.saas.monitor.biz.service.WorkOrderLogService;
import com.treefinance.saas.monitor.common.enumeration.EOrderStatus;
import com.treefinance.saas.monitor.common.utils.DataConverterUtils;
import com.treefinance.saas.monitor.dao.entity.*;
import com.treefinance.saas.monitor.facade.domain.request.AlarmRecordRequest;
import com.treefinance.saas.monitor.facade.domain.request.UpdateWorkOrderRequest;
import com.treefinance.saas.monitor.facade.domain.request.WorkOrderLogRequest;
import com.treefinance.saas.monitor.facade.domain.request.WorkOrderRequest;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResultBuilder;
import com.treefinance.saas.monitor.facade.domain.ro.AlarmRecordRO;
import com.treefinance.saas.monitor.facade.domain.ro.AlarmWorkOrderRO;
import com.treefinance.saas.monitor.facade.domain.ro.SaasWorkerRO;
import com.treefinance.saas.monitor.facade.domain.ro.WorkOrderLogRO;
import com.treefinance.saas.monitor.facade.service.AlarmRecordFacade;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author chengtong
 * @date 18/5/30 14:46
 */
@Service
public class AlarmRecordFacadeImpl implements AlarmRecordFacade {

    private static final Logger logger = LoggerFactory.getLogger(AlarmRecordFacadeImpl.class);
    @Autowired
    private AlarmRecordService alarmRecordService;
    @Autowired
    private SaasWorkerService saasWorkerService;
    @Autowired
    private AlarmWorkOrderService alarmWorkOrderService;
    @Autowired
    private WorkOrderLogService workOrderLogService;

    @Override
    public MonitorResult<List<AlarmRecordRO>> queryAlarmRecord(AlarmRecordRequest recordRequest) {

        AlarmRecordCriteria criteria = new AlarmRecordCriteria();
        AlarmRecordCriteria.Criteria criteriaInner = criteria.createCriteria();

        if (StringUtils.isNotEmpty(recordRequest.getAlarmType())) {
            criteriaInner.andAlarmTypeEqualTo(recordRequest.getAlarmType());
        }
        if (StringUtils.isNotEmpty(recordRequest.getLevel())) {
            criteriaInner.andLevelEqualTo(recordRequest.getLevel());
        }
        if (StringUtils.isNotEmpty(recordRequest.getSummary())) {
            criteriaInner.andSummaryLike(recordRequest.getSummary());
        }
        if (Objects.nonNull(recordRequest.getStartTime())) {
            criteriaInner.andDataTimeGreaterThan(recordRequest.getStartTime());
        }
        if (Objects.nonNull(recordRequest.getEndTime())) {
            criteriaInner.andDataTimeLessThanOrEqualTo(recordRequest.getEndTime());
        }

        criteria.setOrderByClause("dataTime desc,level desc");
        criteria.setLimit(recordRequest.getPageSize());
        criteria.setOffset(recordRequest.getOffset());

        long count = alarmRecordService.countByExample(criteria);

        List<AlarmRecord> list = alarmRecordService.queryByCondition(criteria);

        List<AlarmRecordRO> alarmRecordROList = DataConverterUtils.convert(list, AlarmRecordRO.class);

        return MonitorResultBuilder.pageResult(recordRequest, alarmRecordROList, count);
    }

    @Override
    public MonitorResult<List<SaasWorkerRO>> querySaasWorker() {

        List<SaasWorker> workers = saasWorkerService.getAllSaasWorker();

        List<SaasWorkerRO> saasWorkerROS = DataConverterUtils.convert(workers, SaasWorkerRO.class);

        return MonitorResultBuilder.build(saasWorkerROS);
    }

    @Override
    public MonitorResult<List<AlarmWorkOrderRO>> queryAlarmWorkerOrder(WorkOrderRequest recordRequest) {

        String dutyName = recordRequest.getDutyName();
        String processorName = recordRequest.getProcessorName();
        Long id = recordRequest.getId();
        Date startTime = recordRequest.getStartTime();
        Date endTime = recordRequest.getEndTime();

        AlarmWorkOrderCriteria criteria = new AlarmWorkOrderCriteria();
        AlarmWorkOrderCriteria.Criteria inner = criteria.createCriteria();

        if (null != id) {
            inner.andIdIsLike(String.valueOf(id));
        }

        if (StringUtils.isNotEmpty(dutyName)) {
            inner.andDutyNameLike(dutyName);
        }
        if (StringUtils.isNotEmpty(processorName)) {
            inner.andProcessorNameLike(processorName);
        }
        if (Objects.nonNull(startTime)) {
            inner.andCreateTimeGreaterThan(startTime);
        }
        if (Objects.nonNull(endTime)) {
            inner.andCreateTimeLessThanOrEqualTo(endTime);
        }

        criteria.setLimit(recordRequest.getPageSize());
        criteria.setOffset(recordRequest.getOffset());
        criteria.setOrderByClause("createTime desc");
        long count = alarmWorkOrderService.countByCondition(criteria);


        List<AlarmWorkOrder> list = alarmWorkOrderService.queryByCondition(criteria);

        List<AlarmWorkOrderRO> alarmWorkOrderROS = DataConverterUtils.convert(list, AlarmWorkOrderRO.class);

        return MonitorResultBuilder.pageResult(recordRequest, alarmWorkOrderROS, count);
    }

    @Override
    public MonitorResult<Boolean> updateWorkerOrderProcessor(UpdateWorkOrderRequest request) {
        logger.info("更新工单处理人员");
        AlarmWorkOrder alarmWorkOrder = alarmWorkOrderService.getByPrimaryKey(request.getId());

        if (alarmWorkOrder == null) {
            return MonitorResultBuilder.build("不存在的工单");
        }

        Date now = new Date();

        if (saasWorkerService.getWorkerByName(request.getProcessorName()) == null) {
            logger.error("不存在的工作人员,processName:{}", request.getProcessorName());
            return MonitorResultBuilder.build("不存在的工作人员，" + request.getProcessorName());
        }
        ;

        alarmWorkOrder.setProcessorName(request.getProcessorName());
        alarmWorkOrder.setLastUpdateTime(now);

        WorkOrderLog workOrderLog = new WorkOrderLog();

        workOrderLog.setId(UidGenerator.getId());
        workOrderLog.setOrderId(alarmWorkOrder.getId());
        workOrderLog.setRecordId(alarmWorkOrder.getRecordId());
        workOrderLog.setOpDesc("指定处理人员" + request.getProcessorName());
        workOrderLog.setOpName(alarmWorkOrder.getDutyName());
        workOrderLog.setLastUpdateTime(now);
        workOrderLog.setCreateTime(now);
        try {
            alarmWorkOrderService.updateOrder(alarmWorkOrder, workOrderLog);
        } catch (Exception e) {
            logger.error("更新工单失败");
            logger.error(e.getMessage());
            return MonitorResultBuilder.build(Boolean.FALSE);
        }

        return MonitorResultBuilder.build(Boolean.TRUE);
    }

    @Override
    public MonitorResult<Boolean> updateWorkerOrderStatus(UpdateWorkOrderRequest request) {
        logger.info("更新工单状态");

        EOrderStatus status = EOrderStatus.getByValue(request.getStatus());
        if (status == null) {
            logger.error("不支持的状态，status={}", request.getStatus());
            return MonitorResultBuilder.build("不支持的status字段");
        }
        Date now = new Date();

        AlarmWorkOrder alarmWorkOrder = alarmWorkOrderService.getByPrimaryKey(request.getId());

        if (alarmWorkOrder == null) {
            return MonitorResultBuilder.build("不存在的工单");
        }

        AlarmRecord alarmRecord = alarmRecordService.getByPrimaryKey(alarmWorkOrder.getRecordId());

        if (alarmRecord == null) {
            logger.error("预警记录id：{}不存在", alarmWorkOrder.getRecordId());
            return MonitorResultBuilder.build("不存在的记录");
        }

        AlarmRecordCriteria criteria = new AlarmRecordCriteria();
        criteria.createCriteria().andIsProcessedEqualTo(Boolean.FALSE).andSummaryEqualTo(alarmRecord.getSummary())
                .andContentEqualTo(String.valueOf(alarmRecord.getId()));

        List<AlarmRecord> unProcessedRecords = alarmRecordService.queryByCondition(criteria);
        unProcessedRecords.add(alarmRecord);

        unProcessedRecords.forEach(record -> {
            record.setIsProcessed(Boolean.TRUE);
            record.setLastUpdateTime(now);
        });

        EOrderStatus oldStatus = EOrderStatus.getByValue(alarmWorkOrder.getStatus());

        alarmWorkOrder.setRemark(request.getRemark());
        alarmWorkOrder.setStatus(request.getStatus());
        alarmWorkOrder.setLastUpdateTime(now);

        WorkOrderLog workOrderLog = new WorkOrderLog();

        workOrderLog.setId(UidGenerator.getId());
        workOrderLog.setOrderId(alarmWorkOrder.getId());
        workOrderLog.setRecordId(alarmWorkOrder.getRecordId());
        workOrderLog.setOpDesc(request.getProcessorName() + "处理工单，状态由" + (oldStatus == null ? "未处理" : oldStatus.getDesc()) +
                "变更到" + status.getDesc());
        workOrderLog.setOpName(alarmWorkOrder.getProcessorName());
        workOrderLog.setLastUpdateTime(now);
        workOrderLog.setCreateTime(now);
        try {
            alarmWorkOrderService.updateOrder(alarmWorkOrder, workOrderLog, unProcessedRecords);
        } catch (Exception e) {
            logger.error("更新工单失败");
            logger.error(e.getMessage());
            return MonitorResultBuilder.build(Boolean.FALSE);
        }

        return MonitorResultBuilder.build(Boolean.TRUE);
    }

    @Override
    public MonitorResult<List<WorkOrderLogRO>> queryWorkOrderLog(WorkOrderLogRequest request) {

        Long orderId = request.getOrderId();

        AlarmWorkOrder order = alarmWorkOrderService.getByPrimaryKey(orderId);

        if (order == null) {
            logger.error("id为{}的工单不存在", orderId);
            return MonitorResultBuilder.build("不存在的工单");
        }

        WorkOrderLogCriteria criteria = new WorkOrderLogCriteria();
        criteria.setOrderByClause("createTime desc");

        criteria.createCriteria().andOrderIdEqualTo(orderId);

        List<WorkOrderLog> workOrderLogs = workOrderLogService.queryByCondition(criteria);

        List<WorkOrderLogRO> workOrderLogROs = DataConverterUtils.convert(workOrderLogs, WorkOrderLogRO.class);

        return MonitorResultBuilder.build(workOrderLogROs);
    }


}
