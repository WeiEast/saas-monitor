package com.treefinance.saas.monitor.biz.task;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.treefinance.saas.monitor.biz.config.DiamondConfig;
import com.treefinance.saas.monitor.biz.helper.RedisKeyHelper;
import com.treefinance.saas.monitor.biz.service.AlarmService;
import com.treefinance.saas.monitor.common.cache.RedisDao;
import com.treefinance.saas.monitor.common.enumeration.EStatType;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;

import java.util.Set;

/**
 * 告警任务
 * Created by yh-treefinance on 2017/6/30.
 */
public class AlarmJob implements SimpleJob {
    private static final Logger logger = LoggerFactory.getLogger(StatDataFlushJob.class);
    @Autowired
    private RedisDao redisDao;
    @Autowired
    private DiamondConfig diamondConfig;
    @Autowired
    private AlarmService alarmService;

    @Override
    public void execute(ShardingContext shardingContext) {
        long start = System.currentTimeMillis();
        try {
            // 超阈值次数, 默认3次
            int thresholdCount = diamondConfig.getMonitorAlarmThresholdCount() == null ? 3 : diamondConfig.getMonitorAlarmThresholdCount();

            redisDao.getRedisTemplate().execute(new SessionCallback<Object>() {
                @Override
                public Object execute(RedisOperations redisOperations) throws DataAccessException {
                    // appId列表
                    String appIdKey = RedisKeyHelper.keyOfAppIds();
                    Set<String> appIdSet = redisOperations.opsForSet().members(appIdKey);
                    if (CollectionUtils.isEmpty(appIdSet)) {
                        return null;
                    }
                    appIdSet.forEach(appId -> {
                        for (EStatType statType : EStatType.values()) {
                            if (statType == EStatType.TOTAL) {
                                continue;
                            }
                            String alarmKey = RedisKeyHelper.keyOfAlarm(appId, statType);
                            Object flag = redisOperations.opsForValue().get(alarmKey);
                            if (flag == null) {
                                continue;
                            }
                            logger.info("alarm job running : {}={}  thresholdCount={} 。。。", alarmKey, flag, thresholdCount);
                            Integer alarmNums = Integer.valueOf(flag.toString());
                            if (alarmNums >= thresholdCount) {
                                alarmService.alarm(appId, statType);
                                redisOperations.delete(alarmKey);
                            }
                        }
                    });
                    return null;
                }
            });

        } catch (Exception e) {
            logger.error("alarm job exception ", e);
        } finally {
            logger.info("alarm job completed cost {} ms", (System.currentTimeMillis() - start));
        }
    }
}
