package com.treefinance.saas.monitor.biz.mq.handler;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.assistant.listener.TagBaseMessageHandler;
import com.treefinance.saas.assistant.model.MonitorTagEnum;
import com.treefinance.saas.assistant.model.TaskMonitorMessage;
import com.treefinance.saas.monitor.biz.helper.TaskMonitorPerMinKeyHelper;
import com.treefinance.saas.monitor.biz.service.newmonitor.TaskExistMonitorService;
import com.treefinance.saas.monitor.biz.service.newmonitor.task.TaskMonitorPerMinService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by haojiahong on 2017/11/23.
 */
@Component
public class TaskMonitorPerMinMessageHandler implements TagBaseMessageHandler<TaskMonitorMessage> {

    private static final Logger logger = LoggerFactory.getLogger(TaskMonitorPerMinMessageHandler.class);

    @Autowired
    private TaskExistMonitorService taskExistMonitorService;
    @Autowired
    private TaskMonitorPerMinService taskMonitorPerMinService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public MonitorTagEnum getMonitorType() {
        return MonitorTagEnum.TASK;
    }

    @Override
    public void handleMessage(TaskMonitorMessage message) {

        //任务重复消息不处理
        Date redisTime = TaskMonitorPerMinKeyHelper.getRedisStatDateTime(message.getCompleteTime(), 10);
        String taskLogKey = TaskMonitorPerMinKeyHelper.keyOfTaskLog(redisTime);
        BoundSetOperations<String, Object> setOperations = redisTemplate.boundSetOps(taskLogKey);
        if (setOperations.isMember(message.getTaskId().toString())) {
            logger.info("任务监控,消息处理,message={}重复发送不再统计.message={}", JSON.toJSONString(message));
            return;
        }
        setOperations.add(message.getTaskId().toString());
        if (setOperations.getExpire() == -1) {
            setOperations.expire(1, TimeUnit.HOURS);
        }

        //任务是否存在处理
        taskExistMonitorService.doService(message);

        //商户访问统计表 merchant_stat_access
        taskMonitorPerMinService.statMerchantIntervalAccess(message);
        //商户日访问统计表 merchant_stat_day_access
        taskMonitorPerMinService.statMerchantDayAccess(message);

        //saas合计所有商户的访问统计表 saas_stat_access
        taskMonitorPerMinService.statSaasIntervalAccess(message);
        //saas合计所有商户的日访问统计表 saas_stat_day_access
        taskMonitorPerMinService.statSaasDayAccess(message);

        //商户银行访问统计表,商户邮箱访问统计表,商户邮箱访问统计表,商户邮箱访问统计表,以及后续会添加其他业务类型
        taskMonitorPerMinService.statMerchantAccessWithType(message);

        //任务失败环节统计表 saas_error_step_day_stat
        taskMonitorPerMinService.statSaasErrorStepDay(message);


    }
}
