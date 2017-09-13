package com.treefinance.saas.monitor.biz.task;

import com.alibaba.fastjson.JSON;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.treefinance.commonservice.uid.UidGenerator;
import com.treefinance.saas.gateway.servicefacade.enums.TaskStepEnum;
import com.treefinance.saas.monitor.biz.config.DiamondConfig;
import com.treefinance.saas.monitor.biz.helper.RedisKeyHelper;
import com.treefinance.saas.monitor.biz.helper.StatHelper;
import com.treefinance.saas.monitor.biz.service.*;
import com.treefinance.saas.monitor.common.cache.RedisDao;
import com.treefinance.saas.monitor.common.domain.dto.*;
import com.treefinance.saas.monitor.common.enumeration.EStatType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 数据统计Job
 * Created by yh-treefinance on 2017/5/25.
 */
public class TaskDataFlushJob implements SimpleJob {
    private static final Logger logger = LoggerFactory.getLogger(TaskDataFlushJob.class);
    @Autowired
    private RedisDao redisDao;
    @Autowired
    private EcommerceService ecommerceService;
    @Autowired
    private WebsiteService websiteService;
    @Autowired
    private OperatorService operatorService;
    @Autowired
    private StatAccessUpdateService statAccessUpdateService;
    @Autowired
    private SaasStatAccessUpdateService saasStatAccessUpdateService;
    @Autowired
    private SaasErrorStepDayStatUpdateService saasErrorDayStatUpdateService;
    @Autowired
    private AlarmService alarmService;
    @Autowired
    private AllAlarmService allAlarmService;
    @Autowired
    private DiamondConfig diamondConfig;

    @Override
    public void execute(ShardingContext shardingContext) {
        long start = System.currentTimeMillis();
        try {
            int intervalMinutes = diamondConfig.getMonitorIntervalMinutes();
            logger.info("TaskMonitorAlarm:intervalMinutes={}", intervalMinutes);
            Date now = new Date();
            Date intervalTime = StatHelper.calculateIntervalTime(now, intervalMinutes);
            //下一个监控时间 比如当前是9:00则currentInterval=9:10
            String currentInterval = intervalTime.getTime() + "";
            //当前监控时间 比如当前是9:00则previousCurrentInterval=9:00
            String previousCurrentInterval = DateUtils.addMinutes(intervalTime, -intervalMinutes).getTime() + "";

            redisDao.getRedisTemplate().execute(new SessionCallback<Object>() {
                @Override
                public Object execute(RedisOperations redisOperations) throws DataAccessException {
                    // 统计时间
                    String dayKey = RedisKeyHelper.keyOfDay(now);
                    Set<String> intervalSets = redisOperations.opsForSet().members(dayKey);
                    if (CollectionUtils.isEmpty(intervalSets)) {
                        return null;
                    }
                    Set<Date> intervalTimeSets = Sets.newHashSet();
                    intervalSets.forEach(t -> {
                        Long time = Long.valueOf(t);
                        intervalTimeSets.add(new Date(time));
                    });
                    intervalTimeSets.remove(new Date(Long.valueOf(currentInterval)));
                    logger.info("TaskMonitorAlarm:TaskDataFlushJob中intervalTimeSets={} currentInterval={}", JSON.toJSONString(intervalTimeSets), currentInterval);
                    // appId列表
                    String appIdKey = RedisKeyHelper.keyOfAppIds();
                    Set<String> appIdSet = redisOperations.opsForSet().members(appIdKey);
                    if (CollectionUtils.isEmpty(appIdSet)) {
                        return null;
                    }
                    // 1.保存合计数据
                    saveTotalDayData(intervalTimeSets, appIdSet, redisOperations);
                    // 2.保存合计数据
                    saveTotalData(intervalTimeSets, appIdSet, redisOperations);
                    // 3.保存电商数据
                    saveEcommerceData(intervalTimeSets, appIdSet, redisOperations);
                    // 4.保存邮箱数据
                    saveMailData(intervalTimeSets, appIdSet, redisOperations);
                    // 5.保存运营商数据
                    saveOperatorData(intervalTimeSets, appIdSet, redisOperations);

                    //6.保存合计所有商户后的总计数据
                    saveAllTotalDayData(intervalTimeSets, redisOperations);
                    saveAllTotalData(intervalTimeSets, redisOperations);

                    //7.保存任务失败环节统计数据
                    saveAllErrorDayData(intervalTimeSets, redisOperations);

                    // 删除已生成数据key
                    List<String> deleteList = Lists.newArrayList();
                    intervalSets.forEach(time -> {
                        if (!time.equals(currentInterval)) {
                            deleteList.add(time);
                        }
                    });
                    if (!deleteList.isEmpty()) {
                        logger.info("TaskMonitorAlarm:刷新数据完成，清除数据：deleteList={},currentInterval={}", JSON.toJSONString(deleteList), currentInterval);
                        String[] array = new String[deleteList.size()];
                        redisOperations.opsForSet().remove(dayKey, deleteList.toArray(array));
                    }
                    return null;
                }
            });
        } catch (Exception e) {
            logger.error("TaskMonitorAlarm:statdataflushjob exception : ", e);
        } finally {
            logger.info("TaskMonitorAlarm:定时刷新数据完成，耗时time={}ms", System.currentTimeMillis() - start);
            allAlarm();
        }
    }

