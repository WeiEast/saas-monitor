package com.treefinance.saas.monitor.biz.task;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.treefinance.saas.monitor.biz.service.newmonitor.task.TaskPerMinDataFlushService;
import com.treefinance.saas.monitor.common.cache.RedisDao;
import com.treefinance.saas.monitor.common.utils.MonitorDateUtils;
import com.treefinance.saas.monitor.common.utils.MonitorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;

import java.util.Date;

/**
 * Created by haojiahong on 2017/11/23.
 */
public class TaskPerMinDataFlushJob implements SimpleJob {
    private static final Logger logger = LoggerFactory.getLogger(TaskPerMinDataFlushJob.class);
    @Autowired
    private RedisDao redisDao;
    @Autowired
    private TaskPerMinDataFlushService taskPerMinDataFlushService;

    @Override
    public void execute(ShardingContext shardingContext) {
        if (MonitorUtils.isPreProductContext()) {
            logger.info("定时任务,预发布环境暂不执行");
            return;
        }
        long start = System.currentTimeMillis();
        Date jobTime = new Date();//定时任务执行时间
        logger.info("任务监控,定时任务执行jobTime={}", MonitorDateUtils.format(jobTime));
        try {
            redisDao.getRedisTemplate().execute(new SessionCallback<Object>() {
                @Override
                public Object execute(RedisOperations redisOperations) throws DataAccessException {

//                    //商户访问统计表 merchant_stat_access
//                    taskPerMinDataFlushService.statMerchantIntervalAccess(redisOperations, jobTime);
//                    //商户日访问统计表 merchant_stat_day_access
//                    taskPerMinDataFlushService.statMerchantDayAccess(redisOperations, jobTime);
//
//                    //saas合计所有商户的访问统计表 saas_stat_access
//                    taskPerMinDataFlushService.statSaasIntervalAccess(redisOperations, jobTime);
//                    //saas合计所有商户的日访问统计表 saas_stat_day_access
//                    taskPerMinDataFlushService.statSaasDayAccess(redisOperations, jobTime);

                    //商户银行访问统计表,商户邮箱访问统计表,商户邮箱访问统计表,商户邮箱访问统计表,以及后续会添加其他业务类型
                    taskPerMinDataFlushService.statMerchantAccessWithType(redisOperations, jobTime);

                    //任务失败环节统计表 saas_error_step_day_stat
                    taskPerMinDataFlushService.statSaasErrorStepDay(redisOperations, jobTime);
                    return null;
                }
            });
        } catch (Exception e) {
            logger.error("任务监控,定时任务执行jobTime={}异常", MonitorDateUtils.format(jobTime), e);
        } finally {
            logger.info("任务监控,定时任务执行jobTime={}完成,耗时{}ms", MonitorDateUtils.format(jobTime), System.currentTimeMillis() - start);
        }
    }
}
