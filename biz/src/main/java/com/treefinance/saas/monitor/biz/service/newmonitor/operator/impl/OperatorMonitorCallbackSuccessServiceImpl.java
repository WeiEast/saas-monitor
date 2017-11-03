package com.treefinance.saas.monitor.biz.service.newmonitor.operator.impl;

import com.treefinance.saas.assistant.model.TaskOperatorMonitorMessage;
import com.treefinance.saas.grapserver.facade.model.enums.ETaskOperatorMonitorStatus;
import com.treefinance.saas.monitor.biz.service.newmonitor.operator.OperatorMonitorCallbackSuccessService;
import com.treefinance.saas.monitor.biz.service.newmonitor.operator.TaskOperatorMonitorMessageProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by haojiahong on 2017/11/2.
 */
@Service
public class OperatorMonitorCallbackSuccessServiceImpl implements OperatorMonitorCallbackSuccessService {

    @Autowired
    private TaskOperatorMonitorMessageProcessor taskOperatorMonitorMessageProcessor;

    @Override
    public void updateAllDayData(Date intervalTime, TaskOperatorMonitorMessage message) {
        taskOperatorMonitorMessageProcessor.updateAllDayData(intervalTime, message, ETaskOperatorMonitorStatus.CALLBACK_SUCCESS);

    }
}
