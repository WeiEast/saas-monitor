package com.treefinance.saas.monitor.biz.autostat.template.calc.calculator;

import com.google.common.base.Joiner;
import com.google.common.collect.*;
import com.treefinance.commonservice.uid.UidGenerator;
import com.treefinance.saas.monitor.biz.autostat.mybatis.MybatisService;
import com.treefinance.saas.monitor.biz.autostat.mybatis.model.DbColumn;
import com.treefinance.saas.monitor.biz.autostat.template.calc.ExpressionCalculator;
import com.treefinance.saas.monitor.biz.autostat.template.calc.StatDataCalculator;
import com.treefinance.saas.monitor.biz.autostat.template.calc.spel.SpelExpressionCalculator;
import com.treefinance.saas.monitor.biz.autostat.template.service.StatGroupService;
import com.treefinance.saas.monitor.biz.autostat.template.service.StatItemService;
import com.treefinance.saas.monitor.dao.entity.StatGroup;
import com.treefinance.saas.monitor.dao.entity.StatItem;
import com.treefinance.saas.monitor.dao.entity.StatTemplate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
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
public class DefaultStatDataCalculator implements StatDataCalculator {
    /**
     * redis key前缀
     */
    public static final String PREFIX = "saas-monitor:stat-data:";
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
    private ExpressionCalculator expressionCalculator;
    @Autowired
    private MybatisService mybatisService;
    @Autowired
    private StatGroupService statGroupService;
    @Autowired
    private StatItemService statItemService;


