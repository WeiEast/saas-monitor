package com.treefinance.saas.monitor.biz.autostat.template.calc;

import java.util.Map;

/**
 * Created by yh-treefinance on 2018/1/24.
 */
public interface ExpressionCalculator {

    /**
     * 初始化上下文
     */
    void initContext(String key, Object value);

    /**
     * 数据计算
     *
     * @param expressionId
     * @param expression
     * @param dataMap
     * @return
     */
    Object calculate(Long expressionId, String expression, Map<String, Object> dataMap);
}
