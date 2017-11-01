package com.treefinance.saas.monitor.biz.service.newmonitor.operator.impl;

import com.treefinance.saas.assistant.model.TaskOperatorMonitorMessage;
import com.treefinance.saas.monitor.biz.service.newmonitor.operator.OperatorMonitorConfirmMobileService;
import com.treefinance.saas.monitor.biz.service.newmonitor.operator.TaskOperatorMonitorMessageProcessor;
import com.treefinance.saas.monitor.common.enumeration.ETaskOperatorStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by haojiahong on 2017/10/30.
 */
@Service
public class OperatorMonitorConfirmMobileServiceImpl implements OperatorMonitorConfirmMobileService {

    @Autowired
    private TaskOperatorMonitorMessageProcessor taskOperatorMonitorMessageProcessor;

    @Override
    public void updateIntervalData(Date intervalTime, TaskOperatorMonitorMessage message) {
        taskOperatorMonitorMessageProcessor.updateIntervalData(intervalTime, message, ETaskOperatorStatus.COMFIRM_MOBILE);

    }

    @Override
    public void updateDayData(Date intervalTime, TaskOperatorMonitorMessage message) {
        taskOperatorMonitorMessageProcessor.updateDayData(intervalTime, message, ETaskOperatorStatus.COMFIRM_MOBILE);

    }
}
