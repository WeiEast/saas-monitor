package com.treefinance.saas.monitor.biz.alarm.service.handler.impl;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.monitor.biz.alarm.expression.ExpressionParser;
import com.treefinance.saas.monitor.biz.alarm.model.AlarmConfig;
import com.treefinance.saas.monitor.biz.alarm.model.AlarmContext;
import com.treefinance.saas.monitor.biz.alarm.service.handler.AlarmHandler;
import com.treefinance.saas.monitor.biz.alarm.service.handler.Order;
import com.treefinance.saas.monitor.dao.entity.AsAlarmVariable;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by yh-treefinance on 2018/7/24.
 */
@Order(3)
@Component
public class VariableHandler implements AlarmHandler {
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
        List<AsAlarmVariable> alarmQueries = config.getAlarmVariables();
        if (CollectionUtils.isEmpty(alarmQueries)) {
            logger.info("alarm variable list is empty : config={}", JSON.toJSONString(config));
            return;
        }
        List<AsAlarmVariable> sortedAlarmVariable = alarmQueries.stream()
                .sorted(Comparator.comparing(AsAlarmVariable::getVarIndex)).collect(Collectors.toList());
        List<Map<String, Object>> groups = context.groups();

        for (AsAlarmVariable variable : sortedAlarmVariable) {
            String code = variable.getCode();
            String expression = variable.getValue();
            for (Map<String, Object> data : groups) {
                data.put(code, expressionParser.parse(expression, data));
            }
        }
    }
}
