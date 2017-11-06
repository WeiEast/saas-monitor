package com.treefinance.saas.monitor.biz.mq.handler;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.assistant.listener.TagBaseMessageHandler;
import com.treefinance.saas.assistant.model.MonitorTagEnum;
import com.treefinance.saas.assistant.model.TaskOperatorMonitorMessage;
import com.treefinance.saas.grapserver.facade.model.enums.ETaskOperatorMonitorStatus;
import com.treefinance.saas.monitor.biz.config.DiamondConfig;
import com.treefinance.saas.monitor.biz.helper.TaskOperatorMonitorKeyHelper;
import com.treefinance.saas.monitor.biz.service.newmonitor.operator.*;
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
    private OperatorMonitorCreateTaskService operatorMonitorCreateTaskService;
    @Autowired
    private OperatorMonitorLoginSuccessService operatorMonitorLoginSuccessService;
    @Autowired
    private OperatorMonitorConfirmMobileService operatorMonitorConfirmMobileService;
    @Autowired
    private OperatorMonitorCrawlSuccessService operatorMonitorCrawlFailService;
    @Autowired
    private OperatorMonitorProcessSuccessService operatorMonitorProcessFailService;
    @Autowired
    private OperatorMonitorCallbackSuccessService operatorMonitorCallbackSuccessService;


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
            ETaskOperatorMonitorStatus status = ETaskOperatorMonitorStatus.getMonitorStats(message.getStatus());
            switch (status) {
                case CREATE_TASK:
                    operatorMonitorCreateTaskService.updateAllDayData(intervalTime, message);
                    break;
                case COMFIRM_MOBILE:
                    operatorMonitorConfirmMobileService.updateIntervalData(intervalTime, message);
                    operatorMonitorConfirmMobileService.updateDayData(intervalTime, message);
                    break;
                case LOGIN_SUCCESS:
                    operatorMonitorLoginSuccessService.updateIntervalData(intervalTime, message);
                    operatorMonitorLoginSuccessService.updateDayData(intervalTime, message);
                    operatorMonitorLoginSuccessService.updateAllDayData(intervalTime, message);
                    break;
                case CRAWL_SUCCESS:
                    operatorMonitorCrawlFailService.updateIntervalData(intervalTime, message);
                    operatorMonitorCrawlFailService.updateDayData(intervalTime, message);
                    operatorMonitorCrawlFailService.updateAllDayData(intervalTime, message);
                    break;
                case PROCESS_SUCCESS:
                    operatorMonitorProcessFailService.updateIntervalData(intervalTime, message);
                    operatorMonitorProcessFailService.updateDayData(intervalTime, message);
                    operatorMonitorProcessFailService.updateAllDayData(intervalTime, message);
                    break;
                case CALLBACK_SUCCESS:
                    operatorMonitorCallbackSuccessService.updateAllDayData(intervalTime, message);
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
