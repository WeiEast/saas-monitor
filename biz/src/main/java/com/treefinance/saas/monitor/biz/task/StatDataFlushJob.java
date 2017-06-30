package com.treefinance.saas.monitor.biz.task;

import com.alibaba.fastjson.JSON;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.treefinance.saas.monitor.biz.config.DiamondConfig;
import com.treefinance.saas.monitor.biz.helper.RedisKeyHelper;
import com.treefinance.saas.monitor.biz.helper.StatHelper;
import com.treefinance.saas.monitor.biz.service.*;
import com.treefinance.saas.monitor.common.cache.RedisDao;
import com.treefinance.saas.monitor.common.domain.dto.*;
import com.treefinance.saas.monitor.common.enumeration.EStatType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;

import java.math.BigDecimal;
import java.util.*;

/**
 * 数据统计Job
 * Created by yh-treefinance on 2017/5/25.
 */
public class StatDataFlushJob implements SimpleJob {
    private static final Logger logger = LoggerFactory.getLogger(StatDataFlushJob.class);
    @Autowired
    private RedisDao redisDao;
    @Autowired
    private DiamondConfig diamondConfig;
    @Autowired
    private EcommerceService ecommerceService;
    @Autowired
    private WebsiteService websiteService;
    @Autowired
    private OperatorService operatorService;
    @Autowired
    private StatAccessUpdateService statAccessUpdateService;

    @Override
    public void execute(ShardingContext shardingContext) {
        long start = System.currentTimeMillis();
        try {
            int intervalMinutes = diamondConfig.getMonitorIntervalMinutes();
            logger.info("intervalMinutes={}", intervalMinutes);
            Date now = new Date();
            Date intervalTime = StatHelper.calculateIntervalTime(now, intervalMinutes);
            String currentInterval = intervalTime.getTime() + "";

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

                    // 6.删除已生成数据key
                    List<String> deleteList = Lists.newArrayList();
                    intervalSets.forEach(time -> {
                        if (!time.equals(currentInterval)) {
                            deleteList.add(time);
                        }
                    });
                    if (!deleteList.isEmpty()) {
                        logger.info("刷新数据完成，清除数据：deleteList={}", JSON.toJSONString(deleteList));
                        redisOperations.opsForSet().remove(dayKey, deleteList.toArray(new String[]{}));
                    }
                    return null;
                }
            });
        } catch (Exception e) {
            logger.error("statdataflushjob exception : ", e);
        } finally {
            logger.info("定时刷新数据完成，耗时time={}ms", System.currentTimeMillis() - start);
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
                    BigDecimal successRate = dto.getSuccessRate();
                    // 无成功率，不计数
                    if (successRate == null) {
                        continue;
                    }
                    // 成功率 > 阀值， 清零计数
                    String alarmKey = RedisKeyHelper.keyOfAlarm(appId, statType);
                    // 没有成功、失败，跳过
                    if ((dto.getFailCount() == null || dto.getFailCount() == 0)
                            && (dto.getSuccessCount() == null || dto.getSuccessCount() == 0)) {
                        continue;
                    }
                    if (successRate.compareTo(alarmThreshold) >= 0) {
                        redisOperations.opsForValue().set(alarmKey, 0);
                    }
                    // 成功率 > 阀值， 计数器+1
                    else {
                        redisOperations.opsForValue().increment(alarmKey, 1);
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
     * 计算比率
     *
     * @param totalCount 总数
     * @param rateCount  比率数
     * @return
     */
    private BigDecimal calcRate(Integer totalCount, Integer cancelCount, Integer rateCount) {
        if (totalCount == null || rateCount == null) {
            return null;
        }
        if (totalCount == 0) {
            return null;
        }
        if (cancelCount != null) {
            totalCount -= cancelCount;
        }
        BigDecimal rate = BigDecimal.valueOf(rateCount, 2)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalCount, 2), 2);
        return rate;
    }
}
