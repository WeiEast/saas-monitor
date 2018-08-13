package com.treefinance.saas.monitor.biz.facade;

import com.google.common.collect.Maps;
import com.treefinance.commonservice.uid.UidGenerator;
import com.treefinance.saas.monitor.biz.autostat.utils.CronUtils;
import com.treefinance.saas.monitor.biz.config.DiamondConfig;
import com.treefinance.saas.monitor.biz.config.EmailAlarmConfig;
import com.treefinance.saas.monitor.biz.event.AlarmClearEvent;
import com.treefinance.saas.monitor.biz.event.OrderDelegateEvent;
import com.treefinance.saas.monitor.biz.helper.StatHelper;
import com.treefinance.saas.monitor.biz.service.AlarmRecordService;
import com.treefinance.saas.monitor.biz.service.AlarmWorkOrderService;
import com.treefinance.saas.monitor.biz.service.SaasWorkerService;
import com.treefinance.saas.monitor.biz.service.WorkOrderLogService;
import com.treefinance.saas.monitor.common.constants.AlarmConstants;
import com.treefinance.saas.monitor.common.enumeration.EAlarmLevel;
import com.treefinance.saas.monitor.common.enumeration.EAlarmRecordStatus;
import com.treefinance.saas.monitor.common.enumeration.EAlarmType;
import com.treefinance.saas.monitor.common.enumeration.EOrderStatus;
import com.treefinance.saas.monitor.common.utils.DataConverterUtils;
import com.treefinance.saas.monitor.common.utils.MonitorDateUtils;
import com.treefinance.saas.monitor.dao.entity.*;
import com.treefinance.saas.monitor.facade.domain.request.*;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResultBuilder;
import com.treefinance.saas.monitor.facade.domain.ro.*;
import com.treefinance.saas.monitor.facade.exception.ParamCheckerException;
import com.treefinance.saas.monitor.facade.service.AlarmRecordFacade;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.treefinance.saas.monitor.common.constants.AlarmConstants.TASK_SUCCESS_ALARM_OPERATOR;

/**
 * @author chengtong
 * @date 18/5/30 14:46
 */
