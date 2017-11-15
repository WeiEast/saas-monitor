package com.treefinance.saas.monitor.biz.mq.handler;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.assistant.listener.TagBaseMessageHandler;
import com.treefinance.saas.assistant.model.MonitorTagEnum;
import com.treefinance.saas.assistant.model.TaskOperatorMonitorMessage;
import com.treefinance.saas.grapserver.facade.model.enums.ETaskOperatorMonitorStatus;
import com.treefinance.saas.monitor.biz.config.DiamondConfig;
import com.treefinance.saas.monitor.biz.helper.TaskOperatorMonitorKeyHelper;
import com.treefinance.saas.monitor.biz.service.newmonitor.operator.OperatorMonitorActionStatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

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


    @Override
    public MonitorTagEnum getMonitorType() {
        return MonitorTagEnum.TASK_OPERATOR;
    }

    @Override
    public void handleMessage(TaskOperatorMonitorMessage message) {
        // todo 消息重复消费的问题
        logger.info("运营商监控,消息处理,message={}", JSON.toJSONString(message));
        long start = System.currentTimeMillis();
        try {
            Date dataTime = message.getDataTime();
            Date intervalTime = TaskOperatorMonitorKeyHelper.getRedisStatDateTime(dataTime, diamondConfig.getOperatorMonitorIntervalMinutes());//按小时统计数据的时间点,如6:00,7:00
            ETaskOperatorMonitorStatus status = ETaskOperatorMonitorStatus.getMonitorStats(message.getStatus());
            switch (status) {
                case CREATE_TASK:
                    operatorMonitorActionStatService.updateAllIntervalDataByTask(intervalTime, message, ETaskOperatorMonitorStatus.CREATE_TASK);
                    operatorMonitorActionStatService.updateAllDayDataByTask(intervalTime, message, ETaskOperatorMonitorStatus.CREATE_TASK);
                    operatorMonitorActionStatService.updateAllIntervalDataByUser(intervalTime, message, ETaskOperatorMonitorStatus.CREATE_TASK);
                    operatorMonitorActionStatService.updateAllDayDataByUser(intervalTime, message, ETaskOperatorMonitorStatus.CREATE_TASK);
                    break;
                case CONFIRM_MOBILE:
                    operatorMonitorActionStatService.updateIntervalDataByTask(intervalTime, message, ETaskOperatorMonitorStatus.CONFIRM_MOBILE);
                    operatorMonitorActionStatService.updateDayDataByTask(intervalTime, message, ETaskOperatorMonitorStatus.CONFIRM_MOBILE);
                    operatorMonitorActionStatService.updateAllIntervalDataByTask(intervalTime, message, ETaskOperatorMonitorStatus.CONFIRM_MOBILE);
                    operatorMonitorActionStatService.updateAllDayDataByTask(intervalTime, message, ETaskOperatorMonitorStatus.CONFIRM_MOBILE);

                    operatorMonitorActionStatService.updateIntervalDataByUser(intervalTime, message, ETaskOperatorMonitorStatus.CONFIRM_MOBILE);
                    operatorMonitorActionStatService.updateDayDataByUser(intervalTime, message, ETaskOperatorMonitorStatus.CONFIRM_MOBILE);
                    operatorMonitorActionStatService.updateAllIntervalDataByUser(intervalTime, message, ETaskOperatorMonitorStatus.CONFIRM_MOBILE);
                    operatorMonitorActionStatService.updateAllDayDataByUser(intervalTime, message, ETaskOperatorMonitorStatus.CONFIRM_MOBILE);
                    break;
                case START_LOGIN:
                    operatorMonitorActionStatService.updateIntervalDataByTask(intervalTime, message, ETaskOperatorMonitorStatus.START_LOGIN);
                    operatorMonitorActionStatService.updateDayDataByTask(intervalTime, message, ETaskOperatorMonitorStatus.START_LOGIN);
                    operatorMonitorActionStatService.updateAllIntervalDataByTask(intervalTime, message, ETaskOperatorMonitorStatus.START_LOGIN);
                    operatorMonitorActionStatService.updateAllDayDataByTask(intervalTime, message, ETaskOperatorMonitorStatus.START_LOGIN);

                    operatorMonitorActionStatService.updateIntervalDataByUser(intervalTime, message, ETaskOperatorMonitorStatus.START_LOGIN);
                    operatorMonitorActionStatService.updateDayDataByUser(intervalTime, message, ETaskOperatorMonitorStatus.START_LOGIN);
                    operatorMonitorActionStatService.updateAllIntervalDataByUser(intervalTime, message, ETaskOperatorMonitorStatus.START_LOGIN);
                    operatorMonitorActionStatService.updateAllDayDataByUser(intervalTime, message, ETaskOperatorMonitorStatus.START_LOGIN);
                    break;
                case LOGIN_SUCCESS:
                    operatorMonitorActionStatService.updateIntervalDataByTask(intervalTime, message, ETaskOperatorMonitorStatus.LOGIN_SUCCESS);
                    operatorMonitorActionStatService.updateDayDataByTask(intervalTime, message, ETaskOperatorMonitorStatus.LOGIN_SUCCESS);
                    operatorMonitorActionStatService.updateAllIntervalDataByTask(intervalTime, message, ETaskOperatorMonitorStatus.LOGIN_SUCCESS);
                    operatorMonitorActionStatService.updateAllDayDataByTask(intervalTime, message, ETaskOperatorMonitorStatus.LOGIN_SUCCESS);

                    operatorMonitorActionStatService.updateIntervalDataByUser(intervalTime, message, ETaskOperatorMonitorStatus.LOGIN_SUCCESS);
                    operatorMonitorActionStatService.updateDayDataByUser(intervalTime, message, ETaskOperatorMonitorStatus.LOGIN_SUCCESS);
                    operatorMonitorActionStatService.updateAllIntervalDataByUser(intervalTime, message, ETaskOperatorMonitorStatus.LOGIN_SUCCESS);
                    operatorMonitorActionStatService.updateAllDayDataByUser(intervalTime, message, ETaskOperatorMonitorStatus.LOGIN_SUCCESS);
                    break;
                case CRAWL_SUCCESS:
                    operatorMonitorActionStatService.updateIntervalDataByTask(intervalTime, message, ETaskOperatorMonitorStatus.CRAWL_SUCCESS);
                    operatorMonitorActionStatService.updateDayDataByTask(intervalTime, message, ETaskOperatorMonitorStatus.CRAWL_SUCCESS);
                    operatorMonitorActionStatService.updateAllIntervalDataByTask(intervalTime, message, ETaskOperatorMonitorStatus.CRAWL_SUCCESS);
                    operatorMonitorActionStatService.updateAllDayDataByTask(intervalTime, message, ETaskOperatorMonitorStatus.CRAWL_SUCCESS);

                    operatorMonitorActionStatService.updateIntervalDataByUser(intervalTime, message, ETaskOperatorMonitorStatus.CRAWL_SUCCESS);
                    operatorMonitorActionStatService.updateDayDataByUser(intervalTime, message, ETaskOperatorMonitorStatus.CRAWL_SUCCESS);
                    operatorMonitorActionStatService.updateAllIntervalDataByUser(intervalTime, message, ETaskOperatorMonitorStatus.CRAWL_SUCCESS);
                    operatorMonitorActionStatService.updateAllDayDataByUser(intervalTime, message, ETaskOperatorMonitorStatus.CRAWL_SUCCESS);
                    break;
                case PROCESS_SUCCESS:
                    operatorMonitorActionStatService.updateIntervalDataByTask(intervalTime, message, ETaskOperatorMonitorStatus.PROCESS_SUCCESS);
                    operatorMonitorActionStatService.updateDayDataByTask(intervalTime, message, ETaskOperatorMonitorStatus.PROCESS_SUCCESS);
                    operatorMonitorActionStatService.updateAllIntervalDataByTask(intervalTime, message, ETaskOperatorMonitorStatus.PROCESS_SUCCESS);
                    operatorMonitorActionStatService.updateAllDayDataByTask(intervalTime, message, ETaskOperatorMonitorStatus.PROCESS_SUCCESS);

                    operatorMonitorActionStatService.updateIntervalDataByUser(intervalTime, message, ETaskOperatorMonitorStatus.PROCESS_SUCCESS);
                    operatorMonitorActionStatService.updateDayDataByUser(intervalTime, message, ETaskOperatorMonitorStatus.PROCESS_SUCCESS);
                    operatorMonitorActionStatService.updateAllIntervalDataByUser(intervalTime, message, ETaskOperatorMonitorStatus.PROCESS_SUCCESS);
                    operatorMonitorActionStatService.updateAllDayDataByUser(intervalTime, message, ETaskOperatorMonitorStatus.PROCESS_SUCCESS);
                    break;
                case CALLBACK_SUCCESS:
                    operatorMonitorActionStatService.updateAllIntervalDataByTask(intervalTime, message, ETaskOperatorMonitorStatus.CALLBACK_SUCCESS);
                    operatorMonitorActionStatService.updateAllDayDataByTask(intervalTime, message, ETaskOperatorMonitorStatus.CALLBACK_SUCCESS);
                    operatorMonitorActionStatService.updateAllIntervalDataByUser(intervalTime, message, ETaskOperatorMonitorStatus.CALLBACK_SUCCESS);
                    operatorMonitorActionStatService.updateAllDayDataByUser(intervalTime, message, ETaskOperatorMonitorStatus.CALLBACK_SUCCESS);
                    break;
                default:
                    logger.error("运营商监控,消息处理,更新数据时,数据类型有误,message={}", JSON.toJSONString(message));
                    break;
            }
        } finally {
            logger.info("运营商监控,消息处理,耗时{}ms,message={}", System.currentTimeMillis() - start, JSON.toJSONString(message));
        }

    }
}
