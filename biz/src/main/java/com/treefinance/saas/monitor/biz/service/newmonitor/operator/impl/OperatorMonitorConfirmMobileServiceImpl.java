package com.treefinance.saas.monitor.biz.service.newmonitor.operator.impl;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.assistant.model.TaskOperatorMonitorMessage;
import com.treefinance.saas.grapserver.facade.model.enums.ETaskOperatorMonitorStatus;
import com.treefinance.saas.monitor.biz.service.newmonitor.operator.OperatorMonitorConfirmMobileService;
import com.treefinance.saas.monitor.biz.service.newmonitor.operator.TaskOperatorMonitorMessageProcessor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by haojiahong on 2017/10/30.
 */
@Service
public class OperatorMonitorConfirmMobileServiceImpl implements OperatorMonitorConfirmMobileService {
    private static final Logger logger = LoggerFactory.getLogger(OperatorMonitorConfirmMobileService.class);

    @Autowired
    private TaskOperatorMonitorMessageProcessor taskOperatorMonitorMessageProcessor;

    @Override
    public void updateIntervalData(Date intervalTime, TaskOperatorMonitorMessage message) {
        if (message == null || StringUtils.isBlank(message.getGroupCode()) || StringUtils.isBlank(message.getGroupName())) {
            logger.error("运营商监控,消息处理,groupCode,groupName为空,message={}", JSON.toJSONString(message));
            return;
        }
        taskOperatorMonitorMessageProcessor.updateIntervalData(intervalTime, message, ETaskOperatorMonitorStatus.CONFIRM_MOBILE);

    }

    @Override
    public void updateDayData(Date intervalTime, TaskOperatorMonitorMessage message) {
        if (message == null || StringUtils.isBlank(message.getGroupCode()) || StringUtils.isBlank(message.getGroupName())) {
            logger.error("运营商监控,消息处理,groupCode,groupName为空,message={}", JSON.toJSONString(message));
            return;
        }
        taskOperatorMonitorMessageProcessor.updateDayData(intervalTime, message, ETaskOperatorMonitorStatus.CONFIRM_MOBILE);

    }

    @Override
    public void updateAllIntervalData(Date intervalTime, TaskOperatorMonitorMessage message) {
        taskOperatorMonitorMessageProcessor.updateAllIntervalData(intervalTime, message, ETaskOperatorMonitorStatus.CONFIRM_MOBILE);
    }

    @Override
    public void updateAllDayData(Date intervalTime, TaskOperatorMonitorMessage message) {
        taskOperatorMonitorMessageProcessor.updateAllDayData(intervalTime, message, ETaskOperatorMonitorStatus.CONFIRM_MOBILE);

    }

}
