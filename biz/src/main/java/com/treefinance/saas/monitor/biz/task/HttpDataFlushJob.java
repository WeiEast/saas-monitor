package com.treefinance.saas.monitor.biz.task;

import com.alibaba.fastjson.JSON;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.treefinance.saas.assistant.model.Constants;
import com.treefinance.saas.monitor.biz.config.DiamondConfig;
import com.treefinance.saas.monitor.biz.helper.RedisKeyHelper;
import com.treefinance.saas.monitor.biz.helper.StatHelper;
import com.treefinance.saas.monitor.biz.service.ApiStatAccessService;
import com.treefinance.saas.monitor.common.cache.RedisDao;
import com.treefinance.saas.monitor.facade.domain.ro.stat.api.ApiBaseStatRO;
import com.treefinance.saas.monitor.facade.domain.ro.stat.api.ApiStatAccessRO;
import com.treefinance.saas.monitor.facade.domain.ro.stat.api.ApiStatDayAccessRO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 数据统计Job
 * Created by yh-treefinance on 2017/5/25.
 */
public class HttpDataFlushJob implements SimpleJob {
    private static final Logger logger = LoggerFactory.getLogger(HttpDataFlushJob.class);
    @Autowired
    private RedisDao redisDao;
    @Autowired
    private DiamondConfig diamondConfig;
    @Autowired
    private ApiStatAccessService apiStatAccessService;

