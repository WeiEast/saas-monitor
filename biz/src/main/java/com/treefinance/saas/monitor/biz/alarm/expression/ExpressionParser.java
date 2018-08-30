package com.treefinance.saas.monitor.biz.alarm.expression;

import com.treefinance.saas.monitor.biz.alarm.model.EMessageType;

import java.util.Map;

/**
 * 表达式解析器
 * Created by yh-treefinance on 2018/7/23.
 */
public interface ExpressionParser {

    /**
     * 表达式解析器
     *
     * @param expression
     * @param context
     * @return
     */
    Object parse(String expression, Map<String, Object> context);

    /**
     * 解析的消息类型
     * @return
     */
    default EMessageType type() {
        return this.getClass().getAnnotation(Analysis.class).value();
    }
}
