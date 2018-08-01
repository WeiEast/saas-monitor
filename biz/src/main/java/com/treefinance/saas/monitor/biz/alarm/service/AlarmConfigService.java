package com.treefinance.saas.monitor.biz.alarm.service;

import com.google.common.collect.Lists;
import com.treefinance.saas.monitor.biz.alarm.model.AlarmConfig;
import com.treefinance.saas.monitor.common.enumeration.ESwitch;
import com.treefinance.saas.monitor.dao.entity.*;
import com.treefinance.saas.monitor.dao.mapper.*;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by yh-treefinance on 2018/7/19.
 */
@Service
public class AlarmConfigService {
    @Autowired
    AsAlarmMapper alarmMapper;

    @Autowired
    AsAlarmConstantMapper alarmConstantMapper;

    @Autowired
    AsAlarmQueryMapper alarmQueryMapper;

    @Autowired
    AsAlarmVariableMapper alarmVariableMapper;

    @Autowired
    AsAlarmMsgMapper alarmMsgMapper;

    @Autowired
    AsAlarmTriggerMapper alarmTriggerMapper;

    @Autowired
    AsAlarmNotifyMapper alarmNotifyMapper;

    /**
     * @return
     */
    List<AlarmConfig> getActiveAlarmConfigs() {
        // 1. 主数据
        AsAlarmCriteria criteria = new AsAlarmCriteria();
        criteria.createCriteria().andAlarmSwitchEqualTo(ESwitch.ON.getCode());
        List<AsAlarm> alarms = alarmMapper.selectByExample(criteria);
        if (CollectionUtils.isEmpty(alarms)) {
            return Lists.newArrayList();
        }
        List<Long> alarmIds = alarms.stream().map(AsAlarm::getId).collect(Collectors.toList());

        // 2.常量
        List<AsAlarmConstant> alarmConstants = getAlarmConstants(alarmIds);
        Map<Long, List<AsAlarmConstant>> alarmConstantMap = alarmConstants.stream().collect(Collectors.groupingBy(AsAlarmConstant::getAlarmId));

        // 3.查询
        List<AsAlarmQuery> alarmQueries = getAsAlarmQueries(alarmIds);
        Map<Long, List<AsAlarmQuery>> alarmQueryMap = alarmQueries.stream().collect(Collectors.groupingBy(AsAlarmQuery::getAlarmId));

        // 4.变量
        List<AsAlarmVariable> alarmVariables = getAsAlarmVariables(alarmIds);
        Map<Long, List<AsAlarmVariable>> alarmVariableMap = alarmVariables.stream().collect(Collectors.groupingBy(AsAlarmVariable::getAlarmId));

        // 5.msg
        List<AsAlarmMsg> alarmMsgs = getAlarmMsgs(alarmIds);
        Map<Long, List<AsAlarmMsg>> alarmMsgMap = alarmMsgs.stream().collect(Collectors.groupingBy(AsAlarmMsg::getAlarmId));

        // 6.Trigger
        List<AsAlarmTrigger> alarmTriggers = getAlarmTriggers(alarmIds);
        Map<Long, List<AsAlarmTrigger>> alarmTriggerMap = alarmTriggers.stream().collect(Collectors.groupingBy(AsAlarmTrigger::getAlarmId));

        List<AsAlarmNotify> notifys = getAlarmNotifys(alarmIds);
        Map<Long, List<AsAlarmNotify>> notifysMap = notifys.stream().collect(Collectors.groupingBy(AsAlarmNotify::getAlarmId));

        // config组装
        List<AlarmConfig> alarmConfigs = Lists.newArrayList();
        alarms.forEach(asAlarm -> {
            Long alarmId = asAlarm.getId();
            AlarmConfig alarmConfig = new AlarmConfig();
            alarmConfig.setAlarm(asAlarm);
            alarmConfig.setAlarmConstants(alarmConstantMap.get(alarmId));
            alarmConfig.setAlarmQueries(alarmQueryMap.get(alarmId));
            alarmConfig.setAlarmVariables(alarmVariableMap.get(alarmId));

            List<AsAlarmMsg> _alarmMsgs = alarmMsgMap.get(alarmId);
            if (CollectionUtils.isNotEmpty(_alarmMsgs)) {
                alarmConfig.setAlarmMsg(_alarmMsgs.get(0));
            }
            alarmConfig.setAlarmTriggers(alarmTriggerMap.get(alarmId));
            alarmConfig.setAlarmNotifies(notifysMap.get(alarmId));
            alarmConfigs.add(alarmConfig);
        });
        return alarmConfigs;
    }

