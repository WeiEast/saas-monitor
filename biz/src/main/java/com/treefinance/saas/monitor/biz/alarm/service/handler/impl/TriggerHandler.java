package com.treefinance.saas.monitor.biz.alarm.service.handler.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.treefinance.commonservice.uid.UidGenerator;
import com.treefinance.saas.monitor.biz.alarm.expression.ExpressionParser;

import static com.treefinance.saas.monitor.biz.alarm.expression.spel.func.SpelFunction.*;

import com.treefinance.saas.monitor.biz.alarm.model.AlarmConfig;
import com.treefinance.saas.monitor.biz.alarm.model.AlarmContext;
import com.treefinance.saas.monitor.biz.alarm.model.AlarmMessage;
import com.treefinance.saas.monitor.biz.alarm.service.handler.AlarmHandler;
import com.treefinance.saas.monitor.biz.alarm.service.handler.Order;
import com.treefinance.saas.monitor.common.enumeration.EAlarmLevel;
import com.treefinance.saas.monitor.dao.entity.AsAlarmMsg;
import com.treefinance.saas.monitor.dao.entity.AsAlarmTrigger;
import com.treefinance.saas.monitor.dao.entity.AsAlarmTriggerRecord;
import com.treefinance.saas.monitor.dao.entity.AsAlarmTriggerRecordCriteria;
import com.treefinance.saas.monitor.dao.mapper.AsAlarmTriggerRecordMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
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
    @Resource(name = "spelExpressionParser")
    private ExpressionParser expressionParser;

    @Autowired
    private AsAlarmTriggerRecordMapper alarmTriggerRecordMapper;

    @Resource(name = "messageExpressionParser")
    private ExpressionParser messageExpressionParser;

    @Override
    public void handle(AlarmConfig config, AlarmContext context) {
        List<AsAlarmTrigger> triggers = config.getAlarmTriggers();
        if (CollectionUtils.isEmpty(triggers)) {
            logger.info("alarm triggers is empty: triggers={}", JSON.toJSONString(triggers));
            return;
        }


        // 已排序触发条件
        List<AsAlarmTrigger> sortedTriggers = triggers.stream().sorted(Comparator.comparing(AsAlarmTrigger::getTriggerIndex)).collect(Collectors.toList());
        // 数据分组
        List<Map<String, Object>> groups = context.groups();


        // 预警级别
        EAlarmLevel[] alarmLevels = new EAlarmLevel[]{EAlarmLevel.error, EAlarmLevel.warning, EAlarmLevel.info};
        for (AsAlarmTrigger trigger : sortedTriggers) {
            List<AsAlarmTriggerRecord> recordList = Lists.newArrayList();
            long start = System.currentTimeMillis();
            Long triggerId = trigger.getId();
            Long alarmId = trigger.getAlarmId();

            // 各级别触发条件
            Map<EAlarmLevel, String> triggerMap = Maps.newHashMap();
            triggerMap.put(EAlarmLevel.info, trigger.getInfoTrigger());
            triggerMap.put(EAlarmLevel.warning, trigger.getWarningTrigger());
            triggerMap.put(EAlarmLevel.error, trigger.getErrorTrigger());

            for (Map<String, Object> data : groups) {
                AsAlarmTriggerRecord record = new AsAlarmTriggerRecord();
                recordList.add(record);
                try {
                    record.setId(UidGenerator.getId());
                    record.setAlarmId(alarmId);
                    record.setConditionId(triggerId);
                    record.setContext(JSON.toJSONString(data));
                    record.setCreateTime(new Date());
                    record.setRunEnv(config.getAlarm().getRunEnv());
                    record.setRunTime(context.getAlarmTime());

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
                        alarmResultMap.put(alarmLevel, result);
                        if (Boolean.TRUE.equals(result)) {
                            currentLevel = alarmLevel;
                            break;
                        }
                    }
                    record.setErrorTrigger(nvl(alarmResultMap.get(EAlarmLevel.error), "").toString());
                    record.setWarningTrigger(nvl(alarmResultMap.get(EAlarmLevel.warning), "").toString());
                    record.setInfoTrigger(nvl(alarmResultMap.get(EAlarmLevel.info), "").toString());
                    // 未触发预警
                    if (currentLevel == null) {
                        // 是否配置恢复触发条件
                        String recover = trigger.getRecoveryTrigger();
                        if (StringUtils.isEmpty(recover)) {
                            record.setRecoveryTrigger("未配置");
                            record.setRecoveryMessage("");
                            continue;
                        }
                        // 上次无预警，本次无预警本，不触发恢复
                        AsAlarmTriggerRecord lastAlarm = getLastAlarm(triggerId, context.getAlarmTime(), alarmId);
                        if (lastAlarm == null) {
                            record.setRecoveryTrigger("上次无预警，本次不触发恢复");
                            continue;
                        }
                        // 上次有预警，本次无预警，触发恢复
                        Object recoverResult = expressionParser.parse(recover, data);
                        if (Boolean.TRUE.equals(recoverResult)) {
                            String messageExpression = trigger.getRecoveryMessageTemplate();
                            String recoverMessage = (String) messageExpressionParser.parse(messageExpression, data);
                            record.setRecoveryMessage(recoverMessage);
                            context.addMessage("预警解除", recoverMessage, EAlarmLevel.getLevel(lastAlarm.getAlarmLevel()));
                            logger.info("trigger recover : trigger={}, alarmLevel ={}, message={}, data={}",
                                    JSON.toJSONString(trigger), currentLevel, recoverMessage, JSON.toJSONString(data));
                        }
                        record.setRecoveryTrigger(nvl(recoverResult, "").toString());
                        continue;
                    }
                    // 触发预警
                    record.setAlarmLevel(currentLevel.name());
                    // 生成预警消息
                    AlarmMessage alarmMessage = generateAlarmMessage(config, context, data, currentLevel);
                    record.setAlarmMessage(alarmMessage.getMessage());
                    logger.info("trigger alarm : trigger={}, alarmLevel ={}, title={}, message={}, data={}",
                            JSON.toJSONString(trigger), currentLevel, JSON.toJSONString(alarmMessage), JSON.toJSONString(data));
                } finally {
                    // 计算耗时
                    record.setCostTime(Long.valueOf((System.currentTimeMillis() - start) / 1000).intValue());
                }
            }
            // 兼容测试时无alarmId
            if (alarmId != null) {
                alarmTriggerRecordMapper.batchInsert(recordList);
            }
        }

    }

    /**
     * @param config
     * @param context
     * @param data
     * @param currentLevel
     * @return
     */
    private AlarmMessage generateAlarmMessage(AlarmConfig config, AlarmContext context, Map<String, Object> data, EAlarmLevel currentLevel) {
        AsAlarmMsg alarmMsg = config.getAlarmMsg();
        Date alarmTime = context.getAlarmTime();
        AlarmMessage alarmMessage = new AlarmMessage();
        String title = "【" + currentLevel.name() + "】【" +
                DateFormatUtils.format(alarmTime, "yyyy-MM-dd HH:mm:ss")
                + "】发生【" + config.getAlarm().getName() + "】预警";
        String message = title + "\n \t预警数据：data=" + JSON.toJSONString(data);
        if (alarmMsg != null) {
            if (StringUtils.isNotEmpty(alarmMsg.getTitleTemplate())) {
                title = (String) messageExpressionParser.parse(alarmMsg.getTitleTemplate(), data);
            }
            if (StringUtils.isNotEmpty(alarmMsg.getBodyTemplate())) {
                message = (String) messageExpressionParser.parse(alarmMsg.getBodyTemplate(), data);
            }
            alarmMessage.setAlarmLevel(currentLevel);
            alarmMessage.setMessage(message);
            alarmMessage.setTitle(title);
            context.addMessage(alarmMessage);
        }
        return alarmMessage;
    }

    /**
     * 上一次预警消息
     *
     * @param conditionId
     * @param alarmTime
     * @param intervalTime
     * @return
     */
    private AsAlarmTriggerRecord getLastAlarm(Long conditionId, Date alarmTime, Long intervalTime) {
        Date lastDate = DateUtils.addSeconds(alarmTime, -intervalTime.intValue());
        AsAlarmTriggerRecordCriteria criteria = new AsAlarmTriggerRecordCriteria();
        criteria.createCriteria().andConditionIdEqualTo(conditionId).andRunTimeEqualTo(lastDate);
        List<AsAlarmTriggerRecord> lasts = alarmTriggerRecordMapper.selectByExample(criteria);
        if (CollectionUtils.isEmpty(lasts)) {
            return null;
        }
        AsAlarmTriggerRecord record = lasts.get(0);
        if (!StringUtils.isEmpty(record.getErrorTrigger()) && !"未配置".equalsIgnoreCase(record.getErrorTrigger())) {
            return record;
        }
        if (!StringUtils.isEmpty(record.getWarningTrigger()) && !"未配置".equalsIgnoreCase(record.getWarningTrigger())) {
            return record;
        }
        return null;
    }
}
