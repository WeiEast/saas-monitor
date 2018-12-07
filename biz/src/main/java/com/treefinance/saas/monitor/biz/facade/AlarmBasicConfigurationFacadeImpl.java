package com.treefinance.saas.monitor.biz.facade;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.treefinance.saas.monitor.biz.alarm.model.AlarmConfig;
import com.treefinance.saas.monitor.biz.alarm.model.AlarmContext;
import com.treefinance.saas.monitor.biz.alarm.service.handler.AlarmHandlerChain;
import com.treefinance.saas.monitor.biz.service.AsAlarmService;
import com.treefinance.saas.monitor.biz.service.AsAlarmTriggerRecordService;
import com.treefinance.saas.monitor.biz.service.AsAlarmTriggerService;
import com.treefinance.saas.monitor.biz.service.SaasWorkerService;
import com.treefinance.saas.monitor.common.constants.AlarmConstants;
import com.treefinance.saas.monitor.common.enumeration.ESaasEnv;
import com.treefinance.saas.monitor.context.component.AbstractFacade;
import com.treefinance.saas.monitor.dao.entity.AsAlarm;
import com.treefinance.saas.monitor.dao.entity.AsAlarmConstant;
import com.treefinance.saas.monitor.dao.entity.AsAlarmMsg;
import com.treefinance.saas.monitor.dao.entity.AsAlarmNotify;
import com.treefinance.saas.monitor.dao.entity.AsAlarmQuery;
import com.treefinance.saas.monitor.dao.entity.AsAlarmTrigger;
import com.treefinance.saas.monitor.dao.entity.AsAlarmTriggerRecord;
import com.treefinance.saas.monitor.dao.entity.AsAlarmVariable;
import com.treefinance.saas.monitor.dao.entity.SaasWorker;
import com.treefinance.saas.monitor.facade.domain.request.AlarmExcuteLogRequest;
import com.treefinance.saas.monitor.facade.domain.request.autoalarm.AlarmBasicConfigurationDetailRequest;
import com.treefinance.saas.monitor.facade.domain.request.autoalarm.AlarmBasicConfigurationRequest;
import com.treefinance.saas.monitor.facade.domain.request.autoalarm.AlarmBasicConfigurationTestRequest;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResultBuilder;
import com.treefinance.saas.monitor.facade.domain.ro.AlarmExecuteLogRO;
import com.treefinance.saas.monitor.facade.domain.ro.SaasWorkerRO;
import com.treefinance.saas.monitor.facade.domain.ro.autoalarm.AsAlarmBasicConfigurationDetailRO;
import com.treefinance.saas.monitor.facade.domain.ro.autoalarm.AsAlarmRO;
import com.treefinance.saas.monitor.facade.exception.ParamCheckerException;
import com.treefinance.saas.monitor.facade.service.autoalarm.AlarmBasicConfigurationFacade;
import com.treefinance.toolkit.util.DateUtils;
import org.quartz.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author:guoguoyun
 * @date:Created in 2018/7/19上午11:30
 */
@Service("alarmBasicConfigurationFacade")
public class AlarmBasicConfigurationFacadeImpl extends AbstractFacade implements AlarmBasicConfigurationFacade {
    private static final Logger logger = LoggerFactory.getLogger(AlarmBasicConfigurationFacade.class);

    @Autowired
    private AsAlarmService asAlarmService;

    @Autowired
    private AsAlarmTriggerService asAlarmTriggerService;

    @Autowired
    private AsAlarmTriggerRecordService asAlarmTriggerRecordService;

    @Autowired
    private SaasWorkerService saasWorkerService;

    @Autowired
    private AlarmHandlerChain alarmHandlerChain;

    @Override
    public MonitorResult<Void> addOrUpdate(AlarmBasicConfigurationDetailRequest request) {
        if (request == null) {
            throw new ParamCheckerException("request不能为null");
        }
        asAlarmService.addOrUpdate(request);
        return MonitorResultBuilder.build();
    }

