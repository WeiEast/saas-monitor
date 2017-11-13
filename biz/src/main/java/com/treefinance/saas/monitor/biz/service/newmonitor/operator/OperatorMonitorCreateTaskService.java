package com.treefinance.saas.monitor.biz.service.newmonitor.operator;

import com.treefinance.saas.assistant.model.TaskOperatorMonitorMessage;

import java.util.Date;

/**
 * Created by haojiahong on 2017/11/2.
 */
public interface OperatorMonitorCreateTaskService {

    void updateAllDayData(Date intervalTime, TaskOperatorMonitorMessage message);

    void updateAllIntervalData(Date intervalTime, TaskOperatorMonitorMessage message);
}
