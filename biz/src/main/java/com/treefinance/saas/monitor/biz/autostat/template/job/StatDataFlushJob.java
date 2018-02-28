package com.treefinance.saas.monitor.biz.autostat.template.job;

import com.alibaba.fastjson.JSON;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.treefinance.saas.monitor.biz.autostat.template.calc.StatDataCalculator;
import com.treefinance.saas.monitor.dao.entity.StatTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by yh-treefinance on 2018/1/25.
 */
public class StatDataFlushJob implements SimpleJob {
    /**
     * logger
     */
    private static Logger logger = LoggerFactory.getLogger(StatCalculateLocalJob.class);
    /**
     * 统计模板
     */
    private StatTemplate statTemplate;

    /**
     * 数据计算器
     */
    private StatDataCalculator statDataCalculator;

    public StatDataFlushJob(StatTemplate statTemplate, StatDataCalculator statDataCalculator) {
        this.statTemplate = statTemplate;
        this.statDataCalculator = statDataCalculator;
    }

    @Override
    public void execute(ShardingContext shardingContext) {
        try {
            statDataCalculator.flushData(statTemplate);
        } catch (Exception e) {
            logger.error("flush data error : template={}", JSON.toJSONString(statTemplate), e);
        }
    }
}
