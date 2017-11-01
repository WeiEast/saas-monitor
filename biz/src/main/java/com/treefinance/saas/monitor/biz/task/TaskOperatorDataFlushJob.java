package com.treefinance.saas.monitor.biz.task;

import com.alibaba.fastjson.JSON;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.google.common.collect.Sets;
import com.treefinance.saas.monitor.biz.helper.StatHelper;
import com.treefinance.saas.monitor.biz.helper.TaskOperatorMonitorKeyHelper;
import com.treefinance.saas.monitor.common.cache.RedisDao;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;

import java.util.Date;
import java.util.Set;

/**
 * Created by haojiahong on 2017/10/31.
 */
public class TaskOperatorDataFlushJob implements SimpleJob {

    private static final Logger logger = LoggerFactory.getLogger(TaskOperatorDataFlushJob.class);
    @Autowired
    private RedisDao redisDao;


    @Override
    public void execute(ShardingContext shardingContext) {

//        long start = System.currentTimeMillis();
//        try {
//            int intervalMinutes = 0;
//            logger.info("TaskMonitorAlarm:intervalMinutes={}", intervalMinutes);
//            Date now = new Date();
//            Date intervalTime = StatHelper.calculateIntervalTime(now, intervalMinutes);
//            //下一个监控时间 比如当前是9:00则currentInterval=9:10
//            String currentInterval = intervalTime.getTime() + "";
//            //当前监控时间 比如当前是9:00则previousCurrentInterval=9:00
//            String previousCurrentInterval = DateUtils.addMinutes(intervalTime, -intervalMinutes).getTime() + "";
//
//            redisDao.getRedisTemplate().execute(new SessionCallback<Object>() {
//                @Override
//                public Object execute(RedisOperations redisOperations) throws DataAccessException {
//                    // 统计时间
//                    String dayKey = TaskOperatorMonitorKeyHelper.keyOfDay(now);
//                    Set<String> intervalSets = redisOperations.opsForSet().members(dayKey);
//                    if (CollectionUtils.isEmpty(intervalSets)) {
//                        return null;
//                    }
//                    Set<Date> intervalTimeSets = Sets.newHashSet();
//                    intervalSets.forEach(t -> {
//                        Long time = Long.valueOf(t);
//                        intervalTimeSets.add(new Date(time));
//                    });
//                    intervalTimeSets.remove(new Date(Long.valueOf(currentInterval)));
//                    logger.info("TaskMonitorAlarm:TaskDataFlushJob中intervalTimeSets={} currentInterval={}", JSON.toJSONString(intervalTimeSets), currentInterval);
//                    // appId列表
//                    String appIdKey = RedisKeyHelper.keyOfAppIds();
//                    Set<String> appIdSet = redisOperations.opsForSet().members(appIdKey);
//                    if (CollectionUtils.isEmpty(appIdSet)) {
//                        return null;
//                    }
//                    // 1.保存合计数据
//                    saveTotalDayData(intervalTimeSets, appIdSet, redisOperations);
//                    // 2.保存合计数据
//                    saveTotalData(intervalTimeSets, appIdSet, redisOperations);
//                    // 3.保存电商数据
//                    saveEcommerceData(intervalTimeSets, appIdSet, redisOperations);
//                    // 4.保存邮箱数据
//                    saveMailData(intervalTimeSets, appIdSet, redisOperations);
//                    // 5.保存运营商数据
//                    saveOperatorData(intervalTimeSets, appIdSet, redisOperations);
//
//                    //6.保存合计所有商户后的总计数据
//                    saveAllTotalDayData(intervalTimeSets, redisOperations);
//                    saveAllTotalData(intervalTimeSets, redisOperations);
//
//                    //7.保存任务失败环节统计数据
//                    saveAllErrorDayData(intervalTimeSets, redisOperations);
//
//                    // 删除已生成数据key
//                    List<String> deleteList = Lists.newArrayList();
//                    intervalSets.forEach(time -> {
//                        if (!time.equals(currentInterval)) {
//                            deleteList.add(time);
//                        }
//                    });
//                    if (!deleteList.isEmpty()) {
//                        logger.info("TaskMonitorAlarm:刷新数据完成，清除数据：deleteList={},currentInterval={}", JSON.toJSONString(deleteList), currentInterval);
//                        String[] array = new String[deleteList.size()];
//                        redisOperations.opsForSet().remove(dayKey, deleteList.toArray(array));
//                    }
//                    return null;
//                }
//            });
//        } catch (Exception e) {
//            logger.error("TaskMonitorAlarm:statdataflushjob exception : ", e);
//        } finally {
//            logger.info("TaskMonitorAlarm:定时刷新数据完成，耗时time={}ms", System.currentTimeMillis() - start);
//            allAlarm();
//        }

    }
}
