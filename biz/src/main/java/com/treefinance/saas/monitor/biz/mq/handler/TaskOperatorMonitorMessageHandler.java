package com.treefinance.saas.monitor.biz.mq.handler;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.treefinance.saas.assistant.listener.TagBaseMessageHandler;
import com.treefinance.saas.assistant.model.MonitorTagEnum;
import com.treefinance.saas.assistant.model.TaskOperatorMonitorMessage;
import com.treefinance.saas.grapserver.facade.model.enums.ETaskOperatorMonitorStatus;
import com.treefinance.saas.monitor.biz.config.DiamondConfig;
import com.treefinance.saas.monitor.biz.helper.TaskOperatorMonitorKeyHelper;
import com.treefinance.saas.monitor.biz.service.newmonitor.operator.OperatorMonitorActionStatService;
import com.treefinance.saas.monitor.common.utils.DataConverterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by haojiahong on 2017/10/30.
 */
@Component
public class TaskOperatorMonitorMessageHandler implements TagBaseMessageHandler<TaskOperatorMonitorMessage> {
    private final static Logger logger = LoggerFactory.getLogger(TaskOperatorMonitorMessageHandler.class);

    @Autowired
    private DiamondConfig diamondConfig;
    @Autowired
    private OperatorMonitorActionStatService operatorMonitorActionStatService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    @Override
    public MonitorTagEnum getMonitorType() {
        return MonitorTagEnum.TASK_OPERATOR;
    }

