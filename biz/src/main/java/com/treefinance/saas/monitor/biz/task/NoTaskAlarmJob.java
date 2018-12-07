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
import com.treefinance.saas.monitor.share.cache.RedisDao;
import com.treefinance.saas.monitor.common.domain.dto.TaskExistAlarmNoSuccessTaskConfigDTO;
import com.treefinance.saas.monitor.common.domain.dto.TaskExistAlarmNoTaskConfigDTO;
import com.treefinance.saas.monitor.util.SystemUtils;
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
    @Autowired
    private DiamondConfig diamondConfig;


    @Override
    public void execute(ShardingContext shardingContext) {
        if (!diamondConfig.isOldAlarmAllSwitchOn()) {
            return;
        }
        if (SystemUtils.isPreProductContext()) {
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
        //无成功任务
        noSuccessTaskAlarmCheck(intervalMinutes, redisKeyTime, hour);
    }

    /**
     * 无成功任务校验,区分任务业务类型
     *
     * @param intervalMinutes
     * @param redisKeyTime
     * @param hour
     */
    private void noSuccessTaskAlarmCheck(int intervalMinutes, Date redisKeyTime, int hour) {
        long start = System.currentTimeMillis();
        try {
            String config = diamondConfig.getTaskExistAlarmNoSuccessTaskConfig();
            List<TaskExistAlarmNoSuccessTaskConfigDTO> configList = JSONObject.parseArray(config, TaskExistAlarmNoSuccessTaskConfigDTO.class);
            redisDao.getRedisTemplate().execute(new SessionCallback<Object>() {
                @Override
                public Object execute(RedisOperations redisOperations) throws DataAccessException {
                    for (TaskExistAlarmNoSuccessTaskConfigDTO config : configList) {
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

                        List<Boolean> noTaskList = Lists.newArrayList();
                        for (int i = 1; i <= noSuccessTaskCount; i++) {
                            Date keyDate = DateUtils.addMinutes(redisKeyTime, -intervalMinutes * i);
                            String dataKey = RedisKeyHelper.keyOfTaskExistWithTypeAndEnv(keyDate, config.getBizType() + "", config.getSaasEnv() + "");
                            Map<String, String> data = redisOperations.opsForHash().entries(dataKey);
                            logger.info("任务预警,定时任务执行,无任务校验(区分业务类型),dataKey={},data={}", dataKey, JSON.toJSONString(data));
                            if (data != null && data.get("successCount") != null && Integer.valueOf(data.get("successCount")) > 0) {
                                continue;
                            }
                            noTaskList.add(true);
                        }
                        if (noTaskList.size() == noSuccessTaskCount) {
                            taskExistMonitorAlarmService.alarmNoSuccessTaskWithConfig(startTime, redisKeyTime, config);
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
            String config = diamondConfig.getTaskExistAlarmNoTaskConfig();
            List<TaskExistAlarmNoTaskConfigDTO> configList = JSONObject.parseArray(config, TaskExistAlarmNoTaskConfigDTO.class);
            redisDao.getRedisTemplate().execute(new SessionCallback<Object>() {
                @Override
                public Object execute(RedisOperations redisOperations) throws DataAccessException {
                    for (TaskExistAlarmNoTaskConfigDTO config : configList) {
                        Integer dayMins = config.getDayMins();
                        Integer nightMins = config.getNightMins();

                        int noTaskCount;
                        // 0-7点
                        if (hour >= 0 && hour < 7) {
                            noTaskCount = nightMins / intervalMinutes;
                            ;
                        } else {
                            noTaskCount = dayMins / intervalMinutes;
                        }
                        Date startTime = DateUtils.addMinutes(redisKeyTime, -intervalMinutes * noTaskCount);
                        List<Boolean> noTaskList = Lists.newArrayList();
                        for (int i = 1; i <= noTaskCount; i++) {
                            Date keyDate = DateUtils.addMinutes(redisKeyTime, -intervalMinutes * i);
                            String dataKey = RedisKeyHelper.keyOfTaskExistWithTypeAndEnv(keyDate, config.getBizType() + "", config.getSaasEnv() + "");
                            Map<String, String> data = redisOperations.opsForHash().entries(dataKey);
                            logger.info("任务预警,定时任务执行,无任务校验,dataKey={},data={}", dataKey, JSON.toJSONString(data));
                            if (data != null && data.get("totalCount") != null && Integer.valueOf(data.get("totalCount")) > 0) {
                                continue;
                            }
                            noTaskList.add(true);
                        }
                        if (noTaskList.size() == noTaskCount) {
                            taskExistMonitorAlarmService.alarmNoTaskWithConfig(startTime, redisKeyTime, config);
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

}