    @Override
    public MonitorResult<AsAlarmBasicConfigurationDetailRO> queryAlarmConfigurationDetailById(Long id) {
        if (id == null) {
            throw new ParamCheckerException("id不能为null");
        }
        AsAlarmBasicConfigurationDetailRO result = asAlarmService.queryAsAlarmBasicConfigurationDetailById(id);
        return MonitorResultBuilder.build(result);
    }

    @Override
    public MonitorResult<List<AlarmExecuteLogRO>> queryAlaramExecuteLogList(AlarmExcuteLogRequest alarmExcuteLogRequest) {
        if (alarmExcuteLogRequest.getId() == null) {
            return new MonitorResult<>("执行日志查询预警配置id不能为空");
        }
        logger.info("执行日志查询条件为{}", JSON.toJSONString(alarmExcuteLogRequest));
        AsAlarm asAlarm = asAlarmService.getAsAlarmByPrimaryKey(alarmExcuteLogRequest.getId());


        AlarmExcuteLogRequest alarmExcuteLogRequest1 = new AlarmExcuteLogRequest();
        alarmExcuteLogRequest1.setEndDate(alarmExcuteLogRequest.getEndDate());
        alarmExcuteLogRequest1.setStartDate(alarmExcuteLogRequest.getStartDate());
        alarmExcuteLogRequest1.setId(alarmExcuteLogRequest.getId());
        alarmExcuteLogRequest1.setPageNumber(alarmExcuteLogRequest.getPageNumber());
        alarmExcuteLogRequest1.setPageSize(alarmExcuteLogRequest.getPageSize());


        long total = asAlarmTriggerRecordService.queryAsAlarmTriggerRecord(alarmExcuteLogRequest1);
        List<AsAlarmTriggerRecord> asAlarmTriggerRecordList = asAlarmTriggerRecordService.queryAsAlarmTriggerRecordPagination(alarmExcuteLogRequest);

        List<Long> conditionIds = new ArrayList<>();
        for (AsAlarmTriggerRecord asAlarmTriggerRecord : asAlarmTriggerRecordList) {
            Long id = asAlarmTriggerRecord.getConditionId();
            conditionIds.add(id);
        }

        List<AlarmExecuteLogRO> list = new ArrayList<>();
        if (asAlarmTriggerRecordList.size() != 0) {
            List<AsAlarmTrigger> asAlarmTriggerList = asAlarmTriggerService.getAsAlarmTriggerByPrimaryKey(conditionIds);
            Map<Long, AsAlarmTrigger> asAlarmTriggerMap = asAlarmTriggerList.stream().collect(Collectors.toMap(AsAlarmTrigger::getId, Function.identity()));

            for (AsAlarmTriggerRecord asAlarmTriggerRecord : asAlarmTriggerRecordList) {
                AlarmExecuteLogRO alarmExecuteLogRO = new AlarmExecuteLogRO();
                AsAlarmTrigger alarmTrigger = asAlarmTriggerMap.get(asAlarmTriggerRecord.getConditionId());
                copy(alarmTrigger, alarmExecuteLogRO);
                copy(asAlarm, alarmExecuteLogRO);
                copy(asAlarmTriggerRecord, alarmExecuteLogRO);
                alarmExecuteLogRO.setConditionName(alarmTrigger.getName());
                double costTime =
                    new BigDecimal(asAlarmTriggerRecord.getCostTime()).divide(new BigDecimal("1000"), RoundingMode.HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                alarmExecuteLogRO.setCostTime(costTime);
                list.add(alarmExecuteLogRO);
            }
        }


        return new MonitorResult<>(alarmExcuteLogRequest, list, total);
    }

    @Override
    public MonitorResult<List<AsAlarmRO>> queryAlarmConfigurationList(AlarmBasicConfigurationRequest request) {
        logger.info("分页查询预警配置：{}", JSON.toJSONString(request));

        long count = asAlarmService.countByCondition(request);

        if (count == 0) {
            return MonitorResultBuilder.pageResult(request, Collections.emptyList(), 0);
        }

        List<AsAlarm> list = asAlarmService.queryPagingList(request);

        if (list.isEmpty()) {
            return MonitorResultBuilder.pageResult(request, Collections.emptyList(), 0);
        }

        List<AsAlarmRO> returnList = convert(list, AsAlarmRO.class);

        for (AsAlarmRO asAlarmRO : returnList) {
            ESaasEnv env = ESaasEnv.getByValue(asAlarmRO.getRunEnv());
            if (env != null) {
                asAlarmRO.setRunEnvDesc(env.getDesc());
            }
        }
        return MonitorResultBuilder.pageResult(request, returnList, count);
    }

    @Override
    public MonitorResult<List<SaasWorkerRO>> queryWorkerNameByDate(Date date) {
        if (StringUtils.isEmpty(date)) {
            return new MonitorResult<>("查询值班人员date不能为空");
        }
        List<SaasWorker> saasWorkerList = saasWorkerService.getNowDateWorker(date);
        List<SaasWorkerRO> list = new ArrayList<>();
        for (SaasWorker saasWorker : saasWorkerList) {
            if (saasWorker != null) {
                SaasWorkerRO saasWorkerRO = new SaasWorkerRO();
                copyProperties(saasWorker, saasWorkerRO);
                list.add(saasWorkerRO);
            }
        }
        return MonitorResultBuilder.build(list);
    }

    @Override
    public MonitorResult<Map<String, String>> getCronComputeValue(String cronExpressionStr) {
        Map<String, String> map = Maps.newHashMap();
        Date now = new Date();
        Date cronDate;
        Long intervalMilliSecond;
        try {
            CronExpression cronExpression = new CronExpression(cronExpressionStr);
            Date startTime = cronExpression.getNextValidTimeAfter(now);
            Date endTime = cronExpression.getNextValidTimeAfter(startTime);
            cronDate = startTime;
            intervalMilliSecond = endTime.getTime() - startTime.getTime();
        } catch (Exception e) {
            throw new ParamCheckerException("cron表达式错误");
        }
        Long intervalTime = intervalMilliSecond / 1000;
        map.put("alarmTime", DateUtils.format(cronDate));
        map.put("intervalTime", String.valueOf(intervalTime));
        return MonitorResultBuilder.build(map);
    }

    @Override
    public MonitorResult<Object> testAlarmConfiguration(AlarmBasicConfigurationTestRequest request) {
        if (request.getTestType() == null || request.getTestType() < 1 || request.getTestType() > 4) {
            throw new ParamCheckerException("测试表达式type不能为空,且取值为[1,4]之间");
        }
        if (request.getTestType() != 4) {
            if (StringUtils.isEmpty(request.getTestCode())) {
                throw new ParamCheckerException("测试表达式code不能为空");
            }
        }
        String testCode = request.getTestCode();
        AlarmConfig alarmConfig = new AlarmConfig();

        AsAlarm alarm = convert(request.getAsAlarmInfoRequest(), AsAlarm.class);
        alarmConfig.setAlarm(alarm);

        List<AsAlarmConstant> alarmConstantList
            = convert(request.getAsAlarmConstantInfoRequestList(), AsAlarmConstant.class);
        alarmConfig.setAlarmConstants(alarmConstantList);

        List<AsAlarmQuery> alarmQueryList
            = convert(request.getAsAlarmQueryInfoRequestList(), AsAlarmQuery.class);
        alarmConfig.setAlarmQueries(alarmQueryList);

        List<AsAlarmVariable> alarmVariableList
            = convert(request.getAsAlarmVariableInfoRequestList(), AsAlarmVariable.class);
        alarmConfig.setAlarmVariables(alarmVariableList);

        List<AsAlarmNotify> alarmNotifyList
            = convert(request.getAsAlarmNotifyInfoRequestList(), AsAlarmNotify.class);
        alarmConfig.setAlarmNotifies(alarmNotifyList);

        List<AsAlarmMsg> alarmNotifyMsgList = convert(request.getAsAlarmNotifyMsgInfoRequestList(), AsAlarmMsg.class);
        alarmConfig.setAlarmMsgs(alarmNotifyMsgList);

        List<AsAlarmMsg> alarmRecoveryMsgList = convert(request.getAsAlarmRecoveryMsgInfoRequestList(), AsAlarmMsg.class);
        alarmConfig.setRecoverMsgs(alarmRecoveryMsgList);

        List<AsAlarmTrigger> alarmTriggerList
            = convert(request.getAsAlarmTriggerInfoRequestList(), AsAlarmTrigger.class);
        alarmConfig.setAlarmTriggers(alarmTriggerList);

        AlarmContext alarmContext = null;
        try {
            alarmContext = alarmHandlerChain.handle(alarmConfig);
        } catch (RuntimeException e) {
            logger.error("测试预警配置异常", e);
            throw new ParamCheckerException(e.getClass().getSimpleName() + ": " + e.getMessage());
        }
        Map<String, Object> result = Maps.newHashMap();
        if (CollectionUtils.isEmpty(alarmContext.getDataList())) {
            return MonitorResultBuilder.build(result);
        }
        result.put("context", alarmContext.getDataList());
        Object valueResult = null;
        if (request.getTestType() != 4) {
            for (Map<String, Object> map : alarmContext.getDataList()) {
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    if (org.apache.commons.lang3.StringUtils.equals(entry.getKey(), testCode)) {
                        valueResult = entry.getValue();
                        break;
                    }
                }
            }
            result.put("result", valueResult);
        } else {
            Map<String, Object> map = null;
            if (!CollectionUtils.isEmpty(alarmContext.getDataList())) {
                Map<String, Object> data = alarmContext.getDataList().get(0);
                JSONObject dataJsonObject = JSONObject.parseObject(JSON.toJSONString(data));
                if (dataJsonObject.get("origin") != null) {
                    JSONObject originJsonObject = JSONObject.parseObject(JSON.toJSONString(dataJsonObject.get("origin")));
                    if (originJsonObject.get("record") != null) {
                        JSONObject record = JSONObject.parseObject(JSON.toJSONString(originJsonObject.get("record")));
                        map = Maps.newHashMap();
                        map.put("infoTrigger", record.get("infoTrigger"));
                        map.put("warningTrigger", record.get("warningTrigger"));
                        map.put("errorTrigger", record.get("errorTrigger"));
                        map.put("recoveryTrigger", record.get("recoveryTrigger"));
                    }
                }

            }
            result.put("result", map);

        }

        return MonitorResultBuilder.build(result);
    }

    @Override
    public MonitorResult<Object> updateAlarmSwitch(Long alarmId) {
        if (alarmId == null) {
            return new MonitorResult<>("操作预警开关预警id不能为空");
        }
        asAlarmService.updateAlarmSwitch(alarmId);
        return MonitorResultBuilder.build();
    }


    @Override
    public MonitorResult<Boolean> duplicateConfig(Long alarmId) {

        asAlarmService.copyAlarm(alarmId);

        return MonitorResultBuilder.build(Boolean.TRUE);
    }

    @Override
    public MonitorResult<Boolean> deleteById(Long alarmId) {

        AsAlarm asAlarm = asAlarmService.getAsAlarmByPrimaryKey(alarmId);

        if(asAlarm == null || AlarmConstants.SWITCH_ON.equals(asAlarm.getAlarmSwitch())){
            return MonitorResultBuilder.build("改预警配置不存在或者正在开启状态");
        }

        asAlarmService.deleteById(alarmId);

        return MonitorResultBuilder.build(Boolean.TRUE);
    }
}
