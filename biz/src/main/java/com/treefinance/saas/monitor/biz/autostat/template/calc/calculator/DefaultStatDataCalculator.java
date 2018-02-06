package com.treefinance.saas.monitor.biz.autostat.template.calc.calculator;

import com.google.common.base.Joiner;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.treefinance.commonservice.uid.UidGenerator;
import com.treefinance.saas.monitor.biz.autostat.model.AsConstants;
import com.treefinance.saas.monitor.biz.autostat.mybatis.MybatisService;
import com.treefinance.saas.monitor.biz.autostat.template.calc.ExpressionCalculator;
import com.treefinance.saas.monitor.biz.autostat.template.calc.StatDataCalculator;
import com.treefinance.saas.monitor.biz.autostat.template.service.StatGroupService;
import com.treefinance.saas.monitor.biz.autostat.template.service.StatItemService;
import com.treefinance.saas.monitor.biz.autostat.utils.CronUtils;
import com.treefinance.saas.monitor.dao.entity.StatGroup;
import com.treefinance.saas.monitor.dao.entity.StatItem;
import com.treefinance.saas.monitor.dao.entity.StatTemplate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
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
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by yh-treefinance on 2018/1/24.
 */
@Component
public class DefaultStatDataCalculator implements StatDataCalculator {
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

        // 分组计算
        Map<Integer, List<StatGroup>> statGroupMap = statGroups.stream().collect(Collectors.groupingBy(StatGroup::getGroupIndex));
        List<Map<String, Object>> resultList = Lists.newArrayList();
        Date currentTime = new Date();

        for (Integer groupIndex : statGroupMap.keySet()) {
            List<StatGroup> _statGroups = statGroupMap.get(groupIndex);
            if (CollectionUtils.isEmpty(_statGroups)) {
                continue;
            }
            // 1.计算数据
            Multimap<String, Map<String, Object>> redisMultiMap = caculateData(dataList, statTemplate, currentTime, groupIndex, _statGroups);
            // 2.写入redis（数据合并计算）
            List<Map<String, Object>> _resultList = cacheData2Redis(statTemplate, redisMultiMap, _statGroups);
            // 3.数据结果
            if (CollectionUtils.isNotEmpty(_resultList)) {
                resultList.addAll(_resultList);
            }
        }

