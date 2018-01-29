package com.treefinance.saas.monitor.biz.autostat.template.calc;

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
    Object calculate(Object data, String expression);
}