    /**
     * 监控消息(合计所有商户后的监控消息)
     */
    private void allAlarm() {
        String environment = diamondConfig.getMonitorEnvironment();
        List<String> excludeEnvironment = Splitter.on(",").trimResults().splitToList(diamondConfig.getMonitorAlarmExcludeEnvironment());
        if (CollectionUtils.isNotEmpty(excludeEnvironment) && excludeEnvironment.contains(environment)) {
            logger.info("TaskMonitorAlarm:all alarm is off in environment={},excludeEnvironment={}", environment, JSON.toJSONString(excludeEnvironment));
            return;
        }
        long start = System.currentTimeMillis();
        try {
            // 超阈值次数, 默认3次
            int thresholdCount = diamondConfig.getMonitorAlarmThresholdCount() == null ? 3 : diamondConfig.getMonitorAlarmThresholdCount();

            redisDao.getRedisTemplate().execute(new SessionCallback<Object>() {
                @Override
                public Object execute(RedisOperations redisOperations) throws DataAccessException {
                    for (EStatType statType : EStatType.values()) {
                        if (statType == EStatType.TOTAL) {
                            continue;
                        }
                        String alarmKey = RedisKeyHelper.keyOfAllAlarm(statType);
                        String alarmTimesKey = RedisKeyHelper.keyOfAllAlarmTimes(statType);
                        Object flag = redisOperations.opsForValue().get(alarmKey);
                        logger.info("TaskMonitorAlarm:alarm job running : {}={}  thresholdCount={} 。。。", alarmKey, flag, thresholdCount);
                        if (flag == null) {
                            continue;
                        }
                        Integer alarmNums = Integer.valueOf(flag.toString());
                        if (alarmNums >= thresholdCount) {
                            Set<String> alarmTimesStrSet = redisOperations.opsForSet().members(alarmTimesKey);
                            Set<Date> alarmTimesSet = Sets.newHashSet();
                            alarmTimesStrSet.forEach(t -> {
                                Long time = Long.valueOf(t);
                                alarmTimesSet.add(new Date(time));
                            });
                            List<Date> alarmTimesList = Lists.newArrayList(alarmTimesSet).stream().sorted(Date::compareTo).collect(Collectors.toList());
                            List<Date> needAlarmTimesList = Lists.newArrayList();
                            if (alarmNums > thresholdCount) {
                                needAlarmTimesList = alarmTimesList.subList(0, thresholdCount);
                            } else {
                                needAlarmTimesList = alarmTimesList;
                            }
                            logger.info("TaskMonitorAlarm:alarm job running : {}={}  {}={} {}={} thresholdCount={}。。。",
                                    alarmKey, flag, alarmTimesKey, JSON.toJSONString(alarmTimesStrSet), needAlarmTimesList, JSON.toJSONString(needAlarmTimesList), thresholdCount);
                            allAlarmService.alarm(statType, needAlarmTimesList);
                            if (alarmNums > thresholdCount) {
                                List<String> needAlarmTimeStrList = needAlarmTimesList.stream().map(o -> o.getTime() + "").collect(Collectors.toList());
                                redisOperations.opsForValue().increment(alarmKey, -thresholdCount);
                                redisOperations.opsForSet().remove(alarmTimesKey, needAlarmTimeStrList.toArray());
                            } else {
                                redisOperations.delete(alarmKey);
                                redisOperations.delete(alarmTimesKey);
                            }
                        }
                    }
                    return null;
                }
            });

        } catch (Exception e) {
            logger.error("alarm job exception ", e);
        } finally {
            logger.info("alarm job completed cost {} ms", (System.currentTimeMillis() - start));
        }
    }

    /**
     * 监控消息
     */
    private void alarm() {
        long start = System.currentTimeMillis();
        try {
            // 超阈值次数, 默认3次
            int thresholdCount = diamondConfig.getMonitorAlarmThresholdCount() == null ? 3 : diamondConfig.getMonitorAlarmThresholdCount();

            redisDao.getRedisTemplate().execute(new SessionCallback<Object>() {
                @Override
                public Object execute(RedisOperations redisOperations) throws DataAccessException {
                    // appId列表
                    String appIdKey = RedisKeyHelper.keyOfAppIds();
                    Set<String> appIdSet = redisOperations.opsForSet().members(appIdKey);
                    if (CollectionUtils.isEmpty(appIdSet)) {
                        return null;
                    }
                    appIdSet.forEach(appId -> {
                        for (EStatType statType : EStatType.values()) {
                            if (statType == EStatType.TOTAL) {
                                continue;
                            }
                            String alarmKey = RedisKeyHelper.keyOfAlarm(appId, statType);
                            Object flag = redisOperations.opsForValue().get(alarmKey);
                            logger.info("alarm job running : {}={}  thresholdCount={} 。。。", alarmKey, flag, thresholdCount);
                            if (flag == null) {
                                continue;
                            }
                            Integer alarmNums = Integer.valueOf(flag.toString());
                            if (alarmNums >= thresholdCount) {
                                alarmService.alarm(appId, statType);
                                redisOperations.delete(alarmKey);
                            }
                        }
                    });
                    return null;
                }
            });

        } catch (Exception e) {
            logger.error("alarm job exception ", e);
        } finally {
            logger.info("alarm job completed cost {} ms", (System.currentTimeMillis() - start));
        }
    }

