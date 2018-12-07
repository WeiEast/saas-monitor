package com.treefinance.saas.monitor.biz.task;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.treefinance.saas.monitor.biz.service.newmonitor.task.TaskPerMinDataFlushService;
import com.treefinance.saas.monitor.share.cache.RedisDao;
import com.treefinance.saas.monitor.util.SystemUtils;
import com.treefinance.toolkit.util.DateUtils;
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
        if (SystemUtils.isPreProductContext()) {
            logger.info("定时任务,预发布环境暂不执行");
            return;
        }
        long start = System.currentTimeMillis();
        Date jobTime = new Date();//定时任务执行时间
        logger.info("任务监控,定时任务执行jobTime={}", DateUtils.format(jobTime));
        try {
            redisDao.getRedisTemplate().execute(new SessionCallback<Object>() {
                @Override
                public Object execute(RedisOperations redisOperations) throws DataAccessException {

                    //商户银行访问统计表,商户邮箱访问统计表,商户邮箱访问统计表,商户邮箱访问统计表,以及后续会添加其他业务类型
                    taskPerMinDataFlushService.statMerchantAccessWithType(redisOperations, jobTime);

                    //任务失败环节统计表 saas_error_step_day_stat
                    taskPerMinDataFlushService.statSaasErrorStepDay(redisOperations, jobTime);
                    return null;
                }
            });
        } catch (Exception e) {
            logger.error("任务监控,定时任务执行jobTime={}异常", DateUtils.format(jobTime), e);
        } finally {
            logger.info("任务监控,定时任务执行jobTime={}完成,耗时{}ms", DateUtils.format(jobTime), System.currentTimeMillis() - start);
        }
    }
}