    @Override
    public void handleMessage(TaskOperatorMonitorMessage message) {
        logger.info("运营商监控,消息处理,message={}", JSON.toJSONString(message));
        long start = System.currentTimeMillis();
        try {
            Date dataTime = message.getDataTime();
            Date intervalTime = TaskOperatorMonitorKeyHelper.getRedisStatDateTime(dataTime, diamondConfig.getOperatorMonitorIntervalMinutes());//按小时统计数据的时间点,如6:00,7:00
            //任务重复消息不处理
            String messageLogKey = TaskOperatorMonitorKeyHelper.keyOfMessageLog(intervalTime);
            BoundSetOperations<String, Object> setOperations = redisTemplate.boundSetOps(messageLogKey);
            if (!Boolean.TRUE.equals(redisTemplate.hasKey(messageLogKey))) {
                setOperations.expire(2, TimeUnit.HOURS);
            }
            StringBuilder sb = new StringBuilder();
            String value = sb.append(message.getTaskId()).append("-").append(message.getStatus()).toString();
            if (setOperations.isMember(value)) {
                logger.info("运营商监控,消息处理,message={}重复发送不再统计.message={}", JSON.toJSONString(message));
                return;
            }
            setOperations.add(value);

            ETaskOperatorMonitorStatus status = ETaskOperatorMonitorStatus.getMonitorStats(message.getStatus());
            TaskOperatorMonitorMessage virtualTotalMessage = DataConverterUtils.convert(message, TaskOperatorMonitorMessage.class);
            virtualTotalMessage.setAppId("virtual_total_stat_appId");
            List<TaskOperatorMonitorMessage> list = Lists.newArrayList(message, virtualTotalMessage);
            for (TaskOperatorMonitorMessage msg : list) {
                switch (status) {
                    case CREATE_TASK:
                        operatorMonitorActionStatService.updateAllIntervalDataByTask(intervalTime, msg, ETaskOperatorMonitorStatus.CREATE_TASK);
                        operatorMonitorActionStatService.updateAllDayDataByTask(intervalTime, msg, ETaskOperatorMonitorStatus.CREATE_TASK);
                        operatorMonitorActionStatService.updateAllIntervalDataByUser(intervalTime, msg, ETaskOperatorMonitorStatus.CREATE_TASK);
                        operatorMonitorActionStatService.updateAllDayDataByUser(intervalTime, msg, ETaskOperatorMonitorStatus.CREATE_TASK);
                        break;
                    case CONFIRM_MOBILE:
                        operatorMonitorActionStatService.updateIntervalDataByTask(intervalTime, msg, ETaskOperatorMonitorStatus.CONFIRM_MOBILE);
                        operatorMonitorActionStatService.updateDayDataByTask(intervalTime, msg, ETaskOperatorMonitorStatus.CONFIRM_MOBILE);
                        operatorMonitorActionStatService.updateAllIntervalDataByTask(intervalTime, msg, ETaskOperatorMonitorStatus.CONFIRM_MOBILE);
                        operatorMonitorActionStatService.updateAllDayDataByTask(intervalTime, msg, ETaskOperatorMonitorStatus.CONFIRM_MOBILE);

                        operatorMonitorActionStatService.updateIntervalDataByUser(intervalTime, msg, ETaskOperatorMonitorStatus.CONFIRM_MOBILE);
                        operatorMonitorActionStatService.updateDayDataByUser(intervalTime, msg, ETaskOperatorMonitorStatus.CONFIRM_MOBILE);
                        operatorMonitorActionStatService.updateAllIntervalDataByUser(intervalTime, msg, ETaskOperatorMonitorStatus.CONFIRM_MOBILE);
                        operatorMonitorActionStatService.updateAllDayDataByUser(intervalTime, msg, ETaskOperatorMonitorStatus.CONFIRM_MOBILE);
                        break;
                    case START_LOGIN:
                        operatorMonitorActionStatService.updateIntervalDataByTask(intervalTime, msg, ETaskOperatorMonitorStatus.START_LOGIN);
                        operatorMonitorActionStatService.updateDayDataByTask(intervalTime, msg, ETaskOperatorMonitorStatus.START_LOGIN);
                        operatorMonitorActionStatService.updateAllIntervalDataByTask(intervalTime, msg, ETaskOperatorMonitorStatus.START_LOGIN);
                        operatorMonitorActionStatService.updateAllDayDataByTask(intervalTime, msg, ETaskOperatorMonitorStatus.START_LOGIN);

                        operatorMonitorActionStatService.updateIntervalDataByUser(intervalTime, msg, ETaskOperatorMonitorStatus.START_LOGIN);
                        operatorMonitorActionStatService.updateDayDataByUser(intervalTime, msg, ETaskOperatorMonitorStatus.START_LOGIN);
                        operatorMonitorActionStatService.updateAllIntervalDataByUser(intervalTime, msg, ETaskOperatorMonitorStatus.START_LOGIN);
                        operatorMonitorActionStatService.updateAllDayDataByUser(intervalTime, msg, ETaskOperatorMonitorStatus.START_LOGIN);
                        break;
                    case LOGIN_SUCCESS:
                        operatorMonitorActionStatService.updateIntervalDataByTask(intervalTime, msg, ETaskOperatorMonitorStatus.LOGIN_SUCCESS);
                        operatorMonitorActionStatService.updateDayDataByTask(intervalTime, msg, ETaskOperatorMonitorStatus.LOGIN_SUCCESS);
                        operatorMonitorActionStatService.updateAllIntervalDataByTask(intervalTime, msg, ETaskOperatorMonitorStatus.LOGIN_SUCCESS);
                        operatorMonitorActionStatService.updateAllDayDataByTask(intervalTime, msg, ETaskOperatorMonitorStatus.LOGIN_SUCCESS);

                        operatorMonitorActionStatService.updateIntervalDataByUser(intervalTime, msg, ETaskOperatorMonitorStatus.LOGIN_SUCCESS);
                        operatorMonitorActionStatService.updateDayDataByUser(intervalTime, msg, ETaskOperatorMonitorStatus.LOGIN_SUCCESS);
                        operatorMonitorActionStatService.updateAllIntervalDataByUser(intervalTime, msg, ETaskOperatorMonitorStatus.LOGIN_SUCCESS);
                        operatorMonitorActionStatService.updateAllDayDataByUser(intervalTime, msg, ETaskOperatorMonitorStatus.LOGIN_SUCCESS);
                        break;
                    case CRAWL_SUCCESS:
                        operatorMonitorActionStatService.updateIntervalDataByTask(intervalTime, msg, ETaskOperatorMonitorStatus.CRAWL_SUCCESS);
                        operatorMonitorActionStatService.updateDayDataByTask(intervalTime, msg, ETaskOperatorMonitorStatus.CRAWL_SUCCESS);
                        operatorMonitorActionStatService.updateAllIntervalDataByTask(intervalTime, msg, ETaskOperatorMonitorStatus.CRAWL_SUCCESS);
                        operatorMonitorActionStatService.updateAllDayDataByTask(intervalTime, msg, ETaskOperatorMonitorStatus.CRAWL_SUCCESS);

                        operatorMonitorActionStatService.updateIntervalDataByUser(intervalTime, msg, ETaskOperatorMonitorStatus.CRAWL_SUCCESS);
                        operatorMonitorActionStatService.updateDayDataByUser(intervalTime, msg, ETaskOperatorMonitorStatus.CRAWL_SUCCESS);
                        operatorMonitorActionStatService.updateAllIntervalDataByUser(intervalTime, msg, ETaskOperatorMonitorStatus.CRAWL_SUCCESS);
                        operatorMonitorActionStatService.updateAllDayDataByUser(intervalTime, msg, ETaskOperatorMonitorStatus.CRAWL_SUCCESS);
                        break;
                    case PROCESS_SUCCESS:
                        operatorMonitorActionStatService.updateIntervalDataByTask(intervalTime, msg, ETaskOperatorMonitorStatus.PROCESS_SUCCESS);
                        operatorMonitorActionStatService.updateDayDataByTask(intervalTime, msg, ETaskOperatorMonitorStatus.PROCESS_SUCCESS);
                        operatorMonitorActionStatService.updateAllIntervalDataByTask(intervalTime, msg, ETaskOperatorMonitorStatus.PROCESS_SUCCESS);
                        operatorMonitorActionStatService.updateAllDayDataByTask(intervalTime, msg, ETaskOperatorMonitorStatus.PROCESS_SUCCESS);

                        operatorMonitorActionStatService.updateIntervalDataByUser(intervalTime, msg, ETaskOperatorMonitorStatus.PROCESS_SUCCESS);
                        operatorMonitorActionStatService.updateDayDataByUser(intervalTime, msg, ETaskOperatorMonitorStatus.PROCESS_SUCCESS);
                        operatorMonitorActionStatService.updateAllIntervalDataByUser(intervalTime, msg, ETaskOperatorMonitorStatus.PROCESS_SUCCESS);
                        operatorMonitorActionStatService.updateAllDayDataByUser(intervalTime, msg, ETaskOperatorMonitorStatus.PROCESS_SUCCESS);
                        break;
                    case CALLBACK_SUCCESS:
                        operatorMonitorActionStatService.updateIntervalDataByTask(intervalTime, msg, ETaskOperatorMonitorStatus.CALLBACK_SUCCESS);
                        operatorMonitorActionStatService.updateDayDataByTask(intervalTime, msg, ETaskOperatorMonitorStatus.CALLBACK_SUCCESS);
                        operatorMonitorActionStatService.updateAllIntervalDataByTask(intervalTime, msg, ETaskOperatorMonitorStatus.CALLBACK_SUCCESS);
                        operatorMonitorActionStatService.updateAllDayDataByTask(intervalTime, msg, ETaskOperatorMonitorStatus.CALLBACK_SUCCESS);

                        operatorMonitorActionStatService.updateIntervalDataByUser(intervalTime, msg, ETaskOperatorMonitorStatus.CALLBACK_SUCCESS);
                        operatorMonitorActionStatService.updateDayDataByUser(intervalTime, msg, ETaskOperatorMonitorStatus.CALLBACK_SUCCESS);
                        operatorMonitorActionStatService.updateAllIntervalDataByUser(intervalTime, msg, ETaskOperatorMonitorStatus.CALLBACK_SUCCESS);
                        operatorMonitorActionStatService.updateAllDayDataByUser(intervalTime, msg, ETaskOperatorMonitorStatus.CALLBACK_SUCCESS);
                        break;
                    default:
                        logger.error("运营商监控,消息处理,更新数据时,数据类型有误,msg={}", JSON.toJSONString(msg));
                        break;
                }
            }

        } finally {
            logger.info("运营商监控,消息处理,耗时{}ms,message={}", System.currentTimeMillis() - start, JSON.toJSONString(message));
        }

    }
}
