package com.treefinance.saas.monitor.biz.service.newmonitor.operator;

import com.treefinance.saas.assistant.model.TaskOperatorMonitorMessage;

import java.util.Date;

/**
 * Created by haojiahong on 2017/10/30.
 */
public interface OperatorMonitorConfirmMobileService {

    void updateIntervalData(Date intervalTime, TaskOperatorMonitorMessage message);

    void updateDayData(Date intervalTime, TaskOperatorMonitorMessage message);

    void updateAllIntervalData(Date intervalTime, TaskOperatorMonitorMessage message);

    void updateAllDayData(Date intervalTime, TaskOperatorMonitorMessage message);
}
