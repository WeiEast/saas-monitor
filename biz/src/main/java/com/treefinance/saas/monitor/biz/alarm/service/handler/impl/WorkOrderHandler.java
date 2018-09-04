package com.treefinance.saas.monitor.biz.alarm.service.handler.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.treefinance.commonservice.uid.UidGenerator;
import com.treefinance.saas.monitor.biz.alarm.model.AlarmConfig;
import com.treefinance.saas.monitor.biz.alarm.model.AlarmContext;
import com.treefinance.saas.monitor.biz.alarm.model.AlarmMessage;
import com.treefinance.saas.monitor.biz.alarm.service.handler.AlarmHandler;
import com.treefinance.saas.monitor.biz.alarm.service.handler.Order;
import com.treefinance.saas.monitor.biz.service.AlarmRecordService;
import com.treefinance.saas.monitor.biz.service.AlarmWorkOrderService;
import com.treefinance.saas.monitor.biz.service.SaasWorkerService;
import com.treefinance.saas.monitor.common.enumeration.EAlarmRecordStatus;
import com.treefinance.saas.monitor.common.enumeration.EOrderStatus;
import com.treefinance.saas.monitor.dao.entity.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by yh-treefinance on 2018/8/29.
 */
@Order(6)
@Component
public class WorkOrderHandler implements AlarmHandler {
    /**
     * logger
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private SaasWorkerService saasWorkerService;
    @Autowired
    private AlarmRecordService alarmRecordService;
    @Autowired
    protected AlarmWorkOrderService alarmWorkOrderService;

    @Override
    public void handle(AlarmConfig config, AlarmContext context) {
        // 预警接受人
        List<SaasWorker> saasWorkers = saasWorkerService.getAllSaasWorker();
        Map<Long, String> triggerNameMap = config.getAlarmTriggers().stream()
                .collect(Collectors.toMap(AsAlarmTrigger::getId, AsAlarmTrigger::getName));
        // 消息发送完成，生产预警工单
        Date now = new Date();

        //获取值班人员
        Long alarmId = config.getAlarm().getId();
        if (alarmId == null) {
            logger.info(" work-order : alarm id is empty： config={}", JSON.toJSONString(config));
            return;
        }
        List<SaasWorker> activeWorkers = saasWorkerService.getActiveWorkers(context.getAlarmTime(), saasWorkers);
        String dutyNames = Joiner.on(",").join(activeWorkers.stream().map(SaasWorker::getName).collect(Collectors.toList()));
        context.getTriggerRecords().stream().filter(triggerRecord -> StringUtils.isNotEmpty(triggerRecord.getAlarmLevel()))
                .forEach(triggerRecord -> {
                    Long recordId = triggerRecord.getId();

                    AlarmRecord alarmRecord = new AlarmRecord();
                    alarmRecord.setId(recordId);
                    alarmRecord.setCreateTime(now);
                    alarmRecord.setDataTime(triggerRecord.getRunTime());
                    alarmRecord.setIsProcessed(EAlarmRecordStatus.UNPROCESS.getCode());
                    alarmRecord.setTriggerId(triggerRecord.getConditionId());
                    alarmRecord.setLevel(triggerRecord.getAlarmLevel());
                    alarmRecord.setSummary(triggerNameMap.get(triggerRecord.getConditionId()));
                    alarmRecord.setAlarmType(config.getAlarm().getName());
                    alarmRecord.setContent(triggerRecord.getAlarmMessage());
                    alarmRecord.setStartTime(now);

                    AlarmWorkOrder workOrder = new AlarmWorkOrder();
                    workOrder.setCreateTime(now);
                    workOrder.setDutyName(dutyNames);
                    workOrder.setId(UidGenerator.getId());
                    workOrder.setLastUpdateTime(now);
                    workOrder.setRecordId(recordId);
                    workOrder.setProcessorName("system");
                    workOrder.setStatus(EAlarmRecordStatus.UNPROCESS.getCode());
                    workOrder.setRemark("创建操作工单");

                    WorkOrderLog orderLog = new WorkOrderLog();
                    orderLog.setId(UidGenerator.getId());
                    orderLog.setOrderId(workOrder.getId());
                    orderLog.setRecordId(recordId);
                    orderLog.setOpName("system");
                    orderLog.setOpDesc("创建操作工单");
                    orderLog.setCreateTime(now);
                    orderLog.setLastUpdateTime(now);
                    try {
                        alarmRecordService.saveAlarmRecords(workOrder, alarmRecord, orderLog);
                    } finally {
                        logger.info("create new alarm work-order：workorder={},record={},log={}", JSON.toJSONString(workOrder), JSON.toJSONString(alarmRecord), JSON.toJSONString(orderLog));
                    }
                });
        // 预警记录-预警编号
        Map<Long, Long> alarmNoMap = Maps.newHashMap();
        for (AlarmMessage alarmMessage : context.getAlaramMessageList()) {
            alarmNoMap.put(alarmMessage.getRecordId(), alarmMessage.getAlarmNo());
        }
        // 消息发送完成，未处理的工单恢复工单
        List<Long> triggerIds = context.getTriggerRecords().stream().filter(triggerRecord -> Boolean.TRUE.toString().equalsIgnoreCase(triggerRecord.getRecoveryTrigger()))
                .map(AsAlarmTriggerRecord::getConditionId).distinct().collect(Collectors.toList());
        // 获取未处理的工单列表
        if (CollectionUtils.isEmpty(triggerIds)) {
            logger.info("no recover work-order： config={}", JSON.toJSONString(config));
            return;
        }

        AlarmRecordCriteria criteria = new AlarmRecordCriteria();
        criteria.createCriteria().andTriggerIdIn(triggerIds).andIsProcessedEqualTo(EAlarmRecordStatus.UNPROCESS.getCode());
        List<AlarmRecord> alarmRecords = alarmRecordService.queryByCondition(criteria);
        logger.info("handle unprocess work-order: triggerIds={},alarmRecords={}", JSON.toJSONString(triggerIds), JSON.toJSONString(alarmRecords));
        alarmRecords.stream().forEach(alarmRecord -> {
            try {
                Long recordId = alarmRecord.getId();
                alarmRecord.setEndTime(now);
                alarmRecord.setIsProcessed(EAlarmRecordStatus.REPAIRED.getCode());

                AlarmWorkOrder alarmWorkOrder = alarmWorkOrderService.getByRecordId(recordId);
                if(alarmWorkOrder != null){
                    alarmWorkOrder.setLastUpdateTime(now);
                    alarmWorkOrder.setStatus(EOrderStatus.REPAIRED.getCode());
                    alarmWorkOrder.setRemark("系统判定修复");
                    alarmWorkOrder.setProcessorName("system");
                }

                WorkOrderLog workOrderLog = new WorkOrderLog();
                workOrderLog.setId(UidGenerator.getId());
                workOrderLog.setOrderId(alarmWorkOrder.getId());
                workOrderLog.setRecordId(recordId);
                workOrderLog.setOpDesc("系统判定预警恢复");
                workOrderLog.setOpName("system");
                workOrderLog.setLastUpdateTime(now);
                workOrderLog.setCreateTime(now);
                try {
                    alarmRecordService.repairAlarmRecord(alarmWorkOrder, alarmRecord, workOrderLog);
                } finally {
                    logger.info("系统判定恢复：work-order={},record={},log={}", JSON.toJSONString(alarmWorkOrder), JSON.toJSONString(alarmRecord), JSON.toJSONString(workOrderLog));
                }
            } catch (Exception e) {
                logger.error("repair work-order error：alarmRecord={}", JSON.toJSONString(alarmRecord),e);
            }
        });
    }
}
