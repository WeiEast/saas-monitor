package com.treefinance.saas.monitor.biz.service.newmonitor.operator;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.treefinance.saas.assistant.model.TaskOperatorMonitorMessage;
import com.treefinance.saas.monitor.biz.helper.TaskOperatorMonitorKeyHelper;
import com.treefinance.saas.monitor.common.cache.RedisDao;
import com.treefinance.saas.monitor.common.enumeration.ETaskOperatorStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by haojiahong on 2017/10/30.
 */
@Component
public class TaskOperatorMonitorMessageProcessor {

    private final static Logger logger = LoggerFactory.getLogger(TaskOperatorMonitorMessageProcessor.class);

    @Autowired
    private RedisDao redisDao;


    /**
     * 按配置的时间间隔更新数据
     *
     * @param intervalTime
     * @param message
     * @param status
     */
    public void updateIntervalData(Date intervalTime, TaskOperatorMonitorMessage message, ETaskOperatorStatus status) {
        updateData(intervalTime, message, statMap -> statMap.put("dataType", status.getStatus() + ""),
                () -> TaskOperatorMonitorKeyHelper.keyOfIntervalStat(intervalTime, status));
    }

    /**
     * 按日统计数据
     *
     * @param intervalTime
     * @param message
     * @param status
     */
    public void updateDayData(Date intervalTime, TaskOperatorMonitorMessage message, ETaskOperatorStatus status) {
        updateData(intervalTime, message, statMap -> statMap.put("dataType", status.getStatus() + ""),
                () -> TaskOperatorMonitorKeyHelper.keyOfDayStat(intervalTime, status));
    }


    /**
     * 更新数据
     *
     * @param intervalTime 循环时间
     * @param message
     * @param dataInitor
     * @param keyGenerator
     */
    private void updateData(Date intervalTime, TaskOperatorMonitorMessage message, Consumer<Map<String, String>> dataInitor, Supplier<String> keyGenerator) {
        Map<String, String> statMap = Maps.newHashMap();
        Byte status = message.getStatus();
        if (dataInitor != null) {
            dataInitor.accept(statMap);
        }
        String key = keyGenerator.get();
        redisDao.getRedisTemplate().execute(new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                // 当日时间列表
                String dayKey = TaskOperatorMonitorKeyHelper.keyOfDay(intervalTime);
                redisOperations.opsForSet().add(dayKey, TaskOperatorMonitorKeyHelper.getDateTimeStr(intervalTime));

                statMap.put("dataTime", TaskOperatorMonitorKeyHelper.getDateTimeStr(intervalTime));
                // 判断是否有key
                BoundHashOperations<String, String, String> hashOperations = redisOperations.boundHashOps(key);
                if (!Boolean.TRUE.equals(hashOperations.hasKey(key))) {
                    hashOperations.put("dataTime", TaskOperatorMonitorKeyHelper.getDateTimeStr(intervalTime));
                    // 设定超时时间默认为2天
                    hashOperations.expire(2, TimeUnit.DAYS);
                }

                // 统计确认手机号人数
                if (ETaskOperatorStatus.COMFIRM_MOBILE.getStatus().equals(status)) {
                    Long confirmMobileCount = hashOperations.increment("confirmMobileCount", 1);
                    statMap.put("confirmMobileCount", confirmMobileCount + "");
                }
                // 统计开始登陆人数
                else if (ETaskOperatorStatus.LOGIN.getStatus().equals(status)) {
                    Long loginCount = hashOperations.increment("loginCount", 1);
                    statMap.put("loginCount", loginCount + "");
                }
                // 统计抓取失败人数
                else if (ETaskOperatorStatus.CRAWL_FAIL.getStatus().equals(status)) {
                    Long crawlFailCount = hashOperations.increment("crawlFailCount", 1);
                    statMap.put("crawlFailCount", crawlFailCount + "");
                }
                //统计洗数失败人数
                else if (ETaskOperatorStatus.PROCESS_FAIL.getStatus().equals(status)) {
                    Long processFailCount = hashOperations.increment("processFailCount", 1);
                    statMap.put("processFailCount", processFailCount + "");
                }
                return null;
            }
        });
        logger.info("运营商监控,update redis access data: key={},value={}", key, JSON.toJSONString(statMap));
    }


}
