package com.treefinance.saas.monitor.biz.autostat.template.calc.spel;

import com.google.common.base.Joiner;
import com.google.common.collect.*;
import com.treefinance.saas.monitor.biz.autostat.mybatis.MybatisService;
import com.treefinance.saas.monitor.biz.autostat.mybatis.model.DbColumn;
import com.treefinance.saas.monitor.biz.autostat.template.calc.StatDataCalculator;
import com.treefinance.saas.monitor.dao.entity.StatGroup;
import com.treefinance.saas.monitor.dao.entity.StatItem;
import com.treefinance.saas.monitor.dao.entity.StatTemplate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.quartz.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by yh-treefinance on 2018/1/24.
 */
@Component
public class StatDataSpelCalculator implements StatDataCalculator {
    /**
     * redis key前缀
     */
    public static final String PREFIX = "saas-monitor:stat-data:spel:";
    /**
     * 数据时间
     */
    public static final String DATA_TIME = "dataTime";
    /**
     * logger
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private SpelExpressionCalculator spelExpressionCalculator;
    @Autowired
    private MybatisService mybatisService;


    @Override
    public List<Map<String, Object>> calculate(StatTemplate statTemplate, List<StatGroup> statGroups, List<StatItem> statItems, List<?> datas) {
        List<Map<String, Object>> resultList = Lists.newArrayList();
        Date currentTime = new Date();
        String templateCode = statTemplate.getTemplateCode();
        String statCron = statTemplate.getStatCron();
        // 统计时间
        Date statTime = getStatDate(currentTime, statCron);

        String statTimeStr = DateFormatUtils.format(statTime, "yyyy-MM-dd HH:mm:ss");
        Multimap<String, Map<String, Object>> redisMultiMap = ArrayListMultimap.create();
        String keyPrefix = Joiner.on(":").join(PREFIX, templateCode);
        for (Object data : datas) {
            Map<String, Object> dataMap = Maps.newHashMap();
            List<Object> redisGroups = Lists.newArrayList(keyPrefix);

            // 1.计算分组值
            dataMap.put(DATA_TIME, statTime);
            statGroups.forEach(statGroup -> {
                String groupCode = statGroup.getGroupCode();
                String groupExpression = statGroup.getGroupExpression();
                Object groupValue = spelExpressionCalculator.calculate(data, groupExpression);
                dataMap.put(groupCode, groupValue);
                redisGroups.add(groupValue);
            });

            // 2.计算数据项值
            statItems.stream().filter(statItem -> Byte.valueOf("0").equals(statItem.getDataSource())).forEach(statItem -> {
                String itemCode = statItem.getItemCode();
                String itemExpression = statItem.getItemExpression();
                Object itemValue = spelExpressionCalculator.calculate(data, itemExpression);
                dataMap.put(itemCode, itemValue);
            });
            redisGroups.add(statTimeStr);
            String redisKey = Joiner.on(":").join(redisGroups);
            redisMultiMap.put(redisKey, dataMap);
        }

        // 写入redis, 批量数据++
        List<String> groupNames = statGroups.stream().map(StatGroup::getGroupCode).collect(Collectors.toList());
        redisMultiMap.keys().forEach(redisKey -> {
            Map<String, Double> totalMap = Maps.newHashMap();
            Map<String, Object> groupMap = Maps.newHashMap();
            redisMultiMap.get(redisKey).forEach(dataMap -> {
                dataMap.keySet().stream()
                        .forEach(key -> {
                            if (groupNames.contains(key)) {
                                groupMap.put(key, dataMap.get(key));
                            } else {
                                Double totalValue = totalMap.get(key);
                                Number value = (Number) dataMap.get(key);
                                if (totalValue == null) {
                                    totalMap.put(key, value.doubleValue());
                                } else {
                                    totalMap.put(key, totalValue + value.doubleValue());
                                }
                            }
                        });
            });

            redisTemplate.boundHashOps(redisKey).putAll(groupMap);
            totalMap.keySet().forEach(key -> redisTemplate
                    .boundHashOps(redisKey)
                    .increment(key, totalMap.get(key)));

            Map<String, Object> resultMap = Maps.newHashMap();
            resultMap.putAll(groupMap);
            resultMap.putAll(totalMap);
            resultList.add(resultMap);
            logger.info("spel base data calculate: redisKey={},result={}", redisKey, resultMap);
        });
        redisTemplate.boundSetOps(keyPrefix).add(redisMultiMap.keys().toArray(new String[]{}));
        return resultList;
    }

    @Override
    public List<Map<String, Object>> flushData(StatTemplate statTemplate, List<StatGroup> statGroups, List<StatItem> statItems) {
        List<Map<String, Object>> resultList = Lists.newArrayList();
        String templateCode = statTemplate.getTemplateCode();
        String dataListKey = Joiner.on(":").join(PREFIX, templateCode);
        Set<String> dataKeySet = redisTemplate.boundSetOps(dataListKey).members();
        if (CollectionUtils.isEmpty(dataKeySet)) {
            return resultList;
        }
        // 获取数据
        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
        dataKeySet.forEach(dataKey -> {
            Map<String, Object> dataMap = hashOperations.entries(dataKey);
            resultList.add(dataMap);
        });

        // 二次计算(统计数据来源：0-基础数据，1-统计数据项)
        List<StatItem> _statItems = statItems.stream()
                .filter(statItem -> Byte.valueOf("1").equals(statItem.getDataSource()))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(_statItems)) {
            resultList.forEach(dataMap -> {

                _statItems.forEach(statItem -> {
                    String itemCode = statItem.getItemCode();
                    String itemExpression = statItem.getItemExpression();
                    Object value = spelExpressionCalculator.calculate(dataMap, itemExpression);
                    dataMap.put(itemCode, value);
                });
            });
        }


        // 获取数据对象
        String tableName = statTemplate.getDataObject();
        // 刷新到db


        return resultList;
    }

    /**
     * 获取统计时间
     *
     * @param currentTime
     * @param statCron
     * @return
     */
    private Date getStatDate(Date currentTime, String statCron) {
        CronExpression cronExpression = null;
        try {
            cronExpression = new CronExpression(statCron);
        } catch (ParseException e) {
            throw new RuntimeException("parse cron exception: cron=" + statCron, e);
        }
        return cronExpression.getTimeAfter(currentTime);
    }
}
