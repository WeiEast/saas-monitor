package com.treefinance.saas.monitor.biz.autostat.template.calc;

import com.treefinance.saas.monitor.dao.entity.StatGroup;
import com.treefinance.saas.monitor.dao.entity.StatItem;
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
     * @param statGroups   统计分组
     * @param statItems    统计数据项
     * @param data         数据
     * @return
     */
    List<Map<String, Object>> calculate(StatTemplate statTemplate, List<StatGroup> statGroups, List<StatItem> statItems, List<?> data);

    /**
     * 数据刷新
     * @param statTemplate
     * @param statGroups
     * @param statItems
     * @return
     */
    List<Map<String,Object>> flushData(StatTemplate statTemplate, List<StatGroup> statGroups, List<StatItem> statItems);
}
