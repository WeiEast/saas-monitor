package com.treefinance.saas.monitor.biz.service.newmonitor.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.treefinance.saas.assistant.model.TaskMonitorMessage;
import com.treefinance.saas.monitor.biz.service.newmonitor.TaskExistMonitorService;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by haojiahong on 2017/11/17.
 */
@Service
public class TaskExistMonitorServiceImpl implements TaskExistMonitorService {
    private static final Logger logger = LoggerFactory.getLogger(TaskExistMonitorService.class);

    private static final String KEY_PREFIX = "saas-monitor-task-exist-monitor";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    @Override
    public void doService(TaskMonitorMessage message) {
        try {
            Map<String, String> map = Maps.newHashMap();
            Date taskCreateTime = message.getCompleteTime();
            Date redisKeyTime = this.getRedisStatDateTime(taskCreateTime, 5);
            String redisKey = this.getRedisKey(redisKeyTime);
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
            logger.info("任务预警,任务预警完成,预警处理的任务消息message={},map={}", JSON.toJSONString(message), JSON.toJSONString(map));
        } catch (Exception e) {
            logger.error("任务预警,异常", e);
        }
    }

    private String getRedisKey(Date time) {
        String timeStr = DateFormatUtils.format(time, "yyyy-MM-dd HH:mm:ss");
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, timeStr);
    }


    /**
     * redis中存入的时间段key,如设置60为:7:00,8:00,9:00
     * 若dataTime=2017-11-06 07:05:55 ,intervalMinutes = 60 则返回 2017-11-06 07:00:00
     *
     * @param dataTime
     * @return
     */
    private Date getRedisStatDateTime(Date dataTime, Integer intervalMinutes) {
        if (intervalMinutes == null) {
            intervalMinutes = 5;
        }
        Date intervalTime = DateUtils.truncate(dataTime, Calendar.MINUTE);
        Long currentMinute = DateUtils.getFragmentInMinutes(intervalTime, Calendar.HOUR_OF_DAY);
        if (currentMinute % intervalMinutes == 0) {
            return intervalTime;
        }
        intervalTime = DateUtils.addMinutes(intervalTime, (-currentMinute.intValue() % intervalMinutes));
        return intervalTime;
    }
}
