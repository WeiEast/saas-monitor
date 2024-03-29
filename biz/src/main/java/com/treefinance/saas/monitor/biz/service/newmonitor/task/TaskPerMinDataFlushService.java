package com.treefinance.saas.monitor.biz.service.newmonitor.task;

import org.springframework.data.redis.core.RedisOperations;

import java.util.Date;

/**
 * Created by haojiahong on 2017/11/23.
 */
public interface TaskPerMinDataFlushService {

    void statSaasErrorStepDay(RedisOperations redisOperations, Date jobTime);

    void statMerchantAccessWithType(RedisOperations redisOperations, Date jobTime);
}
