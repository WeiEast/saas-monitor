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
 * Created by haojiahong on 2017/11/17.
 */
@Service
public class TaskExistMonitorServiceImpl implements TaskExistMonitorService {
    private static final Logger logger = LoggerFactory.getLogger(TaskExistMonitorService.class);


    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    @Override
    public void doService(TaskMonitorMessage message) {
        try {
            Map<String, String> map = Maps.newHashMap();
            Date taskCreateTime = message.getCompleteTime();
            Date redisKeyTime = StatHelper.getRedisStatDateTime(taskCreateTime, 5);
            String redisKey = RedisKeyHelper.keyOfTaskExist(redisKeyTime);
            map.put("key", redisKey);
            BoundHashOperations<String, String, String> hashOperations = redisTemplate.boundHashOps(redisKey);
            if (!Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
                hashOperations.expire(1, TimeUnit.HOURS);
            }
            Long totalCount = hashOperations.increment("totalCount", 1);
            map.put("totalCount", totalCount + "");
            if (message.getStatus() != null && message.getStatus() == 2) {//成功的任务
                Long successCount = hashOperations.increment("successCount", 1);
                map.put("successCount", successCount + "");
            }
            logger.info("任务预警,任务预警消息处理完成,预警处理的任务消息message={},map={}", JSON.toJSONString(message), JSON.toJSONString(map));
        } catch (Exception e) {
            logger.error("任务预警,异常", e);
        }
    }
}
