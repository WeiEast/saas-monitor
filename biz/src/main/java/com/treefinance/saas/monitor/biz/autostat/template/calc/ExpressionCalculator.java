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
     * @param data
     * @param expression
     * @return
     */
    Object calculate(Map<String, Object> data, String expression);
}
