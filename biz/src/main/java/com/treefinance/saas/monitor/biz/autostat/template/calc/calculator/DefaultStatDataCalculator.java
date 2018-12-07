package com.treefinance.saas.monitor.biz.autostat.template.calc.calculator;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Joiner;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.treefinance.commonservice.uid.UidService;
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
import com.treefinance.saas.monitor.util.MonitorDateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
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
    @Resource
    private UidService uidService;


    @Override
    public Map<Integer, List<Map<String, Object>>> calculate(StatTemplate statTemplate, List<?> dataList, Integer... groupIndexs) {
        Long templateId = statTemplate.getId();
        List<StatGroup> statGroups = statGroupService.get(templateId);

        // 计算模板
        expressionCalculator.initContext(AsConstants.STAT_TEMPLATE, statTemplate);

        // 分组计算
        Map<Integer, List<StatGroup>> statGroupMap = statGroups.stream().collect(Collectors.groupingBy(StatGroup::getGroupIndex));
        Map<Integer, List<Map<String, Object>>> resultMap = Maps.newHashMap();
        Date currentTime = new Date();

        Set<Integer> groupIndexSet = statGroupMap.keySet();
        if (groupIndexs != null && groupIndexs.length != 0) {
            List<Integer> _groupIndexs = Lists.newArrayList(groupIndexs);
            groupIndexSet = groupIndexSet.stream().filter(groupIndex -> _groupIndexs.contains(groupIndex)).collect(Collectors.toSet());
        }

        for (Integer groupIndex : groupIndexSet) {
            List<StatGroup> _statGroups = statGroupMap.get(groupIndex);
            if (CollectionUtils.isEmpty(_statGroups)) {
                continue;
            }
            // 1.计算数据
            Multimap<String, Map<String, Object>> redisMultiMap = caculateData(dataList, statTemplate, currentTime, groupIndex, _statGroups);
            logger.info("计算数据:redisMultiMap={},dataList={},statTemplate={},groupIndex={}",
                    JSON.toJSONString(redisMultiMap), JSON.toJSONString(dataList), JSON.toJSONString(statTemplate), groupIndex);
            // 2.写入redis（数据合并计算）
            List<Map<String, Object>> _resultList = cacheData2Redis(statTemplate, redisMultiMap, _statGroups);
            // 3.数据结果
            resultMap.put(groupIndex, _resultList);
        }

        return resultMap;
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
        String keyPrefix = Joiner.on(":").join(AsConstants.REDIS_PREFIX, templateCode, "index-" + groupIndex);

        // 计算数据项
        List<StatItem> statItems = statItemService.get(templateId);
        // 计算结果
        Multimap<String, Map<String, Object>> redisMultiMap = ArrayListMultimap.create();
        // 数据计算
        for (Object dataObj : dataList) {
            Map<String, Object> data = (Map<String, Object>) dataObj;
            Map<String, Object> dataMap = Maps.newHashMap();
            List<Object> redisGroups = Lists.newArrayList(keyPrefix);
            List<Object> distinctUserGroups = Lists.newArrayList(keyPrefix, "distinct:user");

            // 1.优先计算数据时间
            _statGroups.stream().filter(statGroup -> AsConstants.DATA_TIME.equals(statGroup.getGroupCode()))
                    .forEach(statGroup -> {
                        Long itemId = statGroup.getId();
                        Object groupValue = expressionCalculator.calculate(itemId, statGroup.getGroupExpression(), data);
                        dataMap.put(statGroup.getGroupCode(), groupValue);
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

            // 分组唯一键
            redisGroups.add(dataTimeStr);
            if (statTemplate.getEffectiveTime() > 0) {
                distinctUserGroups.add(MonitorDateUtils.format(CronUtils.getStatDate(dataTime, statTemplate.getEffectiveTime())));
            }
            String redisKey = Joiner.on(":").useForNull("null").join(redisGroups);
            String distinctUserRedisKey = Joiner.on(":").useForNull("null").join(distinctUserGroups);

            // GROUP 的特殊处理: 一个数据项对应多个分组
            data.put(AsConstants.GROUP, redisKey);
            data.put(AsConstants.DISTINCT_USER_GROUP, distinctUserRedisKey);
            // 3.计算各分组数据项值
            List<Object> groupValueList = Lists.newArrayList();
            _statGroups.stream().filter(statGroup -> !AsConstants.DATA_TIME.equals(statGroup.getGroupCode()))
                    .forEach(statGroup -> {
                        Long groupId = statGroup.getId();
                        String groupCode = statGroup.getGroupCode();
                        String groupExpression = statGroup.getGroupExpression();
                        Object groupValue = expressionCalculator.calculate(groupId, groupExpression, data);
                        groupValueList.add(groupValue);
                        dataMap.put(groupCode, groupValue);
                        redisGroups.add(groupCode + "-" + groupValue);
                        distinctUserGroups.add(groupCode + "-" + groupValue);
                    });
            //如果分组处理的groupValue为null,统计null维度没有实际意义.
            if (CollectionUtils.isNotEmpty(groupValueList) && groupValueList.contains(null)) {
                continue;
            }
            // 分组数据项值
            redisKey = Joiner.on(":").useForNull("null").join(redisGroups);
            distinctUserRedisKey = Joiner.on(":").useForNull("null").join(distinctUserGroups);
            data.put(AsConstants.GROUP, redisKey);
            data.put(AsConstants.DISTINCT_USER_GROUP, distinctUserRedisKey);

            // 4.计算数据项值
            statItems.stream().filter(statItem -> Byte.valueOf("0").equals(statItem.getDataSource())).forEach(statItem -> {
                Long itemId = statItem.getId();
                String itemCode = statItem.getItemCode();
                String itemExpression = statItem.getItemExpression();
                Object itemValue = expressionCalculator.calculate(itemId, itemExpression, data);
                dataMap.put(itemCode, itemValue);
            });

            statItems.stream().filter(statItem -> Byte.valueOf("2").equals(statItem.getDataSource())).forEach(statItem -> {
                Long itemId = statItem.getId();
                String itemCode = statItem.getItemCode();
                String itemExpression = statItem.getItemExpression();
                Object itemValue = expressionCalculator.calculate(itemId, itemExpression, data);
                if (itemValue instanceof Number) {
                    if (((Number) itemValue).doubleValue() > 0) {
                        Map<String, Integer> map = Maps.newHashMap();
                        map.put(data.get("statCode").toString(), 1);
                        dataMap.put(itemCode, map);
                    }
                }
            });

            // 分组标记
            dataMap.put(AsConstants.GROUP, redisKey);

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

        // 分组名称
        List<String> groupNames = _statGroups.stream().map(StatGroup::getGroupCode).collect(Collectors.toList());

        for (String redisKey : redisMultiMap.keySet()) {

            Map<String, Object> itemMap = Maps.newHashMap();
            Map<String, String> groupMap = Maps.newHashMap();
            Collection<Map<String, Object>> dataList = redisMultiMap.get(redisKey);

            for (Map<String, Object> dataMap : dataList) {
                for (String key : dataMap.keySet()) {
                    if (groupNames.contains(key) || AsConstants.DATA_TIME.equals(key) || AsConstants.GROUP.equals(key)) {
                        groupMap.put(key, dataMap.get(key) + "");
                    } else {
                        if (!(dataMap.get(key) instanceof Map)) {
                            Number value = (Number) dataMap.get(key);
                            Object itemValue = itemMap.get(key);
                            if (itemValue == null) {
                                itemMap.put(key, value.doubleValue());
                            } else {
                                itemMap.put(key, (Double) itemValue + value.doubleValue());
                            }
                            redisTemplate.boundHashOps(redisKey).increment(key, value.doubleValue());
                        } else {
                            Map<String, Integer> value = (Map<String, Integer>) dataMap.get(key);
                            Map<String, Integer> itemValue = (Map<String, Integer>) itemMap.get(key);
                            if (MapUtils.isEmpty(itemValue)) {
                                itemValue = Maps.newHashMap();
                                itemValue.putAll(value);
                            } else {
                                for (Map.Entry<String, Integer> entry : value.entrySet()) {
                                    if (itemValue.get(entry.getKey()) == null) {
                                        itemValue.put(entry.getKey(), entry.getValue());
                                    } else {
                                        Integer newValue = itemValue.get(entry.getKey()) + entry.getValue();
                                        itemValue.put(entry.getKey(), newValue);
                                    }
                                }
                            }
                            final Map<String, Integer> finalItemValue = itemValue;
                            SessionCallback<List<Object>> sessionCallback = new SessionCallback<List<Object>>() {
                                @Override
                                public List<Object> execute(RedisOperations operations) throws DataAccessException {
                                    operations.watch(redisKey);
                                    Object oldItemValueStr = operations.boundHashOps(redisKey).get(key);
                                    operations.multi();
                                    Map<String, Object> oldItemValue;
                                    if (oldItemValueStr != null) {
                                        oldItemValue = JSON.parseObject(oldItemValueStr.toString());
                                    } else {
                                        oldItemValue = Maps.newHashMap();
                                    }
                                    for (Map.Entry<String, Integer> entry : finalItemValue.entrySet()) {
                                        if (oldItemValue.get(entry.getKey()) == null) {
                                            oldItemValue.put(entry.getKey(), entry.getValue());
                                        } else {
                                            Integer updateValue = ((Number) oldItemValue.get(entry.getKey())).intValue() + entry.getValue();
                                            oldItemValue.put(entry.getKey(), updateValue);
                                        }
                                    }
                                    operations.boundHashOps(redisKey).put(key, JSON.toJSONString(oldItemValue));
                                    return operations.exec();
                                }
                            };
                            int i = 1;
                            while (true) {
                                List<Object> operationsResult = redisTemplate.execute(sessionCallback);
                                if (CollectionUtils.isNotEmpty(operationsResult)) {
                                    break;
                                }
                                try {
                                    Random random = new Random();
                                    int m = random.nextInt(10);
                                    Thread.sleep(m);
                                } catch (InterruptedException e) {
                                    logger.error("InterruptedException", e);
                                }
                                logger.info("redis key已被修改,事务discard,开始重试.key={},i={}", redisKey, i);
                                i++;
                            }


//                            Object oldItemValueStr = redisTemplate.boundHashOps(redisKey).get(key);
//                            Map<String, Object> oldItemValue;
//                            if (oldItemValueStr != null) {
//                                oldItemValue = JSON.parseObject(oldItemValueStr.toString());
//                            } else {
//                                oldItemValue = Maps.newHashMap();
//                            }
//                            for (Map.Entry<String, Integer> entry : itemValue.entrySet()) {
//                                if (oldItemValue.get(entry.getKey()) == null) {
//                                    oldItemValue.put(entry.getKey(), entry.getValue());
//                                } else {
//                                    Integer updateValue = ((Number) oldItemValue.get(entry.getKey())).intValue() + entry.getValue();
//                                    oldItemValue.put(entry.getKey(), updateValue);
//                                }
//                            }
//                            redisTemplate.boundHashOps(redisKey).put(key, JSON.toJSONString(oldItemValue));
                        }

                    }
                }
            }
            redisTemplate.boundHashOps(redisKey).putAll(groupMap);
            redisTemplate.boundHashOps(redisKey).expire(expireTime, TimeUnit.MILLISECONDS);

            Map<String, Object> resultMap = Maps.newHashMap();
            resultMap.putAll(groupMap);
            resultMap.putAll(itemMap);
            resultList.add(resultMap);
            logger.info("spel base data calculate: redisKey={}, groupMap={}，totalMap={},dataList={}",
                    redisKey, groupMap, itemMap, JSON.toJSONString(dataList));

        }

        String dataListKey = Joiner.on(":").join(AsConstants.REDIS_PREFIX, templateCode);
        Multiset<String> keys = redisMultiMap.keys();
        if (keys.size() > 0) {
            redisTemplate.boundSetOps(dataListKey).add(keys.toArray(new String[keys.size()]));
        }
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
                dataMap.put("id", uidService.getId());

                if (dataMap.get(AsConstants.DATA_TIME) != null) {
                    Long dataTime = Long.valueOf(dataMap.get(AsConstants.DATA_TIME).toString());
                    dataMap.put(AsConstants.DATA_TIME, new Date(dataTime));
                }
                resultList.add(dataMap);
            } else {
                emptyDataKeys.add(dataKey);
            }
            logger.debug("获取数据:dataKey={},dataMap={}", dataKey, JSON.toJSONString(dataMap, true));
        });
        // 移除空数据key
        if (CollectionUtils.isNotEmpty(emptyDataKeys)) {
            String[] values = emptyDataKeys.toArray(new String[0]);
            redisTemplate.boundSetOps(dataListKey).remove(values);
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
            logger.debug("刷新数据到db:dataList={}", JSON.toJSONString(resultList, true));
            mybatisService.batchInsertOrUpdate(statTemplate.getDataObject(), resultList);
        }

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

    @Override
    public ExpressionCalculator getExpressionCalculator() {
        return expressionCalculator;
    }

    public static void main(String[] args) throws ParseException {
        Date date = new Date();
        String cron = "0 0/15 * * * ? ";
        System.out.println(getExpireTime(cron));

//        Multimap<String, Map<String, Object>> redisMultiMap = ArrayListMultimap.create();
//        String key = "saas-monitor:stat:ecommerce-time-share:index-4:2018-02-24 15:50:00:appId-virtual_total_stat_appId:dataType-0:sourceType-0";
//
//        Map<String, Object> map1 = Maps.newHashMap();
//        map1.put("1", "1");
//
//        Map<String, Object> map2 = Maps.newHashMap();
//        map2.put("2", "2");
//
//        Map<String, Object> map3 = Maps.newHashMap();
//        map3.put("3", "3");
//
//        redisMultiMap.put(key, map1);
//        redisMultiMap.put(key, map2);
//        redisMultiMap.put(key, map3);
//
//        System.out.println("keys =" + JSON.toJSONString(redisMultiMap.keys()));
//        System.out.println("keyset =" + JSON.toJSONString(redisMultiMap.keySet()));
//
//
//        redisMultiMap.keys().forEach(redisKey -> {
//            System.out.println("redisKey=" + redisKey + ",dataMap=" + JSON.toJSONString(redisMultiMap.get(key)));
//        });

    }
}
