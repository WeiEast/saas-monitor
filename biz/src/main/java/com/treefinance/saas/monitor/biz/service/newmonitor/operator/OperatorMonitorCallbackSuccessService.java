package com.treefinance.saas.monitor.biz.service.newmonitor.operator;

import com.treefinance.saas.assistant.model.TaskOperatorMonitorMessage;

import java.util.Date;

/**
 * Created by haojiahong on 2017/11/2.
 */
public interface OperatorMonitorCallbackSuccessService {

    void updateAllDayData(Date intervalTime, TaskOperatorMonitorMessage message);
}
