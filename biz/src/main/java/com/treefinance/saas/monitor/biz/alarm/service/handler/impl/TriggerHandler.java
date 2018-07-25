package com.treefinance.saas.monitor.biz.alarm.service.handler.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.treefinance.commonservice.uid.UidGenerator;
import com.treefinance.saas.monitor.biz.alarm.expression.ExpressionParser;
import com.treefinance.saas.monitor.biz.alarm.model.AlarmConfig;
import com.treefinance.saas.monitor.biz.alarm.model.AlarmContext;
import com.treefinance.saas.monitor.biz.alarm.service.handler.AlarmHandler;
import com.treefinance.saas.monitor.biz.alarm.service.handler.Order;
import com.treefinance.saas.monitor.common.enumeration.EAlarmLevel;
import com.treefinance.saas.monitor.dao.entity.AsAlarmTrigger;
import com.treefinance.saas.monitor.dao.entity.AsAlarmTriggerRecord;
import com.treefinance.saas.monitor.dao.entity.AsAlarmTriggerRecordCriteria;
import com.treefinance.saas.monitor.dao.mapper.AsAlarmTriggerRecordMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by yh-treefinance on 2018/7/24.
 */
@Order(4)
@Component
public class TriggerHandler implements AlarmHandler {
    /**
     * logger
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * expression
     */
    @Autowired
    private ExpressionParser expressionParser;

    @Autowired
    private AsAlarmTriggerRecordMapper asAlarmTriggerRecordMapper;

    @Override
    public void handle(AlarmConfig config, AlarmContext context) {
        List<AsAlarmTrigger> triggers = config.getAlarmTriggers();
        if (CollectionUtils.isEmpty(triggers)) {
            logger.info("alarm triggers is empty: triggers={}", JSON.toJSONString(triggers));
            return;
        }
        Date alarmTime = context.getAlarmTime();
        long intervalTime = context.getIntervalTime();

        // 已排序触发条件
        List<AsAlarmTrigger> sortedTriggers = triggers.stream().sorted(Comparator.comparing(AsAlarmTrigger::getTriggerIndex)).collect(Collectors.toList());
        // 数据分组
        List<Map<String, Object>> groups = context.groups();
        List<AsAlarmTriggerRecord> recordList = Lists.newArrayList();
        // 预警级别
        EAlarmLevel[] alarmLevels = new EAlarmLevel[]{EAlarmLevel.info, EAlarmLevel.warning, EAlarmLevel.error};
        for (AsAlarmTrigger trigger : sortedTriggers) {
            long start = System.currentTimeMillis();
            Long triggerId = trigger.getId();
            Long alarmId = trigger.getAlarmId();
            String recover = trigger.getRecoveryTrigger();

            // 各级别触发条件
            Map<EAlarmLevel, String> triggerMap = Maps.newHashMap();
            triggerMap.put(EAlarmLevel.info, trigger.getInfoTrigger());
            triggerMap.put(EAlarmLevel.warning, trigger.getWarningTrigger());
            triggerMap.put(EAlarmLevel.error, trigger.getErrorTrigger());


            for (Map<String, Object> data : groups) {
                AsAlarmTriggerRecord record = new AsAlarmTriggerRecord();
                recordList.add(record);

                record.setId(UidGenerator.getId());
                record.setAlarmId(alarmId);
                record.setConditionId(triggerId);
                record.setContext(JSON.toJSONString(data));
                record.setCreateTime(new Date());
                record.setRunEnv(config.getAlarm().getRunEnv());
                record.setRunTime(alarmTime);

                // 触发条件禁用
                record.setConditionStatus(Byte.valueOf("0").equals(trigger.getStatus()) ? "启用" : "未启用");
                if (!Byte.valueOf("0").equals(trigger.getStatus())) {
                    record.setCostTime(Long.valueOf((System.currentTimeMillis() - start) / 1000).intValue());
                    logger.info("alarm trigger is un-used: trigger={}", JSON.toJSONString(trigger));
                    continue;
                }
                // 预警触发级别：error -> warning -> info
                EAlarmLevel currentLevel = null;
                Map<EAlarmLevel, Object> alarmResultMap = Maps.newHashMap();
                for (EAlarmLevel alarmLevel : alarmLevels) {
                    String alarmLevelTrigger = triggerMap.get(alarmLevel);
                    if (StringUtils.isEmpty(alarmLevelTrigger)) {
                        alarmResultMap.put(alarmLevel, "未配置");
                        continue;
                    }
                    Object result = expressionParser.parse(alarmLevelTrigger, data);
                    if (Boolean.TRUE.equals(result)) {
                        currentLevel = alarmLevel;
                    }
                    alarmResultMap.put(alarmLevel, result);
                }
                record.setErrorTrigger(JSON.toJSONString(alarmResultMap.get(EAlarmLevel.error)));
                record.setWarningTrigger(JSON.toJSONString(alarmResultMap.get(EAlarmLevel.warning)));
                record.setInfoTrigger(JSON.toJSONString(alarmResultMap.get(EAlarmLevel.info)));
                // 本次是否触发预警
                if (currentLevel == null) {
                    // 本次不触发预警，判断是否恢复，并发送预警恢复消息
                } else {
                    // 本次预警，发送预警消息
                }
                // 计算耗时
                record.setCostTime(Long.valueOf((System.currentTimeMillis() - start) / 1000).intValue());
            }
        }
    }

    /**
     * 上一次预警消息
     *
     * @param conditionId
     * @param alarmTime
     * @param intervalTime
     * @return
     */
    private boolean isLastAlarm(Long conditionId, Date alarmTime, Long intervalTime) {
        Date lastDate = DateUtils.addSeconds(alarmTime, -intervalTime.intValue());
        AsAlarmTriggerRecordCriteria criteria = new AsAlarmTriggerRecordCriteria();
        criteria.createCriteria().andConditionIdEqualTo(conditionId).andRunTimeEqualTo(lastDate);
        List<AsAlarmTriggerRecord> lasts = asAlarmTriggerRecordMapper.selectByExample(criteria);
        if (CollectionUtils.isEmpty(lasts)) {
            return false;
        }
        AsAlarmTriggerRecord record = lasts.get(0);
        if (!StringUtils.isEmpty(record.getErrorTrigger()) && !"未配置".equalsIgnoreCase(record.getErrorTrigger())) {
            return true;
        }
        if (!StringUtils.isEmpty(record.getWarningTrigger()) && !"未配置".equalsIgnoreCase(record.getWarningTrigger())) {
            return true;
        }
        return false;
    }
}