    /**
     * 根据alarmId查询
     *
     * @param alarmId
     * @return
     */
    public AlarmConfig getAlarmConfig(Long alarmId) {
        List<Long> alarmIds = Lists.newArrayList(alarmId);
        // 1. 主数据
        AlarmConfig alarmConfig = new AlarmConfig();
        AsAlarm alarm = alarmMapper.selectByPrimaryKey(alarmId);
        alarmConfig.setAlarm(alarm);
        // 2.常量
        alarmConfig.setAlarmConstants(getAlarmConstants(alarmIds));
        // 3.查询
        alarmConfig.setAlarmQueries(getAsAlarmQueries(alarmIds));
        // 4.变量
        alarmConfig.setAlarmVariables(getAsAlarmVariables(alarmIds));
        // 5.msg
        List<AsAlarmMsg> alarmMsgs = getAlarmMsgs(alarmIds);
        if (CollectionUtils.isNotEmpty(alarmMsgs)) {
            alarmConfig.setAlarmMsg(alarmMsgs.get(0));
        }
        // 6.Trigger
        alarmConfig.setAlarmTriggers(getAlarmTriggers(alarmIds));

        // 7.notifys
        alarmConfig.setAlarmNotifies(getAlarmNotifys(alarmIds));
        return alarmConfig;
    }

    private List<AsAlarmTrigger> getAlarmTriggers(List<Long> alarmIds) {
        AsAlarmTriggerCriteria triggerCriteria = new AsAlarmTriggerCriteria();
        triggerCriteria.createCriteria().andAlarmIdIn(alarmIds);
        return alarmTriggerMapper.selectByExample(triggerCriteria);
    }

    private List<AsAlarmMsg> getAlarmMsgs(List<Long> alarmIds) {
        AsAlarmMsgCriteria msgCriteria = new AsAlarmMsgCriteria();
        msgCriteria.createCriteria().andAlarmIdIn(alarmIds);
        return alarmMsgMapper.selectByExample(msgCriteria);
    }


    private List<AsAlarmNotify> getAlarmNotifys(List<Long> alarmIds) {
        AsAlarmNotifyCriteria criteria = new AsAlarmNotifyCriteria();
        criteria.createCriteria().andAlarmIdIn(alarmIds);
        return alarmNotifyMapper.selectByExample(criteria);
    }


    private List<AsAlarmVariable> getAsAlarmVariables(List<Long> alarmIds) {
        AsAlarmVariableCriteria variableCriteria = new AsAlarmVariableCriteria();
        variableCriteria.createCriteria().andAlarmIdIn(alarmIds);
        return alarmVariableMapper.selectByExample(variableCriteria);
    }

    private List<AsAlarmQuery> getAsAlarmQueries(List<Long> alarmIds) {
        AsAlarmQueryCriteria queryCriteria = new AsAlarmQueryCriteria();
        queryCriteria.createCriteria().andAlarmIdIn(alarmIds);
        return alarmQueryMapper.selectByExample(queryCriteria);
    }

    private List<AsAlarmConstant> getAlarmConstants(List<Long> alarmIds) {
        AsAlarmConstantCriteria constantCriteria = new AsAlarmConstantCriteria();
        constantCriteria.createCriteria().andAlarmIdIn(alarmIds);
        return alarmConstantMapper.selectByExample(constantCriteria);
    }
}
