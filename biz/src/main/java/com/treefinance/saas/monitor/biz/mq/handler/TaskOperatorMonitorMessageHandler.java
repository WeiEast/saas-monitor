package com.treefinance.saas.monitor.biz.mq.handler;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.assistant.listener.TagBaseMessageHandler;
import com.treefinance.saas.assistant.model.MonitorTagEnum;
import com.treefinance.saas.assistant.model.TaskOperatorMonitorMessage;
import com.treefinance.saas.monitor.biz.helper.StatHelper;
import com.treefinance.saas.monitor.biz.service.newmonitor.operator.OperatorMonitorConfirmMobileService;
import com.treefinance.saas.monitor.biz.service.newmonitor.operator.OperatorMonitorCrawlFailService;
import com.treefinance.saas.monitor.biz.service.newmonitor.operator.OperatorMonitorLoginService;
import com.treefinance.saas.monitor.biz.service.newmonitor.operator.OperatorMonitorProcessFailService;
import com.treefinance.saas.monitor.common.enumeration.ETaskOperatorStatus;
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
    private OperatorMonitorConfirmMobileService operatorMonitorConfirmMobileService;
    @Autowired
    private OperatorMonitorLoginService operatorMonitorLoginService;
    @Autowired
    private OperatorMonitorCrawlFailService operatorMonitorCrawlFailService;
    @Autowired
    private OperatorMonitorProcessFailService operatorMonitorProcessFailService;


    @Override
    public MonitorTagEnum getMonitorType() {
        return MonitorTagEnum.TASK_OPERATOR;
    }

    @Override
    public void handleMessage(TaskOperatorMonitorMessage message) {
        long start = System.currentTimeMillis();
        try {
            Date dataTime = message.getDataTime();
            Date intervalTime = StatHelper.calculateIntervalTime(dataTime, 60);//按小时统计数据的时间点,如6:00,7:00
            ETaskOperatorStatus status = ETaskOperatorStatus.getStatus(message.getStatus());
            switch (status) {
                case COMFIRM_MOBILE:
                    operatorMonitorConfirmMobileService.updateIntervalData(intervalTime, message);
                    operatorMonitorConfirmMobileService.updateDayData(intervalTime, message);
                    break;
                case LOGIN:
                    operatorMonitorLoginService.updateIntervalData(intervalTime, message);
                    operatorMonitorLoginService.updateDayData(intervalTime, message);
                    break;
                case CRAWL_FAIL:
                    operatorMonitorCrawlFailService.updateIntervalData(intervalTime, message);
                    operatorMonitorCrawlFailService.updateDayData(intervalTime, message);
                    break;
                case PROCESS_FAIL:
                    operatorMonitorProcessFailService.updateIntervalData(intervalTime, message);
                    operatorMonitorProcessFailService.updateDayData(intervalTime, message);
                    break;
                default:
                    logger.error("运营商监控,更新数据时,数据类型有误,message={}", JSON.toJSONString(message));
                    break;
            }
        } finally {
            logger.info("运营商监控,handleMessage cost {} ms , message={}", System.currentTimeMillis() - start, JSON.toJSONString(message));
        }

    }
}
