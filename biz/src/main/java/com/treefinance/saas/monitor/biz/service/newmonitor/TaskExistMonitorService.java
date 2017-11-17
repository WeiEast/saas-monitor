package com.treefinance.saas.monitor.biz.service.newmonitor;

import com.treefinance.saas.assistant.model.TaskMonitorMessage;

/**
 * Created by haojiahong on 2017/11/17.
 */
public interface TaskExistMonitorService {

    void doService(TaskMonitorMessage message);
}
