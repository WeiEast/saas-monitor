package com.treefinance.saas.monitor.biz.service.newmonitor.operator;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.treefinance.saas.assistant.model.TaskOperatorMonitorMessage;
import com.treefinance.saas.grapserver.facade.model.enums.ETaskOperatorMonitorStatus;
import com.treefinance.saas.monitor.biz.helper.TaskOperatorMonitorKeyHelper;
import com.treefinance.saas.monitor.common.cache.RedisDao;
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
    public void updateIntervalData(Date intervalTime, TaskOperatorMonitorMessage message, ETaskOperatorMonitorStatus status) {
        Map<String, String> statMap = Maps.newHashMap();
        statMap.put("dataType", status + "");
        String key = TaskOperatorMonitorKeyHelper.keyOfGroupCodeIntervalStat(intervalTime, message.getGroupCode());
        redisDao.getRedisTemplate().execute(new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {

                // 需统计的当日特定时间列表
                String dayKey = TaskOperatorMonitorKeyHelper.keyOfDay(intervalTime);
                BoundSetOperations<String, String> setOperations = redisOperations.boundSetOps(dayKey);
                if (!Boolean.TRUE.equals(redisOperations.hasKey(dayKey))) {
                    setOperations.expire(2, TimeUnit.DAYS);
                }
                setOperations.add(MonitorDateUtils.format(intervalTime));

                //需统计的运营商GroupCode
                if (StringUtils.isNotBlank(message.getGroupCode())) {
                    String groupCodesKey = TaskOperatorMonitorKeyHelper.keyOfGroupCodes();
                    redisOperations.opsForSet().add(groupCodesKey, message.getGroupCode());
                }

                // 判断是否有key
                BoundHashOperations<String, String, String> hashOperations = redisOperations.boundHashOps(key);
                if (!Boolean.TRUE.equals(redisOperations.hasKey(key))) {
                    hashOperations.put("dataTime", MonitorDateUtils.format(intervalTime));
                    hashOperations.put("groupCode", message.getGroupCode());
                    hashOperations.put("groupName", message.getGroupName());
                    statMap.put("dataTime", MonitorDateUtils.format(intervalTime));
                    statMap.put("groupCode", message.getGroupCode());
                    statMap.put("groupName", message.getGroupName());
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
    public void updateDayData(Date intervalTime, TaskOperatorMonitorMessage message, ETaskOperatorMonitorStatus status) {
        Map<String, String> statMap = Maps.newHashMap();
        statMap.put("dataType", status + "");
        String key = TaskOperatorMonitorKeyHelper.keyOfGroupCodeDayStat(intervalTime, message.getGroupCode());

        redisDao.getRedisTemplate().execute(new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {

                //需统计的运营商GroupCode
                if (StringUtils.isNotBlank(message.getGroupCode())) {
                    String groupCodesKey = TaskOperatorMonitorKeyHelper.keyOfGroupCodes();
                    redisOperations.opsForSet().add(groupCodesKey, message.getGroupCode());
                }

                // 判断是否有key
                BoundHashOperations<String, String, String> hashOperations = redisOperations.boundHashOps(key);
                if (!Boolean.TRUE.equals(redisOperations.hasKey(key))) {
                    hashOperations.put("dataTime", MonitorDateUtils.getDayStartTimeStr(intervalTime));
                    hashOperations.put("groupCode", message.getGroupCode());
                    hashOperations.put("groupName", message.getGroupName());
                    statMap.put("dataTime", MonitorDateUtils.getDayStartTimeStr(intervalTime));
                    statMap.put("groupCode", message.getGroupCode());
                    statMap.put("groupName", message.getGroupName());
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
    public void updateAllDayData(Date intervalTime, TaskOperatorMonitorMessage message, ETaskOperatorMonitorStatus status) {
        Map<String, String> statMap = Maps.newHashMap();
        statMap.put("dataType", status + "");
        String key = TaskOperatorMonitorKeyHelper.keyOfAllDayStat(intervalTime);

        redisDao.getRedisTemplate().execute(new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {

                // 判断是否有key
                BoundHashOperations<String, String, String> hashOperations = redisOperations.boundHashOps(key);
                if (!Boolean.TRUE.equals(redisOperations.hasKey(key))) {
                    hashOperations.put("dataTime", MonitorDateUtils.getDayStartTimeStr(intervalTime));
                    statMap.put("dataTime", MonitorDateUtils.getDayStartTimeStr(intervalTime));
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

}
