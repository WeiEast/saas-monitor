package com.treefinance.saas.monitor.biz.autostat.template.job;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.treefinance.saas.monitor.biz.autostat.template.calc.StatDataCalculator;
import com.treefinance.saas.monitor.dao.entity.StatGroup;
import com.treefinance.saas.monitor.dao.entity.StatItem;
import com.treefinance.saas.monitor.dao.entity.StatTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by yh-treefinance on 2018/1/25.
 */
public class StatDataFlushJob implements SimpleJob {
    /**
     * logger
     */
    private static Logger logger = LoggerFactory.getLogger(StatCalculateJob.class);
    /**
     * 统计模板
     */
    private StatTemplate statTemplate;

    /**
     * 统计分组
     */
    private List<StatGroup> statGroups;
    /**
     * 统计数据项
     */
    private List<StatItem> statItems;

    /**
     * 数据计算器
     */
    private StatDataCalculator statDataCalculator;

    public StatDataFlushJob(StatTemplate statTemplate, List<StatGroup> statGroups, List<StatItem> statItems, StatDataCalculator statDataCalculator) {
        this.statTemplate = statTemplate;
        this.statGroups = statGroups;
        this.statItems = statItems;
        this.statDataCalculator = statDataCalculator;
    }

    @Override
    public void execute(ShardingContext shardingContext) {
        statDataCalculator.flushData(statTemplate, statGroups, statItems);
    }
}
