package com.treefinance.saas.monitor.biz.task;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.treefinance.saas.monitor.biz.config.DiamondConfig;
import com.treefinance.saas.monitor.biz.helper.RedisKeyHelper;
import com.treefinance.saas.monitor.common.cache.RedisDao;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;

import java.util.Date;
import java.util.Set;

/**
 * 数据统计Job
 * Created by yh-treefinance on 2017/5/25.
 */
public class StatDataFlushJob implements SimpleJob {
    private static final Logger logger = LoggerFactory.getLogger(StatDataFlushJob.class);
    @Autowired
    private RedisDao redisDao;
    @Autowired
    private DiamondConfig diamondConfig;

    @Override
    public void execute(ShardingContext shardingContext) {
        int intervalMinutes = diamondConfig.getMonitorIntervalMinutes();
        Date now = new Date();
        redisDao.getRedisTemplate().execute(new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                redisOperations.multi();

                String dayKey = RedisKeyHelper.keyOfDay(now);

                Set<String> intervalTimeSet = redisOperations.opsForSet().members(dayKey);
                if (CollectionUtils.isEmpty(intervalTimeSet)) {
                    return null;
                }
                intervalTimeSet.forEach(t -> {
                    Long time = Long.valueOf(t);
//                    String totalKey = RedisKeyHelper.keyOfTotal();

                    redisOperations.opsForSet().remove(dayKey, t);
                });
                return null;
            }
        });
    }
}