    /**
     * 保存日合计数据
     *
     * @param intervalTimes   统计时间
     * @param appIds          商户ID
     * @param redisOperations
     */
    private void saveTotalDayData(Set<Date> intervalTimes, Set<String> appIds, RedisOperations redisOperations) {
        try {
            List<MerchantStatDayAccessDTO> totalList = Lists.newArrayList();
            intervalTimes.forEach(intervalTime -> {
                // 统计总数
                appIds.forEach(appId -> {
                    for (EStatType type : EStatType.values()) {
                        String totalDaykey = RedisKeyHelper.keyOfTotalDay(appId, intervalTime, type);
                        Map<String, Object> totalMap = redisOperations.opsForHash().entries(totalDaykey);
                        if (logger.isDebugEnabled()) {
                            logger.debug("key={} , value={}", totalDaykey, JSON.toJSONString(totalMap));
                        }
                        if (MapUtils.isEmpty(totalMap)) {
                            continue;
                        }
                        String json = JSON.toJSONString(totalMap);
                        MerchantStatDayAccessDTO dto = JSON.parseObject(json, MerchantStatDayAccessDTO.class);
                        dto.setDataType(type.getType());
                        dto.setAppId(appId);
                        if (dto.getTotalCount() == null) {
                            dto.setTotalCount(0);
                        }
                        if (dto.getSuccessCount() == null) {
                            dto.setSuccessCount(0);
                        }
                        if (dto.getFailCount() == null) {
                            dto.setFailCount(0);
                        }
                        if (dto.getCancelCount() == null) {
                            dto.setCancelCount(0);
                        }
                        Date dataTime = dto.getDataTime();
                        if (dataTime != null) {
                            dto.setDataTime(DateUtils.truncate(dataTime, Calendar.DAY_OF_MONTH));
                        }
                        dto.setSuccessRate(calcRate(dto.getTotalCount(), dto.getCancelCount(), dto.getSuccessCount()));
                        dto.setFailRate(calcRate(dto.getTotalCount(), dto.getCancelCount(), dto.getFailCount()));
                        dto.setLastUpdateTime(new Date());
                        totalList.add(dto);
                    }
                });
            });
            if (CollectionUtils.isNotEmpty(totalList)) {
                logger.info("saveTotalData : data={}", JSON.toJSONString(totalList));
                statAccessUpdateService.batchInsertStaDayAccess(totalList);
            }
        } catch (Exception e) {
            logger.error("saveTotalData error: intervalTimes=" + JSON.toJSONString(intervalTimes) + ",appIds=" + JSON.toJSONString(appIds) + " : ", e);
        }
    }

    /**
     * 保存合计所有商户后的日合计数据
     *
     * @param intervalTimes   统计时间
     * @param redisOperations
     */
    private void saveAllTotalDayData(Set<Date> intervalTimes, RedisOperations redisOperations) {
        try {
            List<SaasStatDayAccessDTO> totalList = Lists.newArrayList();
            intervalTimes.forEach(intervalTime -> {
                for (EStatType type : EStatType.values()) {
                    String totalDaykey = RedisKeyHelper.keyOfAllTotalDay(intervalTime, type);
                    Map<String, Object> totalMap = redisOperations.opsForHash().entries(totalDaykey);
                    if (logger.isDebugEnabled()) {
                        logger.debug("key={} , value={}", totalDaykey, JSON.toJSONString(totalMap));
                    }
                    if (MapUtils.isEmpty(totalMap)) {
                        continue;
                    }
                    String json = JSON.toJSONString(totalMap);
                    SaasStatDayAccessDTO dto = JSON.parseObject(json, SaasStatDayAccessDTO.class);
                    dto.setId(UidGenerator.getId());
                    dto.setDataType(type.getType());
                    Date dataTime = dto.getDataTime();
                    if (dataTime != null) {
                        dto.setDataTime(DateUtils.truncate(dataTime, Calendar.DAY_OF_MONTH));
                    }
                    if (dto.getTotalCount() == null) {
                        dto.setTotalCount(0);
                    }
                    if (dto.getSuccessCount() == null) {
                        dto.setSuccessCount(0);
                    }
                    if (dto.getFailCount() == null) {
                        dto.setFailCount(0);
                    }
                    if (dto.getCancelCount() == null) {
                        dto.setCancelCount(0);
                    }
                    dto.setSuccessRate(calcRate(dto.getTotalCount(), dto.getCancelCount(), dto.getSuccessCount()));
                    dto.setFailRate(calcRate(dto.getTotalCount(), dto.getCancelCount(), dto.getFailCount()));
                    dto.setLastUpdateTime(new Date());
                    totalList.add(dto);
                }

            });
            if (CollectionUtils.isNotEmpty(totalList)) {
                logger.info("saveTotalData : data={}", JSON.toJSONString(totalList));
                saasStatAccessUpdateService.batchInsertStaDayAccess(totalList);
            }
        } catch (Exception e) {
            logger.error("saveTotalData error: intervalTimes=" + JSON.toJSONString(intervalTimes) + " : ", e);
        }
    }

