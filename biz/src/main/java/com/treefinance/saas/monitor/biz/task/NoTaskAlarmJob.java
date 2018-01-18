package com.treefinance.saas.monitor.biz.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.google.common.collect.Lists;
import com.treefinance.saas.monitor.biz.config.DiamondConfig;
import com.treefinance.saas.monitor.biz.helper.RedisKeyHelper;
import com.treefinance.saas.monitor.biz.helper.StatHelper;
import com.treefinance.saas.monitor.biz.service.TaskExistMonitorAlarmService;
import com.treefinance.saas.monitor.common.cache.RedisDao;
import com.treefinance.saas.monitor.common.domain.dto.TaskExistAlarmNoSuccessMinsConfigDTO;
import com.treefinance.saas.monitor.common.enumeration.EBizType;
import com.treefinance.saas.monitor.common.utils.MonitorUtils;
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
import java.util.stream.Collectors;

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
    @Autowired
    private DiamondConfig diamondConfig;


    @Override
    public void execute(ShardingContext shardingContext) {
        if (MonitorUtils.isPreProductContext()) {
            logger.info("定时任务,预发布环境暂不执行");
            return;
        }
        int intervalMinutes = 5;
        final Date now = new Date();
        final Date redisKeyTime = StatHelper.getRedisStatDateTime(now, intervalMinutes);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        //无任务校验
        noTaskAlarmCheck(intervalMinutes, redisKeyTime, hour);
        //无成功任务校验
        noSuccessTaskAlarmCheck(intervalMinutes, redisKeyTime, hour);
        //无成功任务校验,区分任务业务类型
        noSuccessTaskAlarmCheckWithType(intervalMinutes, redisKeyTime, hour);
    }

    /**
     * 无成功任务校验,区分任务业务类型
     *
     * @param intervalMinutes
     * @param redisKeyTime
     * @param hour
     */
    private void noSuccessTaskAlarmCheckWithType(int intervalMinutes, Date redisKeyTime, int hour) {
        long start = System.currentTimeMillis();
        try {
            String config = diamondConfig.getTaskExistAlarmNoSuccessMinsConfig();
            List<TaskExistAlarmNoSuccessMinsConfigDTO> configList = JSONObject.parseArray(config, TaskExistAlarmNoSuccessMinsConfigDTO.class);
            Map<String, TaskExistAlarmNoSuccessMinsConfigDTO> configMap = configList.stream()
                    .collect(Collectors.toMap(TaskExistAlarmNoSuccessMinsConfigDTO::getType, configDTO -> configDTO));

            redisDao.getRedisTemplate().execute(new SessionCallback<Object>() {
                @Override
                public Object execute(RedisOperations redisOperations) throws DataAccessException {
                    for (EBizType bizType : EBizType.values()) {
                        TaskExistAlarmNoSuccessMinsConfigDTO config = configMap.get(bizType.getText());
                        if (config == null) {
                            continue;
                        }
                        Integer dayMins = config.getDayMins();
                        Integer nightMins = config.getNightMins();
                        int noSuccessTaskCount;
                        // 0-7点
                        if (hour >= 0 && hour < 7) {
                            noSuccessTaskCount = nightMins / intervalMinutes;
                        } else {
                            noSuccessTaskCount = dayMins / intervalMinutes;
                        }
                        Date startTime = DateUtils.addMinutes(redisKeyTime, -intervalMinutes * noSuccessTaskCount);
                        Date endTime = redisKeyTime;

                        List<Boolean> noTaskList = Lists.newArrayList();
                        for (int i = 1; i <= noSuccessTaskCount; i++) {
                            Date keyDate = DateUtils.addMinutes(redisKeyTime, -intervalMinutes * i);
                            String dataKey = RedisKeyHelper.keyOfTaskExistWithType(keyDate, bizType);
                            Map<String, String> data = redisOperations.opsForHash().entries(dataKey);
                            logger.info("任务预警,定时任务执行,无任务校验(区分业务类型),dataKey={},data={}", dataKey, JSON.toJSONString(data));
                            if (data != null && data.get("successCount") != null && Integer.valueOf(data.get("successCount").toString()) > 0) {
                                continue;
                            }
                            noTaskList.add(true);
                        }
                        if (noTaskList.size() == noSuccessTaskCount) {
                            taskExistMonitorAlarmService.alarmNoSuccessTaskWithType(startTime, endTime, bizType);
                        }
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
     * 无任务校验
     *
     * @param intervalMinutes
     * @param redisKeyTime
     * @param hour
     */
    private void noTaskAlarmCheck(int intervalMinutes, Date redisKeyTime, int hour) {
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
                    Date startTime = DateUtils.addMinutes(redisKeyTime, -intervalMinutes * noTaskCount);
                    Date endTime = redisKeyTime;

                    List<Boolean> noTaskList = Lists.newArrayList();
                    for (int i = 1; i <= noTaskCount; i++) {
                        Date keyDate = DateUtils.addMinutes(redisKeyTime, -intervalMinutes * i);
                        String dataKey = RedisKeyHelper.keyOfTaskExist(keyDate);
                        Map<String, String> data = redisOperations.opsForHash().entries(dataKey);
                        logger.info("任务预警,定时任务执行,无任务校验,dataKey={},data={}", dataKey, JSON.toJSONString(data));
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
     * @param redisKeyTime
     * @param hour
     */
    private void noSuccessTaskAlarmCheck(int intervalMinutes, Date redisKeyTime, int hour) {
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
                    Date startTime = DateUtils.addMinutes(redisKeyTime, -intervalMinutes * noSuccessTaskCount);
                    Date endTime = redisKeyTime;

                    List<Boolean> noTaskList = Lists.newArrayList();
                    for (int i = 1; i <= noSuccessTaskCount; i++) {
                        Date keyDate = DateUtils.addMinutes(redisKeyTime, -intervalMinutes * i);
                        String dataKey = RedisKeyHelper.keyOfTaskExist(keyDate);
                        Map<String, String> data = redisOperations.opsForHash().entries(dataKey);
                        logger.info("任务预警,定时任务执行,无成功任务校验,dataKey={},data={}", dataKey, JSON.toJSONString(data));
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
