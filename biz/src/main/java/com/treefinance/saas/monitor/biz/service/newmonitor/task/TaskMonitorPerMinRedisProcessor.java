package com.treefinance.saas.monitor.biz.service.newmonitor.task;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.treefinance.saas.assistant.model.TaskMonitorMessage;
import com.treefinance.saas.monitor.biz.helper.TaskMonitorPerMinKeyHelper;
import com.treefinance.saas.monitor.common.cache.RedisDao;
import com.treefinance.saas.monitor.common.enumeration.EStatType;
import com.treefinance.saas.monitor.common.enumeration.ETaskStatus;
import com.treefinance.saas.monitor.common.utils.MonitorDateUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by haojiahong on 2017/11/23.
 */
@Component
public class TaskMonitorPerMinRedisProcessor {

    private static final Logger logger = LoggerFactory.getLogger(TaskMonitorPerMinRedisProcessor.class);

    @Autowired
    private RedisDao redisDao;


    public void updateMerchantIntervalAccess(Date redisKeyTime, TaskMonitorMessage message, EStatType statType) {
        Map<String, String> statMap = Maps.newHashMap();
        String key = TaskMonitorPerMinKeyHelper.keyOfMerchantIntervalStat(redisKeyTime, message.getAppId(), statType);
        redisDao.getRedisTemplate().execute(new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {

                // 需统计的当日特定时间列表
                String dayKey = TaskMonitorPerMinKeyHelper.keyOfDayOnMerchantIntervalStat(redisKeyTime, statType);
                BoundSetOperations<String, String> setOperations = redisOperations.boundSetOps(dayKey);
                if (!Boolean.TRUE.equals(redisOperations.hasKey(dayKey))) {
                    setOperations.expire(2, TimeUnit.DAYS);
                }
                setOperations.add(MonitorDateUtils.format(redisKeyTime));

                //需统计的商户
                if (StringUtils.isNotBlank(message.getAppId())) {
                    String merchantsKey = TaskMonitorPerMinKeyHelper.keyOfAppIds();
                    redisOperations.opsForSet().add(merchantsKey, message.getAppId());
                }

                // 判断是否有key
                BoundHashOperations<String, String, String> hashOperations = redisOperations.boundHashOps(key);
                if (!Boolean.TRUE.equals(redisOperations.hasKey(key))) {
                    hashOperations.put("dataTime", MonitorDateUtils.format(redisKeyTime));
                    hashOperations.put("dataType", statType.getType().toString());
                    hashOperations.put("appId", message.getAppId());
                    statMap.put("dataTime", MonitorDateUtils.format(redisKeyTime));
                    statMap.put("dataType", statType + "");
                    statMap.put("appId", message.getAppId());
                    // 设定超时时间默认为2天
                    hashOperations.expire(1, TimeUnit.HOURS);
                }


                // 统计用户数: 未存在用户+1
                String userKey = TaskMonitorPerMinKeyHelper.keyOfUsersOnMerchantIntervalStat(redisKeyTime, message.getAppId(), statType);
                BoundSetOperations<String, String> userSetOperations = redisOperations.boundSetOps(userKey);
                if (!Boolean.TRUE.equals(redisOperations.hasKey(userKey))) {
                    userSetOperations.expire(1, TimeUnit.HOURS);
                }
                if (!userSetOperations.isMember(message.getUniqueId())) {
                    Long userCount = hashOperations.increment("userCount", 1);
                    statMap.put("userCount", userCount + "");
                }
                updateMerchantStatRedisData(hashOperations, message, statMap, redisKeyTime);
                return null;
            }
        });
        logger.info("任务监控,消息处理,message={},statMap={},key={},value={}",
                JSON.toJSONString(message), JSON.toJSONString(statMap), key, JSON.toJSONString(statMap));
    }

    public void updateMerchantDayAccess(Date redisKeyTime, TaskMonitorMessage message, EStatType statType) {
        Map<String, String> statMap = Maps.newHashMap();
        String key = TaskMonitorPerMinKeyHelper.keyOfMerchantDayStat(redisKeyTime, message.getAppId(), statType);
        redisDao.getRedisTemplate().execute(new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {

                //需统计的商户
                if (StringUtils.isNotBlank(message.getAppId())) {
                    String merchantsKey = TaskMonitorPerMinKeyHelper.keyOfAppIds();
                    redisOperations.opsForSet().add(merchantsKey, message.getAppId());
                }

                // 判断是否有key
                BoundHashOperations<String, String, String> hashOperations = redisOperations.boundHashOps(key);
                if (!Boolean.TRUE.equals(redisOperations.hasKey(key))) {
                    hashOperations.put("dataTime", MonitorDateUtils.getDayStartTimeStr(redisKeyTime));
                    hashOperations.put("dataType", statType.getType().toString());
                    hashOperations.put("appId", message.getAppId());
                    statMap.put("dataTime", MonitorDateUtils.getDayStartTimeStr(redisKeyTime));
                    statMap.put("dataType", statType + "");
                    statMap.put("appId", message.getAppId());
                    // 设定超时时间默认为2天
                    hashOperations.expire(2, TimeUnit.DAYS);
                }


                // 统计用户数: 未存在用户+1
                String userKey = TaskMonitorPerMinKeyHelper.keyOfUsersOnMerchantDayStat(redisKeyTime, message.getAppId(), statType);
                BoundSetOperations<String, String> userSetOperations = redisOperations.boundSetOps(userKey);
                if (!Boolean.TRUE.equals(redisOperations.hasKey(userKey))) {
                    userSetOperations.expire(2, TimeUnit.DAYS);
                }
                if (!userSetOperations.isMember(message.getUniqueId())) {
                    Long userCount = hashOperations.increment("userCount", 1);
                    statMap.put("userCount", userCount + "");
                }
                updateMerchantStatRedisData(hashOperations, message, statMap, redisKeyTime);
                return null;
            }
        });
        logger.info("任务监控,消息处理,message={},statMap={},key={},value={}",
                JSON.toJSONString(message), JSON.toJSONString(statMap), key, JSON.toJSONString(statMap));
    }

    public void updateSaasIntervalAccess(Date redisKeyTime, TaskMonitorMessage message, EStatType statType) {
        Map<String, String> statMap = Maps.newHashMap();
        String key = TaskMonitorPerMinKeyHelper.keyOfSaasIntervalStat(redisKeyTime, statType);
        redisDao.getRedisTemplate().execute(new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {

                // 需统计的当日特定时间列表
                String dayKey = TaskMonitorPerMinKeyHelper.keyOfDayOnSaasIntervalStat(redisKeyTime, statType);
                BoundSetOperations<String, String> setOperations = redisOperations.boundSetOps(dayKey);
                if (!Boolean.TRUE.equals(redisOperations.hasKey(dayKey))) {
                    setOperations.expire(2, TimeUnit.DAYS);
                }
                setOperations.add(MonitorDateUtils.format(redisKeyTime));

                // 判断是否有key
                BoundHashOperations<String, String, String> hashOperations = redisOperations.boundHashOps(key);
                if (!Boolean.TRUE.equals(redisOperations.hasKey(key))) {
                    hashOperations.put("dataTime", MonitorDateUtils.format(redisKeyTime));
                    hashOperations.put("dataType", statType.getType().toString());
                    statMap.put("dataTime", MonitorDateUtils.format(redisKeyTime));
                    statMap.put("dataType", statType + "");
                    // 设定超时
                    hashOperations.expire(1, TimeUnit.HOURS);
                }


                // 统计用户数: 未存在用户+1
                String userKey = TaskMonitorPerMinKeyHelper.keyOfUsersOnSaasIntervalStat(redisKeyTime, statType);
                BoundSetOperations<String, String> userSetOperations = redisOperations.boundSetOps(userKey);
                if (!Boolean.TRUE.equals(redisOperations.hasKey(userKey))) {
                    userSetOperations.expire(1, TimeUnit.HOURS);
                }
                if (!userSetOperations.isMember(message.getUniqueId())) {
                    Long userCount = hashOperations.increment("userCount", 1);
                    statMap.put("userCount", userCount + "");
                }
                updateMerchantStatRedisData(hashOperations, message, statMap, redisKeyTime);
                return null;
            }
        });
        logger.info("任务监控,消息处理,message={},statMap={},key={},value={}",
                JSON.toJSONString(message), JSON.toJSONString(statMap), key, JSON.toJSONString(statMap));
    }

    public void updateSaasDayAccess(Date redisKeyTime, TaskMonitorMessage message, EStatType statType) {
        Map<String, String> statMap = Maps.newHashMap();
        String key = TaskMonitorPerMinKeyHelper.keyOfSaasDayStat(redisKeyTime, statType);
        redisDao.getRedisTemplate().execute(new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {

                // 判断是否有key
                BoundHashOperations<String, String, String> hashOperations = redisOperations.boundHashOps(key);
                if (!Boolean.TRUE.equals(redisOperations.hasKey(key))) {
                    hashOperations.put("dataTime", MonitorDateUtils.getDayStartTimeStr(redisKeyTime));
                    hashOperations.put("dataType", statType.getType().toString());
                    statMap.put("dataTime", MonitorDateUtils.getDayStartTimeStr(redisKeyTime));
                    statMap.put("dataType", statType + "");
                    // 设定超时时间默认为2天
                    hashOperations.expire(2, TimeUnit.DAYS);
                }


                // 统计用户数: 未存在用户+1
                String userKey = TaskMonitorPerMinKeyHelper.keyOfUsersOnSaasDayStat(redisKeyTime, statType);
                BoundSetOperations<String, String> userSetOperations = redisOperations.boundSetOps(userKey);
                if (!Boolean.TRUE.equals(redisOperations.hasKey(userKey))) {
                    userSetOperations.expire(2, TimeUnit.DAYS);
                }
                if (!userSetOperations.isMember(message.getUniqueId())) {
                    Long userCount = hashOperations.increment("userCount", 1);
                    statMap.put("userCount", userCount + "");
                }
                updateMerchantStatRedisData(hashOperations, message, statMap, redisKeyTime);
                return null;
            }
        });
        logger.info("任务监控,消息处理,message={},statMap={},key={},value={}",
                JSON.toJSONString(message), JSON.toJSONString(statMap), key, JSON.toJSONString(statMap));
    }

    public void updateMerchantAccessWithType(Date redisKeyTime, TaskMonitorMessage message, EStatType statType, String account) {
        Map<String, String> statMap = Maps.newHashMap();
        String key = TaskMonitorPerMinKeyHelper.keyOfMerchantWithTypeIntervalStat(redisKeyTime, message.getAppId(), statType, account);
        redisDao.getRedisTemplate().execute(new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {

                // 需统计的当日特定时间列表
                String dayKey = TaskMonitorPerMinKeyHelper.keyOfDayOnMerchantWithTypeIntervalStat(redisKeyTime, statType);
                BoundSetOperations<String, String> setOperations = redisOperations.boundSetOps(dayKey);
                if (!Boolean.TRUE.equals(redisOperations.hasKey(dayKey))) {
                    setOperations.expire(2, TimeUnit.DAYS);
                }
                setOperations.add(MonitorDateUtils.format(redisKeyTime));

                //需统计的商户
                if (StringUtils.isNotBlank(message.getAppId())) {
                    String merchantsKey = TaskMonitorPerMinKeyHelper.keyOfAppIds();
                    redisOperations.opsForSet().add(merchantsKey, message.getAppId());
                }

                // 判断是否有key
                BoundHashOperations<String, String, String> hashOperations = redisOperations.boundHashOps(key);
                if (!Boolean.TRUE.equals(redisOperations.hasKey(key))) {
                    hashOperations.put("dataTime", MonitorDateUtils.format(redisKeyTime));
                    hashOperations.put("dataType", statType.getType().toString());
                    hashOperations.put("appId", message.getAppId());
                    statMap.put("dataTime", MonitorDateUtils.format(redisKeyTime));
                    statMap.put("dataType", statType + "");
                    statMap.put("appId", message.getAppId());
                    // 设定超时时间默认为2天
                    hashOperations.expire(1, TimeUnit.HOURS);
                }


                // 统计用户数: 未存在用户+1
                String userKey = TaskMonitorPerMinKeyHelper.keyOfUsersOnMerchantWithTypeIntervalStat(redisKeyTime, message.getAppId(), statType, account);
                BoundSetOperations<String, String> userSetOperations = redisOperations.boundSetOps(userKey);
                if (!Boolean.TRUE.equals(redisOperations.hasKey(userKey))) {
                    userSetOperations.expire(1, TimeUnit.HOURS);
                }
                if (!userSetOperations.isMember(message.getUniqueId())) {
                    Long userCount = hashOperations.increment("userCount", 1);
                    statMap.put("userCount", userCount + "");
                }
                updateMerchantStatRedisData(hashOperations, message, statMap, redisKeyTime);
                return null;
            }
        });
        logger.info("任务监控,消息处理,message={},statMap={},key={},value={}",
                JSON.toJSONString(message), JSON.toJSONString(statMap), key, JSON.toJSONString(statMap));


    }

    /**
     * 更新redis中的几个数量
     *
     * @param hashOperations
     * @param message
     * @param statMap
     * @param redisKeyTime
     */
    private void updateMerchantStatRedisData(BoundHashOperations<String, String, String> hashOperations,
                                             TaskMonitorMessage message,
                                             Map<String, String> statMap,
                                             Date redisKeyTime) {
        ETaskStatus status = ETaskStatus.getTaskStatusByStatus(message.getStatus());
        if (status == null) {
            return;
        }
        // 统计总数
        Long totalCount = hashOperations.increment("totalCount", 1);
        statMap.put("totalCount", totalCount + "");
        switch (status) {
            case FAIL:
                Long failCount = hashOperations.increment("failCount", 1);
                statMap.put("failCount", failCount + "");
                break;
            case SUCCESS:
                Long successCount = hashOperations.increment("successCount", 1);
                statMap.put("successCount", successCount + "");
                break;
            case CANCEL:
                Long cancelCount = hashOperations.increment("cancelCount", 1);
                statMap.put("cancelCount", cancelCount + "");
                break;
            default:
                logger.error("任务监控,消息处理,刷新redis时,统计数据类型有误,redisKeyTime={},message={},statMap={}",
                        MonitorDateUtils.format(redisKeyTime), JSON.toJSONString(message), JSON.toJSONString(statMap));
                break;
        }
    }


    public void updateSaasErrorStepDay(Date redisKeyTime, TaskMonitorMessage message, EStatType statType) {
        Map<String, String> statMap = Maps.newHashMap();
        String key = TaskMonitorPerMinKeyHelper.keyOfSaasErrorStepDay(redisKeyTime, statType, message.getStepCode());
        redisDao.getRedisTemplate().execute(new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {

                // 判断是否有key
                BoundHashOperations<String, String, String> hashOperations = redisOperations.boundHashOps(key);
                if (!Boolean.TRUE.equals(redisOperations.hasKey(key))) {
                    hashOperations.put("dataTime", MonitorDateUtils.getDayStartTimeStr(redisKeyTime));
                    hashOperations.put("dataType", statType.getType().toString());
                    hashOperations.put("errorStepCode", message.getStepCode());
                    statMap.put("dataTime", MonitorDateUtils.getDayStartTimeStr(redisKeyTime));
                    statMap.put("dataType", statType + "");
                    statMap.put("errorStepCode", message.getStepCode());
                    // 设定超时时间默认为2天
                    hashOperations.expire(2, TimeUnit.DAYS);
                }
                // 统计失败数
                if (ETaskStatus.FAIL.getStatus().equals(message.getStatus())) {
                    Long failCount = hashOperations.increment("failCount", 1);
                    statMap.put("failCount", failCount + "");
                }
                return null;
            }
        });
        logger.info("任务监控,消息处理,message={},statMap={},key={},value={}",
                JSON.toJSONString(message), JSON.toJSONString(statMap), key, JSON.toJSONString(statMap));

    }
}
