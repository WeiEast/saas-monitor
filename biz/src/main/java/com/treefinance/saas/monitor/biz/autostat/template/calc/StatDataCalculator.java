package com.treefinance.saas.monitor.biz.autostat.template.calc;

import com.treefinance.saas.monitor.dao.entity.StatTemplate;

import java.util.List;
import java.util.Map;

/**
 * 统计数据计算器
 * Created by yh-treefinance on 2018/1/24.
 */
public interface StatDataCalculator {

    /**
     * 数据计算器
     *
     * @param statTemplate 统计模板
     * @param data         数据
     * @param groupIndexs  待计算分组
     * @return
     */
    Map<Integer, List<Map<String, Object>>> calculate(StatTemplate statTemplate, List<?> data, Integer... groupIndexs);

    /**
     * 数据刷新
     *
     * @param statTemplate
     * @return
     */
    List<Map<String, Object>> flushData(StatTemplate statTemplate);

    /**
     * 获取计算器
     *
     * @return
     */
    ExpressionCalculator getExpressionCalculator();
}