    @Override
    public void execute(ShardingContext shardingContext) {
        String saasEnv = Constants.SAAS_ENV;
        logger.info("定时任务执行,当前环境SAAS-ENV={}", saasEnv);
        if (StringUtils.isNotBlank(saasEnv) && StringUtils.equalsIgnoreCase(saasEnv, "pre-product")) {
            logger.info("定时任务,预发布环境暂不执行");
            return;
        }
        long start = System.currentTimeMillis();
        try {
            int intervalMinutes = diamondConfig.getMonitorIntervalMinutes();
            logger.info("intervalMinutes={}", intervalMinutes);
            Date now = new Date();
            Date intervalTime = StatHelper.calculateIntervalTime(now, intervalMinutes);
            String currentInterval = intervalTime.getTime() + "";

            Set<Date> intervalTimeSets = Sets.newHashSet();

            redisDao.getRedisTemplate().execute(new SessionCallback<Object>() {
                @Override
                public Object execute(RedisOperations redisOperations) throws DataAccessException {
                    // 统计时间
                    String dayKey = RedisKeyHelper.keyOfHttpDay(intervalTime);
                    String apiListKey = RedisKeyHelper.keyOfHttpApiList(intervalTime);

                    Set<String> intervalSets = redisOperations.opsForSet().members(dayKey);
                    if (CollectionUtils.isEmpty(intervalSets)) {
                        return null;
                    }
                    intervalSets.forEach(t -> {
                        Long time = Long.valueOf(t);
                        intervalTimeSets.add(new Date(time));
                    });

                    // 保存总计数据
                    saveTotalData(intervalTimeSets, redisOperations);
                    // 商户日数据
                    saveMerchantDayData(intervalTimeSets, redisOperations);
                    // api数据
                    saveApiData(intervalTimeSets, redisOperations);

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
            logger.error("httpdataflushjob exception : ", e);
        } finally {
            logger.info("定时刷新数据完成，耗时time={}ms", System.currentTimeMillis() - start);
        }
    }

    /**
     * 保存合计数据
     *
     * @param intervalTimeSets
     * @param redisOperations
     */
    private void saveTotalData(Set<Date> intervalTimeSets, RedisOperations redisOperations) {
        List<ApiBaseStatRO> totalList = Lists.newArrayList();

        for (Date intervalTime : intervalTimeSets) {
            String key = RedisKeyHelper.keyOfHttpTotal(intervalTime);
            Map<String, Object> totalMap = redisOperations.opsForHash().entries(key);
            if (logger.isDebugEnabled()) {
                logger.debug("key={} , value={}", key, JSON.toJSONString(totalMap));
            }
            if (MapUtils.isEmpty(totalMap)) {
                continue;
            }
            String json = JSON.toJSONString(totalMap);
            ApiBaseStatRO access = JSON.parseObject(json, ApiBaseStatRO.class);
            access.setDataTime(intervalTime);
            Integer avgResponseTime = access.getAvgResponseTime();
            Integer totalCount = access.getTotalCount();
            if (avgResponseTime == null || totalCount == null || totalCount == 0) {
                access.setAvgResponseTime(0);
            } else {
                access.setAvgResponseTime(avgResponseTime / totalCount);
            }
            totalList.add(access);
        }
        apiStatAccessService.batchInsertApiStatTotalAccess(totalList);
    }

    /**
     * 保存商户日访问数据
     *
     * @param intervalTimeSets
     * @param redisOperations
     */
    private void saveMerchantDayData(Set<Date> intervalTimeSets, RedisOperations redisOperations) {
        List<ApiStatDayAccessRO> dataList = Lists.newArrayList();

        for (Date intervalTime : intervalTimeSets) {
            String mercantListKey = RedisKeyHelper.keyOfHttpMerchantList(intervalTime);
            Set<String> merchantSet = redisOperations.opsForSet().members(mercantListKey);
            if (CollectionUtils.isEmpty(merchantSet)) {
                continue;
            }
            for (String appId : merchantSet) {
                String key = RedisKeyHelper.keyOfHttpMerchant(intervalTime, appId);
                Map<String, Object> totalMap = redisOperations.opsForHash().entries(key);
                if (logger.isDebugEnabled()) {
                    logger.debug("key={} , value={}", key, JSON.toJSONString(totalMap));
                }
                if (MapUtils.isEmpty(totalMap)) {
                    continue;
                }
                String json = JSON.toJSONString(totalMap);
                ApiStatDayAccessRO access = JSON.parseObject(json, ApiStatDayAccessRO.class);
                access.setDataTime(intervalTime);
                Integer avgResponseTime = access.getAvgResponseTime();
                Integer totalCount = access.getTotalCount();
                if (avgResponseTime == null || totalCount == null || totalCount == 0) {
                    access.setAvgResponseTime(0);
                } else {
                    access.setAvgResponseTime(avgResponseTime / totalCount);
                }
                dataList.add(access);
            }
        }
        apiStatAccessService.batchInsertApiStatMerchantDayAccess(dataList);
    }


    /**
     * 保存API访问数据
     *
     * @param intervalTimeSets
     * @param redisOperations
     */
    private void saveApiData(Set<Date> intervalTimeSets, RedisOperations redisOperations) {
        List<ApiStatAccessRO> dataList = Lists.newArrayList();

        for (Date intervalTime : intervalTimeSets) {
            String apiListKey = RedisKeyHelper.keyOfHttpApiList(intervalTime);
            Set<String> apiSet = redisOperations.opsForSet().members(apiListKey);
            if (CollectionUtils.isEmpty(apiSet)) {
                continue;
            }
            for (String appId : apiSet) {
                String key = RedisKeyHelper.keyOfHttpApi(intervalTime, appId);
                Map<String, Object> totalMap = redisOperations.opsForHash().entries(key);
                if (logger.isDebugEnabled()) {
                    logger.debug("key={} , value={}", key, JSON.toJSONString(totalMap));
                }
                if (MapUtils.isEmpty(totalMap)) {
                    continue;
                }
                String json = JSON.toJSONString(totalMap);
                ApiStatAccessRO access = JSON.parseObject(json, ApiStatAccessRO.class);
                access.setDataTime(intervalTime);
                Integer avgResponseTime = access.getAvgResponseTime();
                Integer totalCount = access.getTotalCount();
                if (avgResponseTime == null || totalCount == null || totalCount == 0) {
                    access.setAvgResponseTime(0);
                } else {
                    access.setAvgResponseTime(avgResponseTime / totalCount);
                }
                dataList.add(access);
            }
        }
        apiStatAccessService.batchInsertApiStatAccess(dataList);
    }

}