@Service("alarmRecordFacade")
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
    @Autowired
    private ApplicationEventPublisher publisher;

    @Autowired
    private DiamondConfig config;
    @Autowired
    private EmailAlarmConfig emailAlarmConfig;


    private static Map<String, String> typeNameMapping = Maps.newHashMapWithExpectedSize(4);

    static {
        typeNameMapping.put(AlarmConstants.OPERATOR_ALARM, "运营商预警");
        typeNameMapping.put(AlarmConstants.EMAIL_ALARM, "邮箱预警");
        typeNameMapping.put(TASK_SUCCESS_ALARM_OPERATOR, "任务成功率运营商");
        typeNameMapping.put(AlarmConstants.TASK_SUCCESS_ALARM_ECOMMERCE, "任务成功率电商");
    }


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
        if (StringUtils.isNotEmpty(recordRequest.getId())) {
            criteriaInner.andIdIsLike(recordRequest.getId());
        }
        if (Objects.nonNull(recordRequest.getStartTime())) {
            criteriaInner.andDataTimeGreaterThan(recordRequest.getStartTime());
        }
        if (Objects.nonNull(recordRequest.getEndTime())) {
            criteriaInner.andDataTimeLessThanOrEqualTo(recordRequest.getEndTime());
        }
        if (Objects.nonNull(recordRequest.getStatus())) {
            criteriaInner.andIsProcessedEqualTo(recordRequest.getStatus());
        }

        criteria.setOrderByClause("isProcessed asc,dataTime desc");
        criteria.setLimit(recordRequest.getPageSize());
        criteria.setOffset(recordRequest.getOffset());

        long count = alarmRecordService.countByExample(criteria);

        List<AlarmRecord> list = alarmRecordService.queryPaginateByCondition(criteria);

        List<AlarmRecordRO> alarmRecordROList = DataConverterUtils.convert(list, AlarmRecordRO.class);

        for (AlarmRecordRO recordRO : alarmRecordROList) {
            recordRO.setProcessDesc(EAlarmRecordStatus.getDesc(recordRO.getIsProcessed()));
            Long recordId = recordRO.getId();

            AlarmWorkOrder alarmWorkOrder = alarmWorkOrderService.getByRecordId(recordId);
            if (alarmWorkOrder == null) {
                continue;
            }
            recordRO.setDutyName(alarmWorkOrder.getDutyName());
            recordRO.setDesc(alarmWorkOrder.getRemark());
            recordRO.setProcessorName(alarmWorkOrder.getProcessorName());
            recordRO.setOrderId(alarmWorkOrder.getId());
            recordRO.setOrderStatus(alarmWorkOrder.getStatus());
            recordRO.setOrderStatusDesc(EOrderStatus.getDesc(alarmWorkOrder.getStatus()));
        }
        return MonitorResultBuilder.pageResult(recordRequest, alarmRecordROList, count);
    }

    @Override
    public MonitorResult<List<SaasWorkerRO>> querySaasWorker() {

        List<SaasWorker> workers = saasWorkerService.getAllSaasWorker();

        List<SaasWorkerRO> saasWorkerROS = DataConverterUtils.convert(workers, SaasWorkerRO.class);

        Date now = new Date();

        for (SaasWorkerRO ro : saasWorkerROS) {
            if (StringUtils.isNotEmpty(ro.getDutyCorn())) {
                ro.setNextOnDuty(MonitorDateUtils.format2Ymd(CronUtils.getNextMeetDay(ro.getDutyCorn(), now)));
                ro.setPreOnDuty(MonitorDateUtils.format2Ymd(CronUtils.getPreMeetDay(ro.getDutyCorn(), now)));
            }
        }

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

        List<AlarmWorkOrder> list = alarmWorkOrderService.queryPaginateByCondition(criteria);

        List<AlarmWorkOrderRO> alarmWorkOrderROS = DataConverterUtils.convert(list, AlarmWorkOrderRO.class);

        alarmWorkOrderROS.forEach(alarmWorkOrderRO -> alarmWorkOrderRO.setStatusDesc(EOrderStatus.getDesc(alarmWorkOrderRO.getStatus())));

        return MonitorResultBuilder.pageResult(recordRequest, alarmWorkOrderROS, count);
    }

    @Override
    public MonitorResult<List<AlarmRecordRO>> queryAlarmListAndhandleMessge(AlarmRecordRequest recordRequest) {
        if(StringUtils.isEmpty(recordRequest.getAlarmType()))
        {
            throw new ParamCheckerException("请求参数查询记录表预警类型不能为空");
        }

        AlarmRecordCriteria criteria = new AlarmRecordCriteria();
        AlarmRecordCriteria.Criteria criteriaInner = criteria.createCriteria();

            criteriaInner.andAlarmTypeEqualTo(recordRequest.getAlarmType());

        if (Objects.nonNull(recordRequest.getStartTime())) {
            criteriaInner.andStartTimeGreaterThan(recordRequest.getStartTime());
        }
        if (Objects.nonNull(recordRequest.getEndTime())) {
            criteriaInner.andEndTimeLessThan(recordRequest.getEndTime());
        }
        criteria.setOrderByClause("isProcessed asc,dataTime desc");
        criteria.setLimit(recordRequest.getPageSize());
        criteria.setOffset(recordRequest.getOffset());

        long count = alarmRecordService.countByExample(criteria);

        List<AlarmRecord> list = alarmRecordService.queryPaginateByCondition(criteria);

        List<AlarmRecordRO> alarmRecordROList = DataConverterUtils.convert(list, AlarmRecordRO.class);

        for (AlarmRecordRO recordRO : alarmRecordROList) {
            recordRO.setProcessDesc(EAlarmRecordStatus.getDesc(recordRO.getIsProcessed()));
            Long recordId = recordRO.getId();

            AlarmWorkOrder alarmWorkOrder = alarmWorkOrderService.getByRecordId(recordId);
            if (alarmWorkOrder == null) {
                continue;
            }
            recordRO.setContinueTime(StatHelper.getDiffDuration(recordRO.getAlarmType(),recordRO.getEndTime(),recordRO.getDataTime(),config,emailAlarmConfig));
            recordRO.setDesc(alarmWorkOrder.getRemark());
        }
        return MonitorResultBuilder.pageResult(recordRequest, alarmRecordROList, count);

    }





    @Override
    public MonitorResult<Boolean> updateWorkerOrderProcessor(UpdateWorkOrderRequest request) {
        logger.info("更新工单处理人员");
        AlarmWorkOrder alarmWorkOrder = alarmWorkOrderService.getByPrimaryKey(request.getId());

        if (alarmWorkOrder == null) {
            return MonitorResultBuilder.build("不存在的工单");
        }
        if (!EOrderStatus.UNPROCESS.getCode().equals(alarmWorkOrder.getStatus())) {
            logger.info("工单已经处理");
            return MonitorResultBuilder.build("该工单已被处理");
        }

        Date now = new Date();

        SaasWorker processor = saasWorkerService.getWorkerByName(request.getProcessorName());

        if (processor == null) {
            logger.error("不存在的工作人员,processName:{}", request.getProcessorName());
            return MonitorResultBuilder.build("不存在的工作人员，" + request.getProcessorName());
        }


        alarmWorkOrder.setProcessorName(request.getProcessorName());
        alarmWorkOrder.setLastUpdateTime(now);

        String opName = request.getOpName() == null ? alarmWorkOrder.getDutyName() : request.getOpName();

        WorkOrderLog workOrderLog = new WorkOrderLog();

        workOrderLog.setId(UidGenerator.getId());
        workOrderLog.setOrderId(alarmWorkOrder.getId());
        workOrderLog.setRecordId(alarmWorkOrder.getRecordId());
        workOrderLog.setOpDesc("指定处理人员" + request.getProcessorName());
        workOrderLog.setOpName(opName);
        workOrderLog.setLastUpdateTime(now);
        workOrderLog.setCreateTime(now);
        try {
            alarmWorkOrderService.updateOrder(alarmWorkOrder, workOrderLog);
        } catch (Exception e) {
            logger.error("更新工单失败");
            logger.error(e.getMessage());
            return MonitorResultBuilder.build(Boolean.FALSE);
        }
        if (!alarmWorkOrder.getProcessorName().equals(opName)) {
            OrderDelegateEvent event = new OrderDelegateEvent();
            event.setOpName(opName);
            event.setAlarmRecord(alarmRecordService.getByPrimaryKey(alarmWorkOrder.getRecordId()));
            event.setAlarmWorkOrder(alarmWorkOrder);
            event.setProcessor(processor);
            publisher.publishEvent(event);
        }

        return MonitorResultBuilder.build(Boolean.TRUE);
    }

    @Override
    public MonitorResult<Boolean> updateWorkerOrderStatus(UpdateWorkOrderRequest request) {
        logger.info("更新工单状态id:{}", request.toString());

        EOrderStatus newStatus = EOrderStatus.getByValue(request.getStatus());
        if (newStatus == null) {
            logger.error("不支持的状态，newStatus={}", request.getStatus());
            return MonitorResultBuilder.build("不支持的status字段");
        }

        if (EOrderStatus.UNPROCESS.equals(newStatus)) {
            logger.error("不支持的状态，newStatus={}", request.getStatus());
            return MonitorResultBuilder.build("不支持的status字段");
        }

        Date now = new Date();

        AlarmWorkOrder alarmWorkOrder = alarmWorkOrderService.getByPrimaryKey(request.getId());
        if (alarmWorkOrder == null) {
            return MonitorResultBuilder.build("不存在的工单");
        }
        if (!EOrderStatus.UNPROCESS.getCode().equals(alarmWorkOrder.getStatus())) {
            logger.info("工单已经处理");
            return MonitorResultBuilder.build("该工单已被处理");
        }

        EOrderStatus oldStatus = EOrderStatus.getByValue(alarmWorkOrder.getStatus());

        AlarmRecord alarmRecord = alarmRecordService.getByPrimaryKey(alarmWorkOrder.getRecordId());
        if (alarmRecord == null) {
            logger.error("预警记录id：{}不存在", alarmWorkOrder.getRecordId());
            return MonitorResultBuilder.build("不存在的记录");
        }
        if (!EAlarmRecordStatus.UNPROCESS.getCode().equals(alarmWorkOrder.getStatus())) {
            logger.info("预警记录已被处理");
            return MonitorResultBuilder.build("预警记录已被处理");
        }


        List<AlarmRecord> unProcessedRecords = getUnProcessedAndSameTypeRecords(alarmRecord);
        unProcessedRecords.add(alarmRecord);

        unProcessedRecords.forEach(record -> {
            record.setIsProcessed(newStatus.getCode());
            record.setLastUpdateTime(now);
            record.setEndTime(now);
        });


        //如果是没有processor的工单
        if (StringUtils.isNotEmpty(alarmWorkOrder.getProcessorName())) {
            alarmWorkOrder.setProcessorName(alarmWorkOrder.getDutyName());
        }

        String opName = request.getOpName() == null ? alarmWorkOrder.getProcessorName() : request.getOpName();

        alarmWorkOrder.setRemark(request.getRemark());
        alarmWorkOrder.setStatus(newStatus.getCode());
        alarmWorkOrder.setLastUpdateTime(now);
        alarmWorkOrder.setProcessorName(opName);


        WorkOrderLog workOrderLog = new WorkOrderLog();

        workOrderLog.setId(UidGenerator.getId());
        workOrderLog.setOrderId(alarmWorkOrder.getId());
        workOrderLog.setRecordId(alarmWorkOrder.getRecordId());
        workOrderLog.setOpDesc(opName + "处理工单，状态由" + (oldStatus == null ? "未处理" : oldStatus.getDesc()) +
                "变更到" + newStatus.getDesc());
        workOrderLog.setOpName(alarmWorkOrder.getProcessorName());
        workOrderLog.setLastUpdateTime(now);
        workOrderLog.setCreateTime(now);
        try {
            alarmWorkOrderService.updateOrder(alarmWorkOrder, workOrderLog, unProcessedRecords);
        } catch (Exception e) {
            logger.error("更新工单失败");
            logger.error(e.getMessage());
            return MonitorResultBuilder.build("更新工单失败");
        }

        AlarmClearEvent event = new AlarmClearEvent();
        event.setAlarmRecord(alarmRecord);
        event.setAlarmType(EAlarmType.operator_alarm);
        event.setDutyMan(alarmWorkOrder.getDutyName());
        event.setOpDesc(workOrderLog.getOpDesc());
        event.setProcessor(alarmWorkOrder.getProcessorName());
        event.setResult(newStatus);

        publisher.publishEvent(event);

        return MonitorResultBuilder.build(Boolean.TRUE);
    }

    public List<AlarmRecord> getUnProcessedAndSameTypeRecords(AlarmRecord alarmRecord) {
        return alarmRecordService.queryAllUnprocessed(alarmRecord);
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


        for (WorkOrderLogRO logRO : workOrderLogROs) {
            logRO.setOpTime(MonitorDateUtils.format(logRO.getCreateTime()));
        }

        return MonitorResultBuilder.build(workOrderLogROs);
    }


    @Override
    public MonitorResult<List<SaasWorkerRO>> querySaasWorkerPaginate(SaasWorkerRequest request) {

        SaasWorkerCriteria criteria = new SaasWorkerCriteria();
        if (StringUtils.isNotEmpty(request.getName())) {
            criteria.createCriteria().andNameLike(request.getName());
        }

        long count = saasWorkerService.countByCondition(criteria);

        List<SaasWorker> saasWorkers = saasWorkerService.queryPaginateByCondition(criteria);

        List<SaasWorkerRO> saasWorkerROS = DataConverterUtils.convert(saasWorkers, SaasWorkerRO.class);

        Date now = new Date();

        for (SaasWorkerRO ro : saasWorkerROS) {
            if (StringUtils.isNotEmpty(ro.getDutyCorn())) {
                ro.setNextOnDuty(MonitorDateUtils.format2Ymd(CronUtils.getNextMeetDay(ro.getDutyCorn(), now)));
                ro.setPreOnDuty(MonitorDateUtils.format2Ymd(CronUtils.getPreMeetDay(ro.getDutyCorn(), now)));
            }

            ro.setCreateTimeStr(MonitorDateUtils.format(ro.getCreateTime()));
            ro.setLastUpdateTimeStr(MonitorDateUtils.format(ro.getLastUpdateTime()));

        }


        return MonitorResultBuilder.pageResult(request, saasWorkerROS, count);
    }


    @Override
    public MonitorResult<List<AlarmTypeListRO>> queryAlarmTypeList() {
        return null;
    }


    @Override
    public MonitorResult<List<AlarmRecordStatisticRO>> queryAlarmStatistic(AlarmRecordStatRequest recordStatRequest) {

        AlarmRecordCriteria criteria = new AlarmRecordCriteria();

        AlarmRecordCriteria.Criteria innerCriteria = criteria.createCriteria();

        List<Integer> codes = new ArrayList<>();
        codes.add(EAlarmRecordStatus.PROCESSED.getCode());
        codes.add(EAlarmRecordStatus.WRONG.getCode());
        codes.add(EAlarmRecordStatus.DISABLE.getCode());
        codes.add(EAlarmRecordStatus.REPAIRED.getCode());

        innerCriteria.andLevelEqualTo(EAlarmLevel.error.name()).andIsProcessedIn(codes);
        if (recordStatRequest.getStartTime() != null && recordStatRequest.getEndTime() != null) {
            innerCriteria.andCreateTimeGreaterThanOrEqualTo(recordStatRequest.getStartTime()).andCreateTimeLessThan
                    (recordStatRequest.getEndTime());
        }

        List<AlarmRecord> list = alarmRecordService.queryByCondition(criteria);

        if (list.isEmpty()) {
            return MonitorResultBuilder.build(new ArrayList<>());
        }


        Map<String, List<AlarmRecord>> map = list.stream().collect(Collectors.groupingBy
                (alarmRecord -> typeNameMapping.get(alarmRecord.getAlarmType())));

        List<AlarmRecordStatisticRO> returnList = new ArrayList<>();
        boolean hasName = StringUtils.isNotEmpty(recordStatRequest.getName());
        for (String alarmName : map.keySet()) {

            if (hasName) {
                if (!alarmName.contains(recordStatRequest.getName())) {
                    continue;
                }
            }

            List<AlarmRecord> innerList = map.get(alarmName);

            int count = 0, processedCount = 0, wrongCount = 0, disableCount = 0, recoveryCount = 0;
            double duration = 0d, durationAver, maxDuration = 0d;


            for (AlarmRecord record : innerList) {
                count += 1;

                if (EAlarmRecordStatus.REPAIRED.getCode().equals(record.getIsProcessed())) {
                    recoveryCount += 1;
                }
                if (EAlarmRecordStatus.WRONG.getCode().equals(record.getIsProcessed())) {
                    wrongCount += 1;
                }
                if (EAlarmRecordStatus.DISABLE.getCode().equals(record.getIsProcessed())) {
                    disableCount += 1;
                }
                if (EAlarmRecordStatus.PROCESSED.getCode().equals(record.getIsProcessed())) {
                    processedCount += 1;
                }

                double subDuration = StatHelper.getDiffDuration(record.getAlarmType(), record.getEndTime(), record
                        .getDataTime(), config, emailAlarmConfig);

                duration += subDuration;
                maxDuration = subDuration > maxDuration ? subDuration : maxDuration;

            }

            durationAver = duration / count;

            AlarmRecordStatisticRO alarmRecordStatisticRO = new AlarmRecordStatisticRO();

            alarmRecordStatisticRO.setName(alarmName);
            alarmRecordStatisticRO.setCount(count);
            alarmRecordStatisticRO.setDuration(duration);
            alarmRecordStatisticRO.setDurationAver(durationAver);
            alarmRecordStatisticRO.setMaxDuration(maxDuration);

            alarmRecordStatisticRO.setProcessedCount(processedCount);
            alarmRecordStatisticRO.setWrongCount(wrongCount);
            alarmRecordStatisticRO.setDisableCount(disableCount);
            alarmRecordStatisticRO.setRecoveryCount(recoveryCount);

            returnList.add(alarmRecordStatisticRO);

        }


        return MonitorResultBuilder.build(returnList);
    }


}
