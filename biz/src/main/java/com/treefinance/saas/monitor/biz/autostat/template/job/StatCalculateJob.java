package com.treefinance.saas.monitor.biz.autostat.template.job;

import com.alibaba.fastjson.JSON;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.treefinance.saas.monitor.biz.autostat.basicdata.filter.BasicDataFilter;
import com.treefinance.saas.monitor.biz.autostat.template.calc.StatDataCalculator;
import com.treefinance.saas.monitor.dao.entity.StatGroup;
import com.treefinance.saas.monitor.dao.entity.StatItem;
import com.treefinance.saas.monitor.dao.entity.StatTemplate;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by yh-treefinance on 2018/1/23.
 */
public class StatCalculateJob implements SimpleJob, BasicDataFilter<JSONObject> {
    /**
     * logger
     */
    private static Logger logger = LoggerFactory.getLogger(StatCalculateJob.class);
    /**
     * 数据队列
     */
    private final ArrayBlockingQueue<JSONObject> dataQueue = Queues.newArrayBlockingQueue(20000);

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

    /**
     * @param statTemplate
     * @param statGroups
     * @param statItems
     * @param statDataCalculator
     */
    public StatCalculateJob(StatTemplate statTemplate, List<StatGroup> statGroups, List<StatItem> statItems, StatDataCalculator statDataCalculator) {
        this.statTemplate = statTemplate;
        this.statGroups = statGroups;
        this.statItems = statItems;
        this.statDataCalculator = statDataCalculator;
    }

    @Override
    public void execute(ShardingContext shardingContext) {
        long startTime = System.currentTimeMillis();
        List<JSONObject> dataList = Lists.newArrayList();
        if (!dataQueue.isEmpty()) {
            while (!dataQueue.isEmpty()) {
                dataList.add(dataQueue.poll());
            }
            // 数据计算
            statDataCalculator.calculate(statTemplate, statGroups, statItems, dataList);
        }
        logger.info("stat data calculate cost {}ms : dataSize={},template={}...", (System.currentTimeMillis() - startTime), dataList.size(), JSON.toJSONString(statTemplate));
    }

    @Override
    public void doFilter(List<JSONObject> data) {
        if (CollectionUtils.isNotEmpty(data)) {
            int size = data.size();
            int remainderSize = dataQueue.remainingCapacity();
            // 队列是否已满，如果已满触发消费
            if (remainderSize < size) {
                execute(null);
            }
            dataQueue.addAll(data);
        }
    }
}
