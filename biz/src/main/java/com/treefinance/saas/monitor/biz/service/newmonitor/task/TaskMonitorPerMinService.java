package com.treefinance.saas.monitor.biz.service.newmonitor.task;

import com.treefinance.saas.assistant.model.TaskMonitorMessage;

/**
 * Created by haojiahong on 2017/11/23.
 */
public interface TaskMonitorPerMinService {

    void statMerchantAccessWithType(TaskMonitorMessage message);

    void statSaasErrorStepDay(TaskMonitorMessage message);
}
