package com.treefinance.saas.monitor.biz.service.newmonitor.operator;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.treefinance.saas.assistant.model.TaskOperatorMonitorMessage;
import com.treefinance.saas.grapserver.facade.model.enums.ETaskOperatorMonitorStatus;
import com.treefinance.saas.monitor.biz.helper.TaskOperatorMonitorKeyHelper;
import com.treefinance.saas.monitor.common.cache.RedisDao;
import com.treefinance.saas.monitor.common.enumeration.ETaskOperatorStatType;
import com.treefinance.saas.monitor.common.utils.MonitorDateUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by haojiahong on 2017/10/30.
 */
@Component
public class TaskOperatorMonitorMessageProcessor {

    private final static Logger logger = LoggerFactory.getLogger(TaskOperatorMonitorMessageProcessor.class);

    @Autowired
    private RedisDao redisDao;


    /**
     * 按配置的时间间隔更新特定运营商数据
     *
     * @param intervalTime
     * @param message
     * @param status
     */
    public void updateIntervalData(Date intervalTime, TaskOperatorMonitorMessage message, ETaskOperatorMonitorStatus status, ETaskOperatorStatType statType) {
        Map<String, String> statMap = Maps.newHashMap();
        statMap.put("statusType", status + "");
        String key = TaskOperatorMonitorKeyHelper.keyOfGroupCodeIntervalStat(intervalTime, message.getGroupCode(), message.getAppId(), statType);
        redisDao.getRedisTemplate().execute(new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {

                // 需统计的当日特定时间列表
                String dayKey = TaskOperatorMonitorKeyHelper.keyOfDayOnGroupStat(intervalTime, message.getAppId(), statType);
                BoundSetOperations<String, String> setOperations = redisOperations.boundSetOps(dayKey);
                setOperations.add(Joiner.on(";").join(MonitorDateUtils.format(intervalTime), System.currentTimeMillis()));
                if (setOperations.getExpire() == -1) {
                    setOperations.expire(2, TimeUnit.DAYS);
                }

                //需统计的appIds
                if (StringUtils.isNotBlank(message.getAppId())) {
                    String appIdsKey = TaskOperatorMonitorKeyHelper.keyOfAppIds();
                    redisOperations.opsForSet().add(appIdsKey, message.getAppId());
                }

                //需统计的运营商GroupCode
                if (StringUtils.isNotBlank(message.getGroupCode())) {
                    String groupCodesKey = TaskOperatorMonitorKeyHelper.keyOfGroupCodes();
                    redisOperations.opsForSet().add(groupCodesKey, message.getGroupCode());
                }

                // 判断是否有key
                BoundHashOperations<String, String, String> hashOperations = redisOperations.boundHashOps(key);
                if (!Boolean.TRUE.equals(redisOperations.hasKey(key))) {
                    hashOperations.put("dataTime", MonitorDateUtils.format(intervalTime));
                    hashOperations.put("dataType", statType.getCode().toString());
                    hashOperations.put("groupCode", message.getGroupCode());
                    hashOperations.put("groupName", message.getGroupName());
                    hashOperations.put("appId", message.getAppId());
                    statMap.put("dataTime", MonitorDateUtils.format(intervalTime));
                    statMap.put("dataType", statType + "");
                    statMap.put("groupCode", message.getGroupCode());
                    statMap.put("groupName", message.getGroupName());
                    statMap.put("appId", message.getAppId());
                    // 设定超时时间默认为2天
                    hashOperations.expire(2, TimeUnit.DAYS);
                }
                updateRedisData(hashOperations, message, statMap, intervalTime);
                return null;
            }
        });
        logger.info("运营商监控,消息处理,key={},value={}", key, JSON.toJSONString(statMap));
    }

    /**
     * 按日统计特定运营商数据
     *
     * @param intervalTime
     * @param message
     * @param status
     */
    public void updateDayData(Date intervalTime, TaskOperatorMonitorMessage message, ETaskOperatorMonitorStatus status, ETaskOperatorStatType statType) {
        Map<String, String> statMap = Maps.newHashMap();
        statMap.put("statusType", status + "");
        String key = TaskOperatorMonitorKeyHelper.keyOfGroupCodeDayStat(intervalTime, message.getGroupCode(), message.getAppId(), statType);

        redisDao.getRedisTemplate().execute(new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {

                //需统计的appIds
                if (StringUtils.isNotBlank(message.getAppId())) {
                    String appIdsKey = TaskOperatorMonitorKeyHelper.keyOfAppIds();
                    redisOperations.opsForSet().add(appIdsKey, message.getAppId());
                }

                //需统计的运营商GroupCode
                if (StringUtils.isNotBlank(message.getGroupCode())) {
                    String groupCodesKey = TaskOperatorMonitorKeyHelper.keyOfGroupCodes();
                    redisOperations.opsForSet().add(groupCodesKey, message.getGroupCode());
                }

                // 判断是否有key
                BoundHashOperations<String, String, String> hashOperations = redisOperations.boundHashOps(key);
                if (!Boolean.TRUE.equals(redisOperations.hasKey(key))) {
                    hashOperations.put("dataTime", MonitorDateUtils.getDayStartTimeStr(intervalTime));
                    hashOperations.put("dataType", statType.getCode().toString());
                    hashOperations.put("groupCode", message.getGroupCode());
                    hashOperations.put("groupName", message.getGroupName());
                    hashOperations.put("appId", message.getAppId());
                    statMap.put("dataTime", MonitorDateUtils.getDayStartTimeStr(intervalTime));
                    statMap.put("dataType", statType + "");
                    statMap.put("groupCode", message.getGroupCode());
                    statMap.put("groupName", message.getGroupName());
                    statMap.put("appId", message.getAppId());
                    // 设定超时时间默认为2天
                    hashOperations.expire(2, TimeUnit.DAYS);
                }
                updateRedisData(hashOperations, message, statMap, intervalTime);
                return null;
            }
        });
        logger.info("运营商监控,消息处理,key={},value={}", key, JSON.toJSONString(statMap));

    }

    /**
     * 按配置的时间间隔更新所有运营商数据
     *
     * @param intervalTime
     * @param message
     * @param status
     */
    public void updateAllIntervalData(Date intervalTime, TaskOperatorMonitorMessage message, ETaskOperatorMonitorStatus status, ETaskOperatorStatType statType) {
        Map<String, String> statMap = Maps.newHashMap();
        statMap.put("statusType", status + "");
        String key = TaskOperatorMonitorKeyHelper.keyOfAllIntervalStat(intervalTime, message.getAppId(), statType);
        redisDao.getRedisTemplate().execute(new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {

                // 需统计的当日特定时间列表
                String dayKey = TaskOperatorMonitorKeyHelper.keyOfDayOnAllStat(intervalTime, message.getAppId(), statType);
                BoundSetOperations<String, String> setOperations = redisOperations.boundSetOps(dayKey);
                setOperations.add(Joiner.on(";").join(MonitorDateUtils.format(intervalTime), System.currentTimeMillis()));
                if (setOperations.getExpire() == -1) {
                    setOperations.expire(2, TimeUnit.DAYS);
                }
                //需统计的appIds
                if (StringUtils.isNotBlank(message.getAppId())) {
                    String appIdsKey = TaskOperatorMonitorKeyHelper.keyOfAppIds();
                    redisOperations.opsForSet().add(appIdsKey, message.getAppId());
                }

                // 判断是否有key
                BoundHashOperations<String, String, String> hashOperations = redisOperations.boundHashOps(key);
                if (!Boolean.TRUE.equals(redisOperations.hasKey(key))) {
                    hashOperations.put("dataTime", MonitorDateUtils.format(intervalTime));
                    hashOperations.put("dataType", statType.getCode().toString());
                    hashOperations.put("appId", message.getAppId());
                    statMap.put("dataType", statType + "");
                    statMap.put("dataTime", MonitorDateUtils.format(intervalTime));
                    statMap.put("appId", message.getAppId());
                    // 设定超时时间默认为2天
                    hashOperations.expire(2, TimeUnit.DAYS);
                }
                updateRedisData(hashOperations, message, statMap, intervalTime);
                return null;
            }
        });
        logger.info("运营商监控,消息处理,key={},value={}", key, JSON.toJSONString(statMap));
    }

    /**
     * 按日统计所有运营商数据
     *
     * @param intervalTime
     * @param message
     * @param status
     */
    public void updateAllDayData(Date intervalTime, TaskOperatorMonitorMessage message, ETaskOperatorMonitorStatus status, ETaskOperatorStatType statType) {
        Map<String, String> statMap = Maps.newHashMap();
        statMap.put("statusType", status + "");
        String key = TaskOperatorMonitorKeyHelper.keyOfAllDayStat(intervalTime, message.getAppId(), statType);

        redisDao.getRedisTemplate().execute(new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {

                //需统计的appIds
                if (StringUtils.isNotBlank(message.getAppId())) {
                    String appIdsKey = TaskOperatorMonitorKeyHelper.keyOfAppIds();
                    redisOperations.opsForSet().add(appIdsKey, message.getAppId());
                }

                // 判断是否有key
                BoundHashOperations<String, String, String> hashOperations = redisOperations.boundHashOps(key);
                if (!Boolean.TRUE.equals(redisOperations.hasKey(key))) {
                    hashOperations.put("dataTime", MonitorDateUtils.getDayStartTimeStr(intervalTime));
                    hashOperations.put("dataType", statType.getCode().toString());
                    hashOperations.put("appId", message.getAppId());
                    statMap.put("dataType", statType + "");
                    statMap.put("dataTime", MonitorDateUtils.getDayStartTimeStr(intervalTime));
                    statMap.put("appId", message.getAppId());
                    // 设定超时时间默认为2天
                    hashOperations.expire(2, TimeUnit.DAYS);
                }
                updateRedisData(hashOperations, message, statMap, intervalTime);
                return null;
            }
        });
        logger.info("运营商监控,消息处理,key={},value={}", key, JSON.toJSONString(statMap));
    }

    private void updateRedisData(BoundHashOperations<String, String, String> hashOperations, TaskOperatorMonitorMessage message, Map<String, String> statMap, Date intervalTime) {
        ETaskOperatorMonitorStatus status = ETaskOperatorMonitorStatus.getMonitorStats(message.getStatus());
        switch (status) {
            case CREATE_TASK:
                Long createTaskCount = hashOperations.increment("entryCount", 1);
                statMap.put("entryCount", createTaskCount + "");
                break;
            case CONFIRM_MOBILE:
                Long confirmMobileCount = hashOperations.increment("confirmMobileCount", 1);
                statMap.put("confirmMobileCount", confirmMobileCount + "");
                break;
            case START_LOGIN:
                Long startLoginCount = hashOperations.increment("startLoginCount", 1);
                statMap.put("startLoginCount", startLoginCount + "");
                break;
            case LOGIN_SUCCESS:
                Long loginSuccessCount = hashOperations.increment("loginSuccessCount", 1);
                statMap.put("loginSuccessCount", loginSuccessCount + "");
                break;
            case CRAWL_SUCCESS:
                Long crawlSuccessCount = hashOperations.increment("crawlSuccessCount", 1);
                statMap.put("crawlSuccessCount", crawlSuccessCount + "");
                break;
            case PROCESS_SUCCESS:
                Long processSuccessCount = hashOperations.increment("processSuccessCount", 1);
                statMap.put("processSuccessCount", processSuccessCount + "");
                break;
            case CALLBACK_SUCCESS:
                Long callbackSuccessCount = hashOperations.increment("callbackSuccessCount", 1);
                statMap.put("callbackSuccessCount", callbackSuccessCount + "");
                break;
            default:
                logger.error("运营商监控,消息处理,刷新redis时,统计数据类型有误,intervalTime={},message={}",
                        MonitorDateUtils.format(intervalTime), JSON.toJSONString(message));
                break;
        }
    }

    public void updateAllIntervalTaskUserCount(Date intervalTime, TaskOperatorMonitorMessage message, ETaskOperatorStatType statType) {
        String key = TaskOperatorMonitorKeyHelper.keyOfTaskUserCountAllIntervalStat(intervalTime, message.getAppId(), statType);
        redisDao.getRedisTemplate().execute(new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                BoundHashOperations<String, String, String> hashOperations = redisOperations.boundHashOps(key);
                Long taskCount = hashOperations.increment("taskCount", 1);
                if (isStatUserCountAllInterval(intervalTime, message, statType)) {
                    Long userCount = hashOperations.increment("userCount", 1);
                }
                if (hashOperations.getExpire() == -1) {
                    hashOperations.expire(2, TimeUnit.HOURS);
                }
                return null;
            }
        });
    }

    public void updateAllDayTaskUserCount(Date intervalTime, TaskOperatorMonitorMessage message, ETaskOperatorStatType statType) {
        String key = TaskOperatorMonitorKeyHelper.keyOfTaskUserCountAllDayStat(intervalTime, message.getAppId(), statType);
        redisDao.getRedisTemplate().execute(new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                BoundHashOperations<String, String, String> hashOperations = redisOperations.boundHashOps(key);
                Long taskCount = hashOperations.increment("taskCount", 1);
                if (isStatUserCountAllDay(intervalTime, message, statType)) {
                    Long userCount = hashOperations.increment("userCount", 1);
                }
                if (hashOperations.getExpire() == -1) {
                    hashOperations.expire(2, TimeUnit.DAYS);
                }
                return null;
            }
        });
    }

    /**
     * userCount是否增加
     *
     * @return
     */
    private Boolean isStatUserCountAllInterval(Date intervalTime, TaskOperatorMonitorMessage message, ETaskOperatorStatType statType) {
        RedisTemplate<String, String> redisTemplate = redisDao.getRedisTemplate();
        StringBuilder sb = new StringBuilder();
        String uniqueValue = sb.append(message.getAppId()).append("-").append(message.getUniqueId()).toString();

        //判断此用户此操作是否已经统计过
        String usersKey = TaskOperatorMonitorKeyHelper.keyOfUsersCountAllIntervalStat(intervalTime, message.getAppId(), statType);
        BoundSetOperations<String, String> setOperations = redisTemplate.boundSetOps(usersKey);
        if (setOperations.isMember(uniqueValue)) {
            return false;
        }
        setOperations.add(uniqueValue);
        if (setOperations.getExpire() == -1) {
            setOperations.expire(2, TimeUnit.HOURS);
        }

        //统计时段内只统计用户一个手机号的数据
        String accountNo = message.getAccountNo();
        if (StringUtils.isNotBlank(accountNo)) {
            String userMobileKey = TaskOperatorMonitorKeyHelper.keyOfUserCountAllIntervalUsersMobileLog(intervalTime, message.getAppId(), statType);
            BoundHashOperations<String, String, String> hashOperations = redisTemplate.boundHashOps(userMobileKey);
            String oldAccountNo = hashOperations.get(uniqueValue);
            if (StringUtils.isNotBlank(oldAccountNo)) {
                if (!StringUtils.equals(oldAccountNo, accountNo)) {
                    return false;
                }
            } else {
                hashOperations.put(uniqueValue, accountNo);
            }
            if (hashOperations.getExpire() == -1) {
                hashOperations.expire(2, TimeUnit.HOURS);
            }
        }
        return true;
    }

    private Boolean isStatUserCountAllDay(Date intervalTime, TaskOperatorMonitorMessage message, ETaskOperatorStatType statType) {
        RedisTemplate<String, String> redisTemplate = redisDao.getRedisTemplate();
        StringBuilder sb = new StringBuilder();
        String uniqueValue = sb.append(message.getAppId()).append("-").append(message.getUniqueId()).toString();

        //判断此用户此操作是否已经统计过
        String usersKey = TaskOperatorMonitorKeyHelper.keyOfUsersCountAllDayStat(intervalTime, message.getAppId(), statType);
        BoundSetOperations<String, String> setOperations = redisTemplate.boundSetOps(usersKey);
        if (setOperations.isMember(uniqueValue)) {
            return false;
        }
        setOperations.add(uniqueValue);
        if (setOperations.getExpire() == -1) {
            setOperations.expire(2, TimeUnit.DAYS);
        }

        //统计时段内只统计用户一个手机号的数据
        String accountNo = message.getAccountNo();
        if (StringUtils.isNotBlank(accountNo)) {
            String userMobileKey = TaskOperatorMonitorKeyHelper.keyOfUserCountAllDayUsersMobileLog(intervalTime, message.getAppId(), statType);
            BoundHashOperations<String, String, String> hashOperations = redisTemplate.boundHashOps(userMobileKey);
            String oldAccountNo = hashOperations.get(uniqueValue);
            if (StringUtils.isNotBlank(oldAccountNo)) {
                if (!StringUtils.equals(oldAccountNo, accountNo)) {
                    return false;
                }
            } else {
                hashOperations.put(uniqueValue, accountNo);
            }
            if (hashOperations.getExpire() == -1) {
                hashOperations.expire(2, TimeUnit.DAYS);
            }
        }
        return true;
    }
}
