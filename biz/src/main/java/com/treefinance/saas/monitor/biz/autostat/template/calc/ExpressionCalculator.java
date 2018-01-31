package com.treefinance.saas.monitor.biz.autostat.template.calc;

import java.util.Map;

/**
 * Created by yh-treefinance on 2018/1/24.
 */
public interface ExpressionCalculator {

    /**
     * 数据计算
     *
     * @param data
     * @param expression
     * @return
     */
    Object calculate(Map<String,Object> data, String expression);
}
