package com.treefinance.saas.monitor.biz.service.newmonitor.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.treefinance.saas.assistant.model.TaskMonitorMessage;
import com.treefinance.saas.monitor.biz.helper.RedisKeyHelper;
import com.treefinance.saas.monitor.biz.helper.StatHelper;
import com.treefinance.saas.monitor.biz.service.newmonitor.TaskExistMonitorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Good Luck Bro , No Bug !
 * <p>
 * 无任务,无成功任务区分环境统计
 *
 * @author haojiahong
 * @date 2018/5/14
 */
@Service
public class TaskExistEnvMonitorServiceImpl implements TaskExistMonitorService {

    private static final Logger logger = LoggerFactory.getLogger(TaskExistMonitorService.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    @Override
    public void doService(TaskMonitorMessage message) {
        try {
            Date taskCreateTime = message.getCompleteTime();
            Date redisKeyTime = StatHelper.getRedisStatDateTime(taskCreateTime, 5);
            //统计所有任务
            statAllTaskExist(message, redisKeyTime);
            //分别统计不同业务类型的任务
            statTaskExistWithType(message, redisKeyTime);
        } catch (Exception e) {
            logger.error("任务预警,异常", e);
        }
    }

    private void statTaskExistWithType(TaskMonitorMessage message, Date redisKeyTime) {
        Map<String, String> map = Maps.newHashMap();
        String redisKey = RedisKeyHelper.keyOfTaskExistWithTypeAndEnv(redisKeyTime, message.getBizType() + "", message.getSaasEnv());
        map.put("key", redisKey);
        BoundHashOperations<String, String, String> hashOperations = redisTemplate.boundHashOps(redisKey);
        Long totalCount = hashOperations.increment("totalCount", 1);
        map.put("totalCount", totalCount + "");
        if (message.getStatus() != null && message.getStatus() == 2) {//成功的任务
            Long successCount = hashOperations.increment("successCount", 1);
            map.put("successCount", successCount + "");
        }
        if (hashOperations.getExpire() == -1) {
            hashOperations.expire(1, TimeUnit.DAYS);
        }
        logger.info("任务预警,任务预警消息处理完成,预警处理的任务消息message={},map={}", JSON.toJSONString(message), JSON.toJSONString(map));
    }

    private void statAllTaskExist(TaskMonitorMessage message, Date redisKeyTime) {
        Map<String, String> map = Maps.newHashMap();
        String redisKey = RedisKeyHelper.keyOfTaskExistWithTypeAndEnv(redisKeyTime, "0", message.getSaasEnv());
        map.put("key", redisKey);
        BoundHashOperations<String, String, String> hashOperations = redisTemplate.boundHashOps(redisKey);
        Long totalCount = hashOperations.increment("totalCount", 1);
        map.put("totalCount", totalCount + "");
        if (message.getStatus() != null && message.getStatus() == 2) {//成功的任务
            Long successCount = hashOperations.increment("successCount", 1);
            map.put("successCount", successCount + "");
        }
        if (hashOperations.getExpire() == -1) {
            hashOperations.expire(1, TimeUnit.DAYS);
        }
        logger.info("任务预警,任务预警消息处理完成,预警处理的任务消息message={},map={}", JSON.toJSONString(message), JSON.toJSONString(map));
    }
}