    /**
     * 保存合计数据
     *
     * @param intervalTimes   统计时间
     * @param redisOperations
     */
    private void saveAllTotalData(Set<Date> intervalTimes, RedisOperations redisOperations) {
        try {
            List<SaasStatAccessDTO> totalList = Lists.newArrayList();
            intervalTimes.forEach(intervalTime -> {

                for (EStatType type : EStatType.values()) {
                    String totalkey = RedisKeyHelper.keyOfAllTotal(intervalTime, type);
                    Map<String, Object> totalMap = redisOperations.opsForHash().entries(totalkey);
                    if (MapUtils.isEmpty(totalMap)) {
                        continue;
                    }
                    if (logger.isDebugEnabled()) {
                        logger.debug("TaskMonitorAlarm:保存合计商户后的合计数据:key={} , value={}", totalkey, JSON.toJSONString(totalMap));
                    }
                    String json = JSON.toJSONString(totalMap);
                    SaasStatAccessDTO dto = JSON.parseObject(json, SaasStatAccessDTO.class);
                    dto.setId(UidGenerator.getId());
                    dto.setDataType(type.getType());
                    if (dto.getTotalCount() == null) {
                        dto.setTotalCount(0);
                    }
                    if (dto.getSuccessCount() == null) {//如果未统计到成功数量,redis中不存在successCount,这里会为空.赋值为0,防止出现计算转化率为null的错误情况
                        dto.setSuccessCount(0);
                    }
                    if (dto.getFailCount() == null) {
                        dto.setFailCount(0);
                    }
                    if (dto.getCancelCount() == null) {
                        dto.setCancelCount(0);
                    }
                    dto.setSuccessRate(calcRate(dto.getTotalCount(), dto.getCancelCount(), dto.getSuccessCount()));
                    dto.setFailRate(calcRate(dto.getTotalCount(), dto.getCancelCount(), dto.getFailCount()));
                    dto.setLastUpdateTime(new Date());
                    totalList.add(dto);
                }
            });
            if (CollectionUtils.isNotEmpty(totalList)) {
                logger.info("TaskMonitorAlarm:saveTotalData : data={}", JSON.toJSONString(totalList));
                saasStatAccessUpdateService.batchInsertStatAccess(totalList);

                // 未设置预警阀值
                if (diamondConfig.getMonitorAlarmThreshold() == null) {
                    return;
                }
                //获取需要排除的商户的统计信息
                List<String> excludeAppIds = Lists.newArrayList();
                if (StringUtils.isNotBlank(diamondConfig.getMonitorAlarmExcludeAppIdsAll())) {
                    excludeAppIds = Splitter.on(",").trimResults().splitToList(diamondConfig.getMonitorAlarmExcludeAppIdsAll());
                    logger.info("TaskMonitorAlarm: excludeAppIds={}", JSON.toJSONString(excludeAppIds));
                }
                List<MerchantStatAccessDTO> excludeAppTotalDataList = this.getExcludeAppTotalDataList(intervalTimes, excludeAppIds, redisOperations);

                BigDecimal alarmThreshold = BigDecimal.valueOf(diamondConfig.getMonitorAlarmThreshold());
                BigDecimal alarmThresholdMax = BigDecimal.valueOf(diamondConfig.getMonitorAlarmThresholdMax());
                List<SaasStatAccessDTO> sortedTotalList = totalList.stream()
                        .sorted((o1, o2) -> o1.getDataTime().compareTo(o2.getDataTime())).collect(Collectors.toList());
                for (SaasStatAccessDTO dto : sortedTotalList) {
                    EStatType statType = EStatType.getById(dto.getDataType());
                    if (statType == EStatType.TOTAL) {
                        continue;
                    }
                    List<MerchantStatAccessDTO> filterExcludeAppTotalDataList = excludeAppTotalDataList.stream()
                            .filter(data -> data.getDataType().equals(dto.getDataType()) && data.getDataTime().equals(dto.getDataTime()))
                            .collect(Collectors.toList());
                    try {
                        // 成功率= 成功数/总数
                        BigDecimal successRate = calcTotalRate(dto.getSuccessCount(), dto.getTotalCount(), filterExcludeAppTotalDataList);
                        // 成功率 > 阀值， 清零计数
                        String alarmKey = RedisKeyHelper.keyOfAllAlarm(statType);
                        String alarmTimesKey = RedisKeyHelper.keyOfAllAlarmTimes(statType);
                        // 没有成功、失败，跳过
                        if ((dto.getFailCount() == null || dto.getFailCount() == 0)
                                && (dto.getSuccessCount() == null || dto.getSuccessCount() == 0)) {
                            logger.info(" TaskMonitorAlarm:update alarm flag :成功失败数量均为0,不做统计预警: alarmKey={}, value={}, dto={},successRate={}",
                                    alarmKey, redisOperations.opsForValue().get(alarmKey), JSON.toJSONString(dto), successRate);
                            continue;
                        }
                        if (successRate == null) {
                            logger.info("TaskMonitorAlarm:update alarm flag :转化率为null,数据存在问题或这段时间所统计商户均被排除,过滤此数据: alarmKey={}, value={}, dto={}",
                                    alarmKey, redisOperations.opsForValue().get(alarmKey), JSON.toJSONString(dto));
                            continue;
                        }
                        if (successRate.compareTo(alarmThreshold) >= 0 && successRate.compareTo(alarmThresholdMax) <= 0) {
                            logger.info(" TaskMonitorAlarm:update alarm flag :转化率在合法阈值之内: alarmKey={}, value={}, dto={},successRate={}",
                                    alarmKey, 0, JSON.toJSONString(dto), successRate);
                            redisOperations.delete(alarmKey);
                            redisOperations.delete(alarmTimesKey);
                        }
                        // 成功率 < 阀值， 计数器+1
                        else {
                            //
                            String timeKey = Joiner.on(":").useForNull("null").join(alarmTimesKey, dto.getDataTime().getTime() + "").toString();
                            Boolean flag = redisOperations.opsForValue().setIfAbsent(timeKey, "1");
                            redisOperations.expire(timeKey, 1, TimeUnit.DAYS);
                            if (flag) {
                                Long result = redisOperations.opsForValue().increment(alarmKey, 1);
                                redisOperations.opsForSet().add(alarmTimesKey, dto.getDataTime().getTime() + "");
                                logger.info("TaskMonitorAlarm:update alarm flag :转化率不在合法阈值范围内,预警值+1: alarmKey={}, value={}, dto={},successRate={}",
                                        alarmKey, result, JSON.toJSONString(dto), successRate);
                            } else {
                                logger.info("TaskMonitorAlarm:has alarmed this time. alarmKey={},timeKey={}", alarmKey, timeKey);
                            }
                        }
                    } catch (Exception e) {
                        logger.error("TaskMonitorAlarm:update alarm flag error: data={}", JSON.toJSONString(dto), e);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("TaskMonitorAlarm:saveTotalData error: intervalTimes=" + JSON.toJSONString(intervalTimes) + " : ", e);
        }
    }

    private List<MerchantStatAccessDTO> getExcludeAppTotalDataList(Set<Date> intervalTimes, List<String> excludeAppIds, RedisOperations redisOperations) {
        List<MerchantStatAccessDTO> totalList = Lists.newArrayList();
        intervalTimes.forEach(intervalTime -> {
            // 统计总数
            excludeAppIds.forEach(appId -> {
                for (EStatType type : EStatType.values()) {
                    String totalkey = RedisKeyHelper.keyOfTotal(appId, intervalTime, type);
                    Map<String, Object> totalMap = redisOperations.opsForHash().entries(totalkey);
                    if (logger.isDebugEnabled()) {
                        logger.debug("TaskMonitorAlarm: excludeAppData : key={} , value={}", totalkey, JSON.toJSONString(totalMap));
                    }
                    if (MapUtils.isEmpty(totalMap)) {
                        continue;
                    }
                    String json = JSON.toJSONString(totalMap);
                    MerchantStatAccessDTO dto = JSON.parseObject(json, MerchantStatAccessDTO.class);
                    dto.setDataType(type.getType());
                    dto.setAppId(appId);
                    if (dto.getTotalCount() == null) {
                        dto.setTotalCount(0);
                    }
                    if (dto.getSuccessCount() == null) {
                        dto.setSuccessCount(0);
                    }
                    if (dto.getFailCount() == null) {
                        dto.setFailCount(0);
                    }
                    if (dto.getCancelCount() == null) {
                        dto.setCancelCount(0);
                    }
                    dto.setSuccessRate(calcRate(dto.getTotalCount(), dto.getCancelCount(), dto.getSuccessCount()));
                    dto.setFailRate(calcRate(dto.getTotalCount(), dto.getCancelCount(), dto.getFailCount()));
                    dto.setLastUpdateTime(new Date());
                    totalList.add(dto);
                }
            });
        });
        return totalList;
    }

    /**
     * 保存合计数据
     *
     * @param intervalTimes   统计时间
     * @param appIds          商户ID
     * @param redisOperations
     */
    private void saveTotalData(Set<Date> intervalTimes, Set<String> appIds, RedisOperations redisOperations) {
        try {
            List<MerchantStatAccessDTO> totalList = Lists.newArrayList();
            intervalTimes.forEach(intervalTime -> {
                // 统计总数
                appIds.forEach(appId -> {
                    for (EStatType type : EStatType.values()) {
                        String totalkey = RedisKeyHelper.keyOfTotal(appId, intervalTime, type);
                        Map<String, Object> totalMap = redisOperations.opsForHash().entries(totalkey);
                        if (logger.isDebugEnabled()) {
                            logger.debug("key={} , value={}", totalkey, JSON.toJSONString(totalMap));
                        }
                        if (MapUtils.isEmpty(totalMap)) {
                            continue;
                        }
                        String json = JSON.toJSONString(totalMap);
                        MerchantStatAccessDTO dto = JSON.parseObject(json, MerchantStatAccessDTO.class);
                        dto.setDataType(type.getType());
                        dto.setAppId(appId);
                        if (dto.getTotalCount() == null) {
                            dto.setTotalCount(0);
                        }
                        if (dto.getSuccessCount() == null) {
                            dto.setSuccessCount(0);
                        }
                        if (dto.getFailCount() == null) {
                            dto.setFailCount(0);
                        }
                        if (dto.getCancelCount() == null) {
                            dto.setCancelCount(0);
                        }
                        dto.setSuccessRate(calcRate(dto.getTotalCount(), dto.getCancelCount(), dto.getSuccessCount()));
                        dto.setFailRate(calcRate(dto.getTotalCount(), dto.getCancelCount(), dto.getFailCount()));
                        dto.setLastUpdateTime(new Date());
                        totalList.add(dto);
                    }
                });
            });
            if (CollectionUtils.isNotEmpty(totalList)) {
                logger.info("saveTotalData : data={}", JSON.toJSONString(totalList));
                statAccessUpdateService.batchInsertStatAccess(totalList);

                // 未设置预警阀值
                if (diamondConfig.getMonitorAlarmThreshold() == null) {
                    return;
                }
                BigDecimal alarmThreshold = BigDecimal.valueOf(diamondConfig.getMonitorAlarmThreshold());
                for (MerchantStatAccessDTO dto : totalList) {
                    String appId = dto.getAppId();
                    EStatType statType = EStatType.getById(dto.getDataType());
                    if (statType == EStatType.TOTAL) {
                        continue;
                    }
                    // 成功率 > 阀值， 清零计数
                    String alarmKey = RedisKeyHelper.keyOfAlarm(appId, statType);
                    try {
                        BigDecimal successRate = dto.getSuccessRate();
                        // 没有成功、失败，跳过
                        if ((dto.getFailCount() == null || dto.getFailCount() == 0)
                                && (dto.getSuccessCount() == null || dto.getSuccessCount() == 0)) {
                            logger.info(" update alarm flag : alarmKey={}, value={}, dto={}", alarmKey, redisOperations.opsForValue().get(alarmKey), JSON.toJSONString(dto));
                            continue;
                        }
                        if (successRate != null && successRate.compareTo(alarmThreshold) >= 0) {
                            logger.info(" update alarm flag : alarmKey={}, value={}, dto={}", alarmKey, 0, JSON.toJSONString(dto));
                            redisOperations.delete(alarmKey);
                        }
                        // 成功率 > 阀值， 计数器+1
                        else {
                            Long result = redisOperations.opsForValue().increment(alarmKey, 1);
                            logger.info(" update alarm flag : alarmKey={}, value={}, dto={}", alarmKey, result, JSON.toJSONString(dto));
                        }

                    } catch (Exception e) {
                        logger.error("update alarm flag error: data=" + JSON.toJSONString(dto), e);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("saveTotalData error: intervalTimes=" + JSON.toJSONString(intervalTimes) + ",appIds=" + JSON.toJSONString(appIds) + " : ", e);
        }
    }


    /**
     * 保存电商数据
     *
     * @param intervalTimes
     * @param appIds
     * @param redisOperations
     */
    private void saveEcommerceData(Set<Date> intervalTimes, Set<String> appIds, RedisOperations redisOperations) {
        try {
            List<EcommerceDTO> ecommerceList = ecommerceService.getAll();
            if (CollectionUtils.isEmpty(ecommerceList)) {
                return;
            }
            List<MerchantStatEcommerceDTO> dataList = Lists.newArrayList();

            intervalTimes.forEach(intervalTime -> {
                appIds.forEach(appId -> {
                    for (EcommerceDTO ecommerceDTO : ecommerceList) {
                        Short ecommerceId = ecommerceDTO.getId();
                        String hashKey = RedisKeyHelper.keyOfEcommerce(appId, intervalTime, ecommerceId);
                        if (redisOperations.hasKey(hashKey)) {
                            Map<String, Object> dataMap = redisOperations.opsForHash().entries(hashKey);
                            if (logger.isDebugEnabled()) {
                                logger.debug("key={} , value={}", hashKey, JSON.toJSONString(dataMap));
                            }
                            if (MapUtils.isEmpty(dataMap)) {
                                continue;
                            }
                            String json = JSON.toJSONString(dataMap);
                            MerchantStatEcommerceDTO dto = JSON.parseObject(json, MerchantStatEcommerceDTO.class);
                            dto.setEcommerceId(ecommerceId);
                            dto.setAppId(appId);
                            if (dto.getTotalCount() == null) {
                                dto.setTotalCount(0);
                            }
                            if (dto.getSuccessCount() == null) {
                                dto.setSuccessCount(0);
                            }
                            if (dto.getFailCount() == null) {
                                dto.setFailCount(0);
                            }
                            if (dto.getCancelCount() == null) {
                                dto.setCancelCount(0);
                            }
                            dto.setSuccessRate(calcRate(dto.getTotalCount(), dto.getCancelCount(), dto.getSuccessCount()));
                            dto.setFailRate(calcRate(dto.getTotalCount(), dto.getCancelCount(), dto.getFailCount()));
                            dto.setLastUpdateTime(new Date());
                            dataList.add(dto);
                        }
                    }
                });
            });
            if (CollectionUtils.isNotEmpty(dataList)) {
                logger.info("saveEcommerceData : data={}", JSON.toJSONString(dataList));
                statAccessUpdateService.batchInsertEcommerce(dataList);
            }
        } catch (Exception e) {
            logger.error("saveEcommerceData error: intervalTimes=" + JSON.toJSONString(intervalTimes) + ",appIds=" + JSON.toJSONString(appIds) + " : ", e);
        }
    }

    /**
     * 邮箱数据保存
     *
     * @param intervalTimes
     * @param appIds
     * @param redisOperations
     */
    private void saveMailData(Set<Date> intervalTimes, Set<String> appIds, RedisOperations redisOperations) {
        try {
            List<WebsiteDTO> mails = websiteService.getSupportMails();
            if (CollectionUtils.isEmpty(mails)) {
                return;
            }
            List<MerchantStatMailDTO> dataList = Lists.newArrayList();

            intervalTimes.forEach(intervalTime -> {
                appIds.forEach(appId -> {
                    for (WebsiteDTO mail : mails) {
                        String mailCode = mail.getWebsiteName();
                        String hashKey = RedisKeyHelper.keyOfMail(appId, intervalTime, mailCode);
                        if (redisOperations.hasKey(hashKey)) {
                            Map<String, Object> dataMap = redisOperations.opsForHash().entries(hashKey);
                            if (logger.isDebugEnabled()) {
                                logger.debug("key={} , value={}", hashKey, JSON.toJSONString(dataMap));
                            }
                            if (MapUtils.isEmpty(dataMap)) {
                                continue;
                            }
                            String json = JSON.toJSONString(dataMap);
                            MerchantStatMailDTO dto = JSON.parseObject(json, MerchantStatMailDTO.class);
                            dto.setAppId(appId);
                            if (dto.getTotalCount() == null) {
                                dto.setTotalCount(0);
                            }
                            if (dto.getSuccessCount() == null) {
                                dto.setSuccessCount(0);
                            }
                            if (dto.getFailCount() == null) {
                                dto.setFailCount(0);
                            }
                            if (dto.getCancelCount() == null) {
                                dto.setCancelCount(0);
                            }
                            dto.setMailCode(mailCode);
                            dto.setSuccessRate(calcRate(dto.getTotalCount(), dto.getCancelCount(), dto.getSuccessCount()));
                            dto.setFailRate(calcRate(dto.getTotalCount(), dto.getCancelCount(), dto.getFailCount()));
                            dto.setLastUpdateTime(new Date());
                            dataList.add(dto);
                        }
                    }
                });
            });
            if (CollectionUtils.isNotEmpty(dataList)) {
                logger.info("saveMailData : data={}", JSON.toJSONString(dataList));
                statAccessUpdateService.batchInsertMail(dataList);
            }
        } catch (Exception e) {
            logger.error("saveMailData error: intervalTimes=" + JSON.toJSONString(intervalTimes) + ",appIds=" + JSON.toJSONString(appIds) + " : ", e);
        }
    }


    /**
     * 运营商数据保存
     *
     * @param intervalTimes
     * @param appIds
     * @param redisOperations
     */
    private void saveOperatorData(Set<Date> intervalTimes, Set<String> appIds, RedisOperations redisOperations) {
        try {
            List<OperatorDTO> operators = operatorService.getAll();
            if (CollectionUtils.isEmpty(operators)) {
                return;
            }
            List<MerchantStatOperatorDTO> dataList = Lists.newArrayList();

            intervalTimes.forEach(intervalTime -> {
                appIds.forEach(appId -> {
                    for (OperatorDTO operatorDTO : operators) {
                        String operatorId = operatorDTO.getId().toString();
                        String hashKey = RedisKeyHelper.keyOfOperator(appId, intervalTime, operatorId);
                        if (redisOperations.hasKey(hashKey)) {
                            Map<String, Object> dataMap = redisOperations.opsForHash().entries(hashKey);
                            if (logger.isDebugEnabled()) {
                                logger.debug("key={} , value={}", hashKey, JSON.toJSONString(dataMap));
                            }
                            if (MapUtils.isEmpty(dataMap)) {
                                continue;
                            }
                            String json = JSON.toJSONString(dataMap);
                            MerchantStatOperatorDTO dto = JSON.parseObject(json, MerchantStatOperatorDTO.class);
                            dto.setAppId(appId);
                            if (dto.getTotalCount() == null) {
                                dto.setTotalCount(0);
                            }
                            if (dto.getSuccessCount() == null) {
                                dto.setSuccessCount(0);
                            }
                            if (dto.getFailCount() == null) {
                                dto.setFailCount(0);
                            }
                            if (dto.getCancelCount() == null) {
                                dto.setCancelCount(0);
                            }
                            dto.setOperaterId(operatorId);
                            dto.setSuccessRate(calcRate(dto.getTotalCount(), dto.getCancelCount(), dto.getSuccessCount()));
                            dto.setFailRate(calcRate(dto.getTotalCount(), dto.getCancelCount(), dto.getFailCount()));
                            dto.setLastUpdateTime(new Date());
                            dataList.add(dto);
                        }
                    }
                });
            });
            if (CollectionUtils.isNotEmpty(dataList)) {
                logger.info("saveOperatorData : data={}", JSON.toJSONString(dataList));
                statAccessUpdateService.batchInsertOperator(dataList);
            }
        } catch (Exception e) {
            logger.error("saveOperatorData error: intervalTimes=" + JSON.toJSONString(intervalTimes) + ",appIds=" + JSON.toJSONString(appIds) + " : ", e);
        }
    }

    /**
     * 保存任务失败取消统计数据
     *
     * @param intervalTimes   统计时间
     * @param redisOperations
     */
    private void saveAllErrorDayData(Set<Date> intervalTimes, RedisOperations redisOperations) {
        try {
            List<SaasErrorStepDayStatDTO> totalList = Lists.newArrayList();
            intervalTimes.forEach(intervalTime -> {
                for (EStatType type : EStatType.values()) {
                    for (TaskStepEnum taskStep : TaskStepEnum.values()) {
                        String errorDayKey = RedisKeyHelper.keyOfAllErrorDay(intervalTime, type, taskStep.getStepCode());
                        Map<String, Object> errorMap = redisOperations.opsForHash().entries(errorDayKey);
                        if (logger.isDebugEnabled()) {
                            logger.debug("key={} , value={}", errorDayKey, JSON.toJSONString(errorMap));
                        }
                        if (MapUtils.isEmpty(errorMap)) {
                            continue;
                        }
                        String json = JSON.toJSONString(errorMap);
                        SaasErrorStepDayStatDTO dto = JSON.parseObject(json, SaasErrorStepDayStatDTO.class);
                        dto.setId(UidGenerator.getId());
                        dto.setDataType(type.getType());
                        dto.setErrorStepCode(taskStep.getStepCode());
                        Date dataTime = dto.getDataTime();
                        if (dataTime != null) {
                            dto.setDataTime(DateUtils.truncate(dataTime, Calendar.DAY_OF_MONTH));
                        }
                        dto.setLastUpdateTime(new Date());
                        totalList.add(dto);
                    }
                }


            });
            if (CollectionUtils.isNotEmpty(totalList)) {
                logger.info("saveAllErrorData : data={}", JSON.toJSONString(totalList));
                saasErrorDayStatUpdateService.batchInsertErrorDayStat(totalList);
            }
        } catch (Exception e) {
            logger.error("saveTotalData error: intervalTimes=" + JSON.toJSONString(intervalTimes) + " : ", e);
        }
    }


    /**
     * 计算比率
     *
     * @param totalCount 总数
     * @param rateCount  比率数
     * @return
     */
    private BigDecimal calcRate(Integer totalCount, Integer cancelCount, Integer rateCount) {
        if (cancelCount != null) {
            totalCount -= cancelCount;
        }
        if (totalCount == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal rate = BigDecimal.valueOf(rateCount, 2)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalCount, 2), 2);
        return rate;
    }


    /**
     * 计算比率
     *
     * @param rateCount  比率数
     * @param totalCount 总数
     * @param list       需要排除的商户统计数据
     * @return
     */
    private BigDecimal calcTotalRate(Integer rateCount, Integer totalCount, List<MerchantStatAccessDTO> list) {
        logger.info("TaskMonitorAlarm:update alarm flag:计算转化率:rateCount={},totalCount={},excludeAppDatas={}",
                rateCount, totalCount, JSON.toJSONString(list));
        if (CollectionUtils.isNotEmpty(list)) {
            for (MerchantStatAccessDTO dto : list) {
                rateCount = rateCount - dto.getSuccessCount();
                totalCount = totalCount - dto.getTotalCount();
            }
        }
        if (rateCount < 0 || totalCount <= 0) {
            return null;
        }
        BigDecimal rate = BigDecimal.valueOf(rateCount, 2)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalCount, 2), 2);
        return rate;
    }


}
