package com.treefinance.saas.monitor.biz.autostat.template.job;

import com.alibaba.fastjson.JSON;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.treefinance.saas.monitor.biz.autostat.basicdata.filter.BasicDataFilter;
import com.treefinance.saas.monitor.biz.autostat.model.AsConstants;
import com.treefinance.saas.monitor.biz.autostat.template.calc.ExpressionCalculator;
import com.treefinance.saas.monitor.biz.autostat.template.calc.StatDataCalculator;
import com.treefinance.saas.monitor.dao.entity.StatTemplate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by yh-treefinance on 2018/1/23.
 */
public class StatCalculateLocalJob implements SimpleJob, BasicDataFilter<Map<String, Object>> {
    /**
     * logger
     */
    private static Logger logger = LoggerFactory.getLogger(StatCalculateLocalJob.class);
    /**
     * 数据队列
     */
    private final ArrayBlockingQueue<Map<String, Object>> dataQueue = Queues.newArrayBlockingQueue(200);

    /**
     * 统计模板
     */
    private StatTemplate statTemplate;

    /**
     * 数据计算器
     */
    private StatDataCalculator statDataCalculator;

    /**
     * @param statTemplate
     * @param statDataCalculator
     */
    public StatCalculateLocalJob(StatTemplate statTemplate, StatDataCalculator statDataCalculator) {
        this.statTemplate = statTemplate;
        this.statDataCalculator = statDataCalculator;
    }

    @Override
    public void execute(ShardingContext shardingContext) {
        long startTime = System.currentTimeMillis();
        try {
            List<Map<String, Object>> dataList = Lists.newArrayList();
            if (!dataQueue.isEmpty()) {
                while (!dataQueue.isEmpty()) {
                    dataList.add(dataQueue.poll());
                }
                // 数据计算
                statDataCalculator.calculate(statTemplate, dataList);
            }
            logger.info("stat data calculate cost {}ms : dataSize={},template={}...", (System.currentTimeMillis() - startTime), dataList.size(), JSON.toJSONString(statTemplate));
        } catch (Exception e) {
            logger.error("stat data error : template={}", JSON.toJSONString(statTemplate), e);
        }
    }

    @Override
    public void doFilter(List<Map<String, Object>> data) {
        if (CollectionUtils.isNotEmpty(data)) {
            try {
                int size = data.size();
                int remainderSize = dataQueue.remainingCapacity();
                // 队列是否已满，如果已满触发消费
                if (remainderSize < size) {
                    execute(null);
                }
                ExpressionCalculator expressionCalculator = statDataCalculator.getExpressionCalculator();
                // 计算模板
                expressionCalculator.initContext(AsConstants.STAT_TEMPLATE, statTemplate);

                Long expressioId = statTemplate.getId();
                String expression = statTemplate.getBasicDataFilter();
                data.forEach(map -> {
                    Map<String, Object> dataMap = Maps.newHashMap();
                    if (StringUtils.isEmpty(expression) || Boolean.TRUE.equals(expressionCalculator.calculate(expressioId, expression, map))) {
                        dataMap.putAll(map);
                        dataQueue.add(dataMap);
                    }
                });
            } catch (Exception e) {
                logger.error("fliter data error:data={}", JSON.toJSONString(data), e);
                throw new RuntimeException(e);
            }
        }
    }
}
