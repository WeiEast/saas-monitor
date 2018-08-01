package com.treefinance.saas.monitor.biz.alarm.service.handler.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.treefinance.saas.monitor.biz.alarm.expression.ExpressionParser;
import com.treefinance.saas.monitor.biz.alarm.model.AlarmConfig;
import com.treefinance.saas.monitor.biz.alarm.model.AlarmContext;
import com.treefinance.saas.monitor.biz.alarm.service.handler.AlarmHandler;
import com.treefinance.saas.monitor.biz.alarm.service.handler.Order;
import com.treefinance.saas.monitor.biz.alarm.utils.AlarmUtils;
import com.treefinance.saas.monitor.dao.entity.AsAlarm;
import com.treefinance.saas.monitor.dao.entity.AsAlarmConstant;
import org.apache.commons.collections.CollectionUtils;
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
 * 常量计算
 * Created by yh-treefinance on 2018/7/23.
 */
@Order(1)
@Component
public class ConstantHandler implements AlarmHandler {
    /**
     * logger
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * expression
     */
    @Resource(name = "spelExpressionParser")
    private ExpressionParser expressionParser;

    @Override
    public void handle(AlarmConfig config, AlarmContext context) {
        List<AsAlarmConstant> constantList = config.getAlarmConstants();
        if (CollectionUtils.isEmpty(constantList)) {
            logger.info("constants list is empty : config={}", JSON.toJSONString(config));
            return;
        }

        AsAlarm alarm = config.getAlarm();
        String runCron = alarm.getRunInterval();
        List<AsAlarmConstant> sortedConstants = constantList.stream()
                .sorted(Comparator.comparing(AsAlarmConstant::getConstIndex)).collect(Collectors.toList());

        Map<String, Object> constantMap = Maps.newHashMap();
        for (AsAlarmConstant alarmConstant : sortedConstants) {
            String code = alarmConstant.getCode();
            String value = alarmConstant.getValue();
            // 预警时间间隔
            if (Integer.valueOf("1").equals(alarmConstant.getConstIndex())) {
                Long intervalTime = AlarmUtils.interval(runCron) / 1000;
                context.addGroup(code, intervalTime);
                context.setIntervalTime(intervalTime);
                constantMap.put(code, intervalTime);
                continue;
            }
            // 当前预警时间
            if (Integer.valueOf("2").equals(alarmConstant.getConstIndex())) {
                Date alarmTime = AlarmUtils.current(runCron);
                context.addGroup(code, alarmTime);
                context.setAlarmTime(alarmTime);
                constantMap.put(code, alarmTime);
                continue;
            }
            Object calcValue = expressionParser.parse(value, constantMap);
            context.addGroup(code, calcValue);
            constantMap.put(code, calcValue);
        }
    }
}
