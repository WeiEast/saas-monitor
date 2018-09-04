package com.treefinance.saas.monitor.biz.alarm.service.handler.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.treefinance.commonservice.uid.UidGenerator;
import com.treefinance.saas.monitor.biz.alarm.expression.ExpressionParser;
import com.treefinance.saas.monitor.biz.alarm.model.AlarmConfig;
import com.treefinance.saas.monitor.biz.alarm.model.AlarmContext;
import com.treefinance.saas.monitor.biz.alarm.model.AlarmMessage;
import com.treefinance.saas.monitor.biz.alarm.model.EAnalysisType;
import com.treefinance.saas.monitor.biz.alarm.service.handler.AlarmHandler;
import com.treefinance.saas.monitor.biz.alarm.service.handler.Order;
import com.treefinance.saas.monitor.common.enumeration.EAlarmChannel;
import com.treefinance.saas.monitor.common.enumeration.EAlarmLevel;
import com.treefinance.saas.monitor.common.enumeration.ESwitch;
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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.treefinance.saas.monitor.biz.alarm.expression.spel.func.SpelFunction.nvl;

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

    @Autowired
    private List<ExpressionParser> expressionParsers;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void handle(AlarmConfig config, AlarmContext context) {
        List<AsAlarmTrigger> triggers = config.getAlarmTriggers();
        if (CollectionUtils.isEmpty(triggers)) {
            logger.info("alarm triggers is empty: triggers={}", JSON.toJSONString(triggers));
            return;
        }

        // 已排序触发条件
        List<AsAlarmTrigger> sortedTriggers = triggers.stream()
                .sorted(Comparator.comparing(AsAlarmTrigger::getTriggerIndex)).collect(Collectors.toList());
        // 数据分组
        List<Map<String, Object>> groups = context.groups();
        // 预警级别
        for (AsAlarmTrigger trigger : sortedTriggers) {
            List<AsAlarmTriggerRecord> recordList = Lists.newArrayList();
            for (Map<String, Object> data : groups) {
                AsAlarmTriggerRecord record = initTriggerRecord(config, context, trigger, data);
                recordList.add(record);
                try {
                    // 1.触发条件禁用
                    record.setConditionStatus(Byte.valueOf("0").equals(trigger.getStatus()) ? "启用" : "未启用");
                    if (!Byte.valueOf("0").equals(trigger.getStatus())) {
                        record.setCostTime(Long.valueOf((System.currentTimeMillis() - context.getStartTimeStamp())).intValue());
                        logger.info("alarm trigger is un-used: trigger={}", JSON.toJSONString(trigger));
                        continue;
                    }
                    // 2.判断是否触发预警（触发级别：error -> warning -> info）
                    EAlarmLevel currentLevel = judgeAlarmLevel(context, trigger, data, record);
                    // 3.未触发预警, 处理恢复
                    if (currentLevel == null) {
                        recover(context, config, trigger, data, record, currentLevel);
                        continue;
                    }
                    // 3.生成预警消息
                    List<AlarmMessage> alarmMessages = generateAlarmMessage(record.getId(), config, context, data, currentLevel);
                    String alarmMessageJson = JSON.toJSONString(alarmMessages);

                    record.setAlarmLevel(currentLevel.name());
                    record.setAlarmMessage(alarmMessageJson);
                    record.setNotifyTypes(getNotifyTypes(config, currentLevel));
                    logger.info("trigger alarm : trigger={}, alarmLevel ={}, title={}, message={}, data={}",
                            JSON.toJSONString(trigger), currentLevel, alarmMessageJson, JSON.toJSONString(data));
                } finally {
                    // 计算耗时
                    record.setCostTime(Long.valueOf((System.currentTimeMillis() - context.getStartTimeStamp())).intValue());
                }
            }
            context.addRecords(recordList);
            // 兼容测试时无alarmId
            if (trigger.getAlarmId() != null) {
                alarmTriggerRecordMapper.batchInsert(recordList);
            }
        }

    }

    /**
     * 获取通知方式
     *
     * @param config
     * @param currentLevel
     * @return
     */
    private String getNotifyTypes(AlarmConfig config, EAlarmLevel currentLevel) {
        List<String> notifyTypes = Lists.newArrayList();
        config.getAlarmNotifies().stream()
                .filter(notify -> currentLevel.name().equalsIgnoreCase(notify.getAlarmLevel()))
                .forEach(notify -> {
                    if (ESwitch.isOn(notify.getIvrSwitch())) {
                        notifyTypes.add("ivr");
                    }
                    if (ESwitch.isOn(notify.getWechatSwitch())) {
                        notifyTypes.add("微信");
                    }
                    if (ESwitch.isOn(notify.getSmsSwitch())) {
                        notifyTypes.add("短信");
                    }
                    if (ESwitch.isOn(notify.getEmailSwitch())) {
                        notifyTypes.add("邮件");
                    }
                });
        return Joiner.on(",").useForNull("").join(notifyTypes);
    }


    /**
     * 生成预警消息
     *
     * @param config
     * @param context
     * @param data
     * @param currentLevel
     * @return
     */
    private List<AlarmMessage> generateAlarmMessage(Long alarmNo, AlarmConfig config, AlarmContext context, Map<String, Object> data, EAlarmLevel currentLevel) {
        List<AsAlarmMsg> alarmMsgs = config.getAlarmMsgs();
        List<AlarmMessage> alarmMessages = Lists.newArrayList();

        alarmMsgs.forEach(alarmMsg -> {
            AlarmMessage alarmMessage = initMessage(data, currentLevel, alarmMsg);
            alarmMessage.setAlarmNo(alarmNo);
            alarmMessage.setRecordId(alarmNo);
            context.addMessage(alarmMessage);
            alarmMessages.add(alarmMessage);
        });
        return alarmMessages;
    }

    /**
     * 预警恢复的处理
     *
     * @param context
     * @param trigger
     * @param data
     * @param record
     * @param currentLevel
     */
    private void recover(AlarmContext context, AlarmConfig config, AsAlarmTrigger trigger,
                         Map<String, Object> data, AsAlarmTriggerRecord record, EAlarmLevel currentLevel) {
        String recoveryTrigger = trigger.getRecoveryTrigger();
        List<AsAlarmMsg> recoverMsgs = config.getRecoverMsgs();
        // 是否配置恢复触发条件
        if (CollectionUtils.isEmpty(recoverMsgs) || StringUtils.isEmpty(recoveryTrigger)) {
            record.setRecoveryTrigger("未配置");
            record.setRecoveryMessage("");
            return;
        }
        // 上次无预警，本次无预警本，不触发恢复
        AsAlarmTriggerRecord lastAlarm = getLastAlarm(trigger.getId(), context.getAlarmTime());
        if (lastAlarm == null) {
            record.setRecoveryTrigger("上次无预警，本次不触发恢复");
            return;
        }
        // 恢复消息为上次预警编号
        context.origin(data, "alarmNo", lastAlarm.getId());
        currentLevel = EAlarmLevel.getLevel(lastAlarm.getAlarmLevel());

        Object recoverResult = expressionParser.parse(recoveryTrigger, data);
        record.setRecoveryTrigger(nvl(recoverResult, "").toString());
        if (!Boolean.TRUE.equals(recoverResult)) {
            record.setRecoveryMessage("");
            return;
        }
        List<AlarmMessage> alarmMessages = Lists.newArrayList();
        for (AsAlarmMsg alarmMsg : recoverMsgs) {
            AlarmMessage alarmMessage = initMessage(data, currentLevel, alarmMsg);
            alarmMessage.setAlarmNo(lastAlarm.getId());
            alarmMessage.setRecordId(record.getId());

            context.addMessage(alarmMessage);
            alarmMessages.add(alarmMessage);
        }
        String alarmMessageJson = JSON.toJSONString(alarmMessages);
        record.setRecoveryMessage(alarmMessageJson);
        logger.info("trigger recover : alarmLevel ={}，record={}, trigger={}, message={}, data={}",
                currentLevel, JSON.toJSONString(record), JSON.toJSONString(trigger), alarmMessageJson, JSON.toJSONString(data));
    }

    /**
     * 初始化消息
     *
     * @param data
     * @param currentLevel
     * @param alarmMsg
     * @return
     */
    private AlarmMessage initMessage(Map<String, Object> data, EAlarmLevel currentLevel, AsAlarmMsg alarmMsg) {
        // messageType
        ExpressionParser expressionParser = expressionParsers.stream()
                .filter(_expressionParser -> EAnalysisType.code(alarmMsg.getAnalysisType()).equals(_expressionParser.type()))
                .findFirst()
                .get();
        // 预警通道
        List<EAlarmChannel> alarmChannels = Splitter.on(",").trimResults().splitToList(alarmMsg.getNotifyChannel())
                .stream().map(EAlarmChannel::getByValue).collect(Collectors.toList());
        AlarmMessage alarmMessage = new AlarmMessage();
        String title = (String) expressionParser.parse(alarmMsg.getTitleTemplate(), data);
        String message = (String) expressionParser.parse(alarmMsg.getBodyTemplate(), data);

        alarmMessage.setAlarmLevel(currentLevel);
        alarmMessage.setMessage(message);
        alarmMessage.setTitle(title);
        alarmMessage.setAlarmChannels(alarmChannels);
        // 预警解析方式：1-文本，2-html
        alarmMessage.setMessageType(EAnalysisType.code(alarmMsg.getAnalysisType()));
        logger.info("init-message : expressionParser={}，message={}", expressionParser.type(), JSON.toJSONString(alarmMessage));
        return alarmMessage;
    }

    /**
     * 判断当前预警级别
     *
     * @param trigger
     * @param data
     * @param record
     * @return
     */
    private EAlarmLevel judgeAlarmLevel(AlarmContext context, AsAlarmTrigger trigger, Map<String, Object> data, AsAlarmTriggerRecord record) {
        EAlarmLevel currentLevel = null;
        Map<EAlarmLevel, Object> alarmResultMap = Maps.newHashMap();
        for (EAlarmLevel alarmLevel : new EAlarmLevel[]{EAlarmLevel.error, EAlarmLevel.warning, EAlarmLevel.info}) {
            String alarmLevelTrigger = getTriggerMap(trigger).get(alarmLevel);
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
        // 触发预警
        if (currentLevel != null) {
            record.setAlarmLevel(currentLevel.name());
        }
        context.origin(data, "level", currentLevel);
        return currentLevel;
    }

    /**
     * 各级别触发条件
     *
     * @param trigger
     * @return
     */
    private Map<EAlarmLevel, String> getTriggerMap(AsAlarmTrigger trigger) {
        Map<EAlarmLevel, String> triggerMap = Maps.newHashMap();
        triggerMap.put(EAlarmLevel.info, trigger.getInfoTrigger());
        triggerMap.put(EAlarmLevel.warning, trigger.getWarningTrigger());
        triggerMap.put(EAlarmLevel.error, trigger.getErrorTrigger());
        return triggerMap;
    }

    /**
     * 生产
     *
     * @param config
     * @param context
     * @param trigger
     * @param data
     * @return
     */
    private AsAlarmTriggerRecord initTriggerRecord(AlarmConfig config, AlarmContext context, AsAlarmTrigger trigger, Map<String, Object> data) {
        Long triggerId = trigger.getId();
        Long alarmId = trigger.getAlarmId();

        AsAlarmTriggerRecord record = new AsAlarmTriggerRecord();
        record.setId(UidGenerator.getId());
        record.setAlarmId(alarmId);
        record.setConditionId(triggerId);
        record.setContext(JSON.toJSONString(data));
        record.setCreateTime(new Date());
        record.setRunEnv(config.getAlarm().getRunEnv());
        record.setRunTime(context.getAlarmTime());

        // 记录原始数据
        context.origin(data, "record", record);
        context.origin(data, "trigger", trigger);
        context.origin(data, "alarmNo", record.getId());
        return record;
    }


    /**
     * 最近一次预警消息
     *
     * @param conditionId
     * @param alarmTime
     * @return
     */
    private AsAlarmTriggerRecord getLastAlarm(Long conditionId, Date alarmTime) {
        AsAlarmTriggerRecordCriteria criteria = new AsAlarmTriggerRecordCriteria();
        criteria.createCriteria()
                .andRunTimeLessThan(alarmTime)
                .andRunTimeGreaterThan(DateUtils.addDays(alarmTime, -1))
                .andConditionIdEqualTo(conditionId);
        criteria.setOrderByClause("runTime desc");
        criteria.setLimit(3);
        criteria.setOffset(0);
        List<AsAlarmTriggerRecord> lasts = alarmTriggerRecordMapper.selectPaginationByExample(criteria);
        logger.info("getLastAlarm: conditionId={}, alarmTime={},lasts={}",
                conditionId, DateFormatUtils.format(alarmTime, "yyyy-MM-dd'T'HH:mm:ss"), JSON.toJSONString(lasts));
        if (CollectionUtils.isEmpty(lasts)) {
            return null;
        }
        AsAlarmTriggerRecord record = lasts.get(0);
        if (Boolean.TRUE.toString().equalsIgnoreCase(record.getErrorTrigger())) {
            return record;
        }
        if (Boolean.TRUE.toString().equalsIgnoreCase(record.getWarningTrigger())) {
            return record;
        }
        return null;
    }
}