    @Override
    public List<Map<String, Object>> calculate(StatTemplate statTemplate, List<?> dataList) {
        Long templateId = statTemplate.getId();
        List<StatGroup> statGroups = statGroupService.get(templateId);
        List<StatItem> statItems = statItemService.get(templateId);
        // 分组计算
        Map<Integer, List<StatGroup>> statGroupMap = statGroups.stream().collect(Collectors.groupingBy(StatGroup::getGroupIndex));

        List<Map<String, Object>> resultList = Lists.newArrayList();
        Date currentTime = new Date();
        String templateCode = statTemplate.getTemplateCode();
        String statCron = statTemplate.getStatCron();

        for (Integer groupIndex : statGroupMap.keySet()) {
            String keyPrefix = Joiner.on(":").join(PREFIX, templateCode, groupIndex);
            Multimap<String, Map<String, Object>> redisMultiMap = ArrayListMultimap.create();
            List<StatGroup> _statGroups = statGroupMap.get(groupIndex);
            if (CollectionUtils.isEmpty(_statGroups)) {
                continue;
            }
            for (Object dataObj : dataList) {
                Map<String, Object> data = (Map<String, Object>) dataObj;
                Map<String, Object> dataMap = Maps.newHashMap();
                List<Object> redisGroups = Lists.newArrayList(keyPrefix);

                // 1.计算分组值
                _statGroups.forEach(statGroup -> {
                    String groupCode = statGroup.getGroupCode();
                    String groupExpression = statGroup.getGroupExpression();
                    Object groupValue = expressionCalculator.calculate(data, groupExpression);
                    dataMap.put(groupCode, groupValue);
                    redisGroups.add(groupValue);
                });


                // 统计时间
                Date dataTime = null;
                if (dataMap.containsKey(DATA_TIME)) {
                    Long times = (Long) dataMap.get(DATA_TIME);
                    dataTime = getStatDate(new Date(times), statCron);
                } else {
                    dataTime = getStatDate(currentTime, statCron);
                }

                dataMap.put(DATA_TIME, dataTime.getTime());
                String dataTimeStr = DateFormatUtils.format(dataTime, "yyyy-MM-dd HH:mm:ss");

                // 2.计算数据项值
                statItems.stream().filter(statItem -> Byte.valueOf("0").equals(statItem.getDataSource())).forEach(statItem -> {
                    String itemCode = statItem.getItemCode();
                    String itemExpression = statItem.getItemExpression();
                    Object itemValue = expressionCalculator.calculate(data, itemExpression);
                    dataMap.put(itemCode, itemValue);
                });
                redisGroups.add(dataTimeStr);
                String redisKey = Joiner.on(":").useForNull("null").join(redisGroups);
                redisMultiMap.put(redisKey, dataMap);
            }

            // 写入redis, 批量数据++
            List<String> groupNames = _statGroups.stream().map(StatGroup::getGroupCode).collect(Collectors.toList());
            redisMultiMap.keys().forEach(redisKey -> {
                Map<String, Double> totalMap = Maps.newHashMap();
                Map<String, String> groupMap = Maps.newHashMap();
                redisMultiMap.get(redisKey).forEach(dataMap -> {
                    dataMap.keySet().stream().forEach(key -> {
                        if (groupNames.contains(key) || DATA_TIME.equals(key)) {
                            groupMap.put(key, dataMap.get(key) + "");
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
            String dataListKey = Joiner.on(":").join(PREFIX, templateCode);
            redisTemplate.boundSetOps(dataListKey).add(redisMultiMap.keys().toArray(new String[]{}));
        }

        return resultList;
    }

    @Override
    public List<Map<String, Object>> flushData(StatTemplate statTemplate) {
        // 获取模板、分组、数据项信息
        Long templateId = statTemplate.getId();
        List<StatItem> statItems = statItemService.get(templateId);

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
            if (MapUtils.isNotEmpty(dataMap)) {
                // 增加ID
                dataMap.put("id", UidGenerator.getId());

                if (dataMap.get(DATA_TIME) != null) {
                    Long dataTime = Long.valueOf(dataMap.get(DATA_TIME).toString());
                    dataMap.put(DATA_TIME, new Date(dataTime));
                }
                resultList.add(dataMap);
            }
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
                    Object value = expressionCalculator.calculate(dataMap, itemExpression);
                    dataMap.put(itemCode, value);
                });
            });
        }

        // 刷新到db
        if (CollectionUtils.isNotEmpty(resultList)) {
            mybatisService.batchInsertOrUpdate(statTemplate.getDataObject(), resultList);
        }

        // 清除redis数据
        redisTemplate.delete(dataKeySet);
        return resultList;
    }

    /**
     * 获取统计时间
     *
     * @param currentTime
     * @param statCron
     * @return
     */
    private static Date getStatDate(Date currentTime, String statCron) {
        CronExpression cronExpression = null;
        try {
            cronExpression = new CronExpression(statCron);
            Date cronDate = cronExpression.getNextValidTimeAfter(currentTime);
            int timeDiff = ((Long) (cronExpression.getNextValidTimeAfter(cronDate).getTime() - cronDate.getTime())).intValue();
            while (currentTime.before(cronDate)) {
                cronDate = DateUtils.addMilliseconds(cronDate, -2 * timeDiff);
                cronDate = cronExpression.getNextValidTimeAfter(cronDate);
            }
        } catch (ParseException e) {
            throw new RuntimeException("parse cron exception: cron=" + statCron, e);
        }
        return cronExpression.getNextValidTimeAfter(currentTime);
    }

    public ExpressionCalculator getExpressionCalculator() {
        return expressionCalculator;
    }

    public static void main(String[] args) throws ParseException {
        Date date = new Date();
        CronExpression cronExpression = new CronExpression("0/1 * * * * ? ");
        Date cronDate = cronExpression.getNextValidTimeAfter(date);
        System.out.println("date:" + DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss"));
        System.out.println("cronDate:" + DateFormatUtils.format(cronDate, "yyyy-MM-dd HH:mm:ss"));
        int timeDiff = ((Long) (cronExpression.getNextValidTimeAfter(cronDate).getTime() - cronDate.getTime())).intValue();
        while (date.before(cronDate)) {
            cronDate = DateUtils.addMilliseconds(cronDate, -2 * timeDiff);
            cronDate = cronExpression.getNextValidTimeAfter(cronDate);
            System.out.println(DateFormatUtils.format(cronDate, "yyyy-MM-dd HH:mm:ss"));
        }
    }
}