        return resultList;
    }

    /**
     * 数据计算
     *
     * @param dataList
     * @param statTemplate
     * @param currentTime
     * @param groupIndex
     * @param _statGroups
     * @return
     */
    private Multimap<String, Map<String, Object>> caculateData(List<?> dataList, StatTemplate statTemplate, Date currentTime, Integer groupIndex, List<StatGroup> _statGroups) {
        Long templateId = statTemplate.getId();
        String statCron = statTemplate.getStatCron();
        String templateCode = statTemplate.getTemplateCode();
        String keyPrefix = Joiner.on(":").join(AsConstants.REDIS_PREFIX, templateCode, groupIndex);

        // 计算模板
        expressionCalculator.initContext(AsConstants.STAT_TEMPLATE, statTemplate);
        // 计算数据项
        List<StatItem> statItems = statItemService.get(templateId);
        // 计算结果
        Multimap<String, Map<String, Object>> redisMultiMap = ArrayListMultimap.create();
        // 数据计算
        for (Object dataObj : dataList) {
            Map<String, Object> data = (Map<String, Object>) dataObj;
            Map<String, Object> dataMap = Maps.newHashMap();
            List<Object> redisGroups = Lists.newArrayList(keyPrefix);

            // 1.优先计算数据时间
            _statGroups.stream().filter(statGroup -> AsConstants.DATA_TIME.equals(statGroup.getGroupCode()))
                    .forEach(statGroup -> {
                        Long itemId = statGroup.getId();
                        Object groupValue = expressionCalculator.calculate(itemId, statGroup.getGroupExpression(), data);
                        dataMap.put(statGroup.getGroupCode(), groupValue);
                        redisGroups.add(groupValue);
                    });

            // 2.获取统计时间
            Date dataTime = null;
            if (dataMap.containsKey(AsConstants.DATA_TIME)) {
                Long times = (Long) dataMap.get(AsConstants.DATA_TIME);
                dataTime = CronUtils.getStatDate(new Date(times), statCron);
            } else {
                dataTime = CronUtils.getStatDate(currentTime, statCron);
            }
            dataMap.put(AsConstants.DATA_TIME, dataTime.getTime());

            // 计算器上下文，需要数据时间
            String dataTimeStr = DateFormatUtils.format(dataTime, "yyyy-MM-dd HH:mm:ss");
            expressionCalculator.initContext(AsConstants.DATA_TIME, dataTimeStr);

            // groupIndex 的特殊处理: 一个数据项对应多个分组
            Object _groupIndex = data.get(AsConstants.GROUP_INDEX);
            data.put(AsConstants.GROUP_INDEX, groupIndex);
            // 3.计算各分组数据项值
            _statGroups.stream().filter(statGroup -> !AsConstants.DATA_TIME.equals(statGroup.getGroupCode()))
                    .forEach(statGroup -> {
                        Long groupId = statGroup.getId();
                        String groupCode = statGroup.getGroupCode();
                        String groupExpression = statGroup.getGroupExpression();
                        Object groupValue = expressionCalculator.calculate(groupId, groupExpression, data);
                        dataMap.put(groupCode, groupValue);
                        redisGroups.add(groupValue);
                    });
            data.put(AsConstants.GROUP_INDEX, _groupIndex);
            dataMap.put(AsConstants.GROUP_INDEX, groupIndex);

            // 4.计算数据项值
            statItems.stream().filter(statItem -> Byte.valueOf("0").equals(statItem.getDataSource())).forEach(statItem -> {
                Long itemId = statItem.getId();
                String itemCode = statItem.getItemCode();
                String itemExpression = statItem.getItemExpression();
                Object itemValue = expressionCalculator.calculate(itemId, itemExpression, data);
                dataMap.put(itemCode, itemValue);
            });
            // 分组标记
            dataMap.put(AsConstants.GROUP_INDEX, groupIndex);

            redisGroups.add(dataTimeStr);
            String redisKey = Joiner.on(":").useForNull("null").join(redisGroups);
            redisMultiMap.put(redisKey, dataMap);
        }
        return redisMultiMap;
    }

    /**
     * 数据保存至redis
     *
     * @param statTemplate
     * @param redisMultiMap
     * @param _statGroups
     */
    private List<Map<String, Object>> cacheData2Redis(StatTemplate statTemplate, Multimap<String, Map<String, Object>> redisMultiMap, List<StatGroup> _statGroups) {
        List<Map<String, Object>> resultList = Lists.newArrayList();
        String templateCode = statTemplate.getTemplateCode();
        String statCron = statTemplate.getStatCron();
        Long expireTime = getExpireTime(statCron);

        List<String> groupNames = _statGroups.stream().map(StatGroup::getGroupCode).collect(Collectors.toList());
        redisMultiMap.keys().forEach(redisKey -> {
            Map<String, Double> totalMap = Maps.newHashMap();
            Map<String, String> groupMap = Maps.newHashMap();
            redisMultiMap.get(redisKey).forEach(dataMap -> {
                dataMap.keySet().stream().forEach(key -> {
                    if (groupNames.contains(key) || AsConstants.DATA_TIME.equals(key)) {
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
            redisTemplate.boundHashOps(redisKey).expire(expireTime, TimeUnit.MILLISECONDS);
            totalMap.keySet().forEach(key -> redisTemplate
                    .boundHashOps(redisKey)
                    .increment(key, totalMap.get(key)));

            Map<String, Object> resultMap = Maps.newHashMap();
            resultMap.putAll(groupMap);
            resultMap.putAll(totalMap);
            resultList.add(resultMap);
            logger.info("spel base data calculate: redisKey={},result={}", redisKey, resultMap);
        });
        String dataListKey = Joiner.on(":").join(AsConstants.REDIS_PREFIX, templateCode);
        redisTemplate.boundSetOps(dataListKey).add(redisMultiMap.keys().toArray(new String[]{}));
        return resultList;
    }

    @Override
    public List<Map<String, Object>> flushData(StatTemplate statTemplate) {
        expressionCalculator.initContext(AsConstants.STAT_TEMPLATE, statTemplate);
        // 获取模板、分组、数据项信息
        Long templateId = statTemplate.getId();
        List<StatItem> statItems = statItemService.get(templateId);

        List<Map<String, Object>> resultList = Lists.newArrayList();
        String templateCode = statTemplate.getTemplateCode();
        String dataListKey = Joiner.on(":").join(AsConstants.REDIS_PREFIX, templateCode);
        Set<String> dataKeySet = redisTemplate.boundSetOps(dataListKey).members();
        if (CollectionUtils.isEmpty(dataKeySet)) {
            return resultList;
        }
        // 获取数据
        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
        List<String> emptyDataKeys = Lists.newArrayList();
        dataKeySet.forEach(dataKey -> {
            Map<String, Object> dataMap = hashOperations.entries(dataKey);
            if (MapUtils.isNotEmpty(dataMap)) {
                // 增加ID
                dataMap.put("id", UidGenerator.getId());

                if (dataMap.get(AsConstants.DATA_TIME) != null) {
                    Long dataTime = Long.valueOf(dataMap.get(AsConstants.DATA_TIME).toString());
                    dataMap.put(AsConstants.DATA_TIME, new Date(dataTime));
                }
                resultList.add(dataMap);
            } else {
                emptyDataKeys.add(dataKey);
            }
        });
        // 移除空数据key
        if (CollectionUtils.isNotEmpty(emptyDataKeys)) {
            redisTemplate.boundSetOps(dataListKey).remove(emptyDataKeys.toArray(new String[]{}));
        }

        // 二次计算(统计数据来源：0-基础数据，1-统计数据项)
        List<StatItem> _statItems = statItems.stream()
                .filter(statItem -> Byte.valueOf("1").equals(statItem.getDataSource()))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(_statItems)) {
            resultList.forEach(dataMap -> {

                _statItems.forEach(statItem -> {
                    String itemCode = statItem.getItemCode();
                    String itemExpression = statItem.getItemExpression();
                    Object value = expressionCalculator.calculate(statItem.getId(), itemExpression, dataMap);
                    dataMap.put(itemCode, value);
                });
            });
        }

        // 刷新到db
        if (CollectionUtils.isNotEmpty(resultList)) {
            mybatisService.batchInsertOrUpdate(statTemplate.getDataObject(), resultList);
        }

//        // 清除redis数据
//        redisTemplate.delete(dataKeySet);
        return resultList;
    }


    /**
     * 获取超时时间
     *
     * @param statCron
     * @return
     */
    private static Long getExpireTime(String statCron) {
        Long expireTime = CronUtils.getTimeInterval(statCron);
        if (expireTime < 10 * 60 * 1000) {
            return 10 * 60 * 1000L;
        }
        return expireTime;
    }

    public ExpressionCalculator getExpressionCalculator() {
        return expressionCalculator;
    }

    public static void main(String[] args) throws ParseException {
        Date date = new Date();
        String cron = "0 0/5 * * * ? ";
        System.out.println(getExpireTime(cron));
    }
}
