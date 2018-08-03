package com.treefinance.saas.monitor.biz.facade;

import com.google.common.collect.Maps;
import com.treefinance.saas.monitor.biz.alarm.model.AlarmConfig;
import com.treefinance.saas.monitor.biz.alarm.model.AlarmContext;
import com.treefinance.saas.monitor.biz.alarm.service.handler.AlarmHandlerChain;
import com.treefinance.saas.monitor.biz.service.*;
import com.treefinance.saas.monitor.common.enumeration.ESaasEnv;
import com.treefinance.saas.monitor.common.utils.BeanUtils;
import com.treefinance.saas.monitor.common.utils.DataConverterUtils;
import com.treefinance.saas.monitor.common.utils.MonitorDateUtils;
import com.treefinance.saas.monitor.dao.entity.*;
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
import org.quartz.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author:guoguoyun
 * @date:Created in 2018/7/19上午11:30
 */
@Service("alarmBasicConfigurationFacade")
public class AlarmBasicConfigurationFacadeImpl implements AlarmBasicConfigurationFacade {
    private static final Logger logger = LoggerFactory.getLogger(AlarmBasicConfigurationFacade.class);

    @Autowired
    private AsAlarmService asAlarmService;

    @Autowired
    private AsAlarmMsgService asAlarmMsgService;

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
        logger.info("执行日志查询条件为{}", alarmExcuteLogRequest.toString());
        AsAlarm asAlarm = asAlarmService.getAsAlarmByPrimaryKey(alarmExcuteLogRequest.getId());

        AlarmExcuteLogRequest alarmExcuteLogRequest1 = new AlarmExcuteLogRequest();
        alarmExcuteLogRequest1.setId(alarmExcuteLogRequest.getId());

        List<AsAlarmTriggerRecord> totalasAlarmTriggerRecordList = asAlarmTriggerRecordService.queryAsAlarmTriggerRecord(alarmExcuteLogRequest1);
        List<AsAlarmTriggerRecord> asAlarmTriggerRecordList = asAlarmTriggerRecordService.queryAsAlarmTriggerRecordPagination(alarmExcuteLogRequest);


        List<AlarmExecuteLogRO> list = new ArrayList<>();
        for (AsAlarmTriggerRecord asAlarmTriggerRecord : asAlarmTriggerRecordList) {
            AlarmExecuteLogRO alarmExecuteLogRO = new AlarmExecuteLogRO();
            AsAlarmTrigger asAlarmTrigger = asAlarmTriggerService.getAsAlarmTriggerByPrimaryKey(asAlarmTriggerRecord.getConditionId());
            BeanUtils.copyProperties(asAlarmTrigger, alarmExecuteLogRO);
            BeanUtils.copyProperties(asAlarm, alarmExecuteLogRO);
            BeanUtils.copyProperties(asAlarmTriggerRecord, alarmExecuteLogRO);
            alarmExecuteLogRO.setConditionName(asAlarmTrigger.getName());
            list.add(alarmExecuteLogRO);
        }

        return new MonitorResult<>(alarmExcuteLogRequest, list, totalasAlarmTriggerRecordList.size());
    }

    @Override
    public MonitorResult<List<AsAlarmRO>> queryAlarmConfigurationList(AlarmBasicConfigurationRequest request) {
        logger.info("分页查询预警配置：{}", request);

        long count = asAlarmService.countByCondition(request);

        if (count == 0) {
            return MonitorResultBuilder.pageResult(request, new ArrayList<>(), 0);
        }

        List<AsAlarm> list = asAlarmService.queryPagingList(request);

        if (list.isEmpty()) {
            return MonitorResultBuilder.pageResult(request, new ArrayList<>(), 0);
        }

        List<Long> ids = list.stream().map(AsAlarm::getId).collect(Collectors.toList());
        List<AsAlarmMsg> asAlarmMsgs = asAlarmMsgService.queryMsgInIdList(ids);

        List<AsAlarmRO> returnList = DataConverterUtils.convert(list, AsAlarmRO.class);
        Map<Long, AsAlarmMsg> map = asAlarmMsgs.stream().collect(Collectors.toMap(AsAlarmMsg::getAlarmId,
                asAlarmMsg -> asAlarmMsg));
        for (AsAlarmRO asAlarmRO : returnList) {
            Long id = asAlarmRO.getId();
            AsAlarmMsg asAlarmMsg = map.get(id);
            ESaasEnv env = ESaasEnv.getByValue(asAlarmRO.getRunEnv());
            asAlarmRO.setRunEnvDesc(env.getDesc());
            if (asAlarmMsg != null) {
                asAlarmRO.setTitleTemplate(asAlarmMsg.getTitleTemplate());
                asAlarmRO.setBodyTemplate(asAlarmMsg.getBodyTemplate());
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
            SaasWorkerRO saasWorkerRO = new SaasWorkerRO();
            BeanUtils.copyProperties(saasWorker, saasWorkerRO);
            list.add(saasWorkerRO);
        }
        return new MonitorResult(list);
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
        Long intervalTime = intervalMilliSecond / (1000 * 60);
        map.put("alarmTime", MonitorDateUtils.format(cronDate));
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

        AsAlarm alarm = DataConverterUtils.convert(request.getAsAlarmInfoRequest(), AsAlarm.class);
        alarmConfig.setAlarm(alarm);

        List<AsAlarmConstant> alarmConstantList
                = DataConverterUtils.convert(request.getAsAlarmConstantInfoRequestList(), AsAlarmConstant.class);
        alarmConfig.setAlarmConstants(alarmConstantList);

        List<AsAlarmQuery> alarmQueryList
                = DataConverterUtils.convert(request.getAsAlarmQueryInfoRequestList(), AsAlarmQuery.class);
        alarmConfig.setAlarmQueries(alarmQueryList);

        List<AsAlarmVariable> alarmVariableList
                = DataConverterUtils.convert(request.getAsAlarmVariableInfoRequestList(), AsAlarmVariable.class);
        alarmConfig.setAlarmVariables(alarmVariableList);

        List<AsAlarmNotify> alarmNotifyList
                = DataConverterUtils.convert(request.getAsAlarmNotifyInfoRequestList(), AsAlarmNotify.class);
        alarmConfig.setAlarmNotifies(alarmNotifyList);

        AsAlarmMsg alarmMsg = DataConverterUtils.convert(request.getAsAlarmMsgInfoRequest(), AsAlarmMsg.class);
        alarmConfig.setAlarmMsg(alarmMsg);

        List<AsAlarmTrigger> alarmTriggerList
                = DataConverterUtils.convert(request.getAsAlarmTriggerInfoRequestList(), AsAlarmTrigger.class);
        alarmConfig.setAlarmTriggers(alarmTriggerList);

        AlarmContext alarmContext = alarmHandlerChain.handle(alarmConfig);
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
            List<AsAlarmTriggerRecord> triggerRecordList = alarmContext.getTriggerRecords();
            Map<String, Object> map = Maps.newHashMap();
            if (!CollectionUtils.isEmpty(triggerRecordList)) {
                AsAlarmTriggerRecord record = triggerRecordList.get(0);
                map.put("infoTrigger", record.getInfoTrigger());
                map.put("warningTrigger", record.getWarningTrigger());
                map.put("errorTrigger", record.getErrorTrigger());
                map.put("recoveryTrigger", record.getRecoveryTrigger());
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
}
