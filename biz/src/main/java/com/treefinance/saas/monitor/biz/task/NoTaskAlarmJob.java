package com.treefinance.saas.monitor.biz.task;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.google.common.collect.Lists;
import com.treefinance.saas.monitor.biz.helper.RedisKeyHelper;
import com.treefinance.saas.monitor.biz.helper.StatHelper;
import com.treefinance.saas.monitor.biz.service.TaskExistMonitorAlarmService;
import com.treefinance.saas.monitor.common.cache.RedisDao;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 数据统计Job
 * Created by yh-treefinance on 2017/5/25.
 */
public class NoTaskAlarmJob implements SimpleJob {
    private static final Logger logger = LoggerFactory.getLogger(NoTaskAlarmJob.class);
    @Autowired
    private RedisDao redisDao;

    @Autowired
    private TaskExistMonitorAlarmService taskExistMonitorAlarmService;

    @Override
    public void execute(ShardingContext shardingContext) {

        int intervalMinutes = 5;
        final Date now = new Date();
        final Date intervalTime = StatHelper.getRedisStatDateTime(now, intervalMinutes);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        // 1.无任务校验
        noTaskAlarmCheck(intervalMinutes, intervalTime, hour);
        // 2.无成功任务校验
        noSuccessTaskAlarmCheck(intervalMinutes, intervalTime, hour);
    }

    /**
     * 无任务校验
     *
     * @param intervalMinutes
     * @param intervalTime
     * @param hour
     */
    private void noTaskAlarmCheck(int intervalMinutes, Date intervalTime, int hour) {
        long start = System.currentTimeMillis();
        try {
            logger.info("NoTaskAlarmJob:noTaskAlarmCheck - intervalMinutes={}", intervalMinutes);
            redisDao.getRedisTemplate().execute(new SessionCallback<Object>() {
                @Override
                public Object execute(RedisOperations redisOperations) throws DataAccessException {
                    int noTaskCount = 1;
                    // 0-7点
                    if (hour >= 0 && hour < 7) {
                        noTaskCount = 3;
                    } else {
                        noTaskCount = 1;
                    }
                    Date startTime = DateUtils.addMinutes(intervalTime, -intervalMinutes * noTaskCount);
                    Date endTime = intervalTime;

                    List<Boolean> noTaskList = Lists.newArrayList();
                    for (int i = 0; i < noTaskCount; i++) {
                        Date keyDate = DateUtils.addMinutes(intervalTime, -intervalMinutes * i);
                        String dataKey = RedisKeyHelper.keyOfTaskExist(keyDate);
                        Map<String, String> data = redisOperations.opsForHash().entries(dataKey);
                        if (data != null && data.get("totalCount") != null && Integer.valueOf(data.get("totalCount").toString()) > 0) {
                            continue;
                        }
                        noTaskList.add(true);
                    }
                    if (noTaskList.size() == noTaskCount) {
                        taskExistMonitorAlarmService.alarmNoTask(startTime, endTime);
                    }
                    return null;
                }
            });
        } catch (Exception e) {
            logger.error("NoTaskAlarmJob:noTaskAlarmCheck - exception : ", e);
        } finally {
            logger.info("NoTaskAlarmJob:noTaskAlarmCheck - 耗时time={}ms", System.currentTimeMillis() - start);
        }
    }

    /**
     * 无成功任务校验
     *
     * @param intervalMinutes
     * @param intervalTime
     * @param hour
     */
    private void noSuccessTaskAlarmCheck(int intervalMinutes, Date intervalTime, int hour) {
        long start = System.currentTimeMillis();
        try {
            logger.info("NoTaskAlarmJob:noSuccessTaskAlarmCheck - intervalMinutes={}", intervalMinutes);
            redisDao.getRedisTemplate().execute(new SessionCallback<Object>() {
                @Override
                public Object execute(RedisOperations redisOperations) throws DataAccessException {
                    // #TODO
                    int noSuccessTaskCount = 1;
                    // 0-7点
                    if (hour >= 0 && hour < 7) {
                        noSuccessTaskCount = 6;
                    } else {
                        noSuccessTaskCount = 2;
                    }
                    Date startTime = DateUtils.addMinutes(intervalTime, -intervalMinutes * noSuccessTaskCount);
                    Date endTime = intervalTime;

                    List<Boolean> noTaskList = Lists.newArrayList();
                    for (int i = 0; i < noSuccessTaskCount; i++) {
                        Date keyDate = DateUtils.addMinutes(intervalTime, -intervalMinutes * i);
                        String dataKey = RedisKeyHelper.keyOfTaskExist(keyDate);
                        Map<String, String> data = redisOperations.opsForHash().entries(dataKey);
                        if (data != null && data.get("successCount") != null && Integer.valueOf(data.get("successCount").toString()) > 0) {
                            continue;
                        }
                        noTaskList.add(true);
                    }
                    if (noTaskList.size() == noSuccessTaskCount) {
                        taskExistMonitorAlarmService.alarmNoSuccessTask(startTime, endTime);
                    }
                    return null;
                }
            });
        } catch (Exception e) {
            logger.error("NoTaskAlarmJob:noSuccessTaskAlarmCheck exception : ", e);
        } finally {
            logger.info("NoTaskAlarmJob:noSuccessTaskAlarmCheck 耗时time={}ms", System.currentTimeMillis() - start);
        }
    }
}
