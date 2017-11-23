package com.treefinance.saas.monitor.biz.mq.handler;

import com.treefinance.saas.assistant.listener.TagBaseMessageHandler;
import com.treefinance.saas.assistant.model.MonitorTagEnum;
import com.treefinance.saas.assistant.model.TaskMonitorMessage;
import com.treefinance.saas.monitor.biz.service.newmonitor.TaskExistMonitorService;
import com.treefinance.saas.monitor.biz.service.newmonitor.task.TaskMonitorPerMinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by haojiahong on 2017/11/23.
 */
@Component
public class TaskMonitorPerMinMessageHandler implements TagBaseMessageHandler<TaskMonitorMessage> {

    @Autowired
    private TaskExistMonitorService taskExistMonitorService;
    @Autowired
    private TaskMonitorPerMinService taskMonitorPerMinService;

    @Override
    public MonitorTagEnum getMonitorType() {
        return MonitorTagEnum.TASK;
    }

    @Override
    public void handleMessage(TaskMonitorMessage message) {
        //todo 搞个任务消息去重

        //任务是否存在处理
        taskExistMonitorService.doService(message);

        //商户访问统计表 merchant_stat_access
        taskMonitorPerMinService.statMerchantIntervalAccess(message);
        //商户日访问统计表 merchant_stat_day_access
        taskMonitorPerMinService.statMerchantDayAccess(message);

        //saas合计所有商户的访问统计表 saas_stat_access
        taskMonitorPerMinService.statSaasIntervalAccess(message);
        //saas合计所有商户的日访问统计表 saas_stat_day_access
        taskMonitorPerMinService.statSaasDayAccess(message);

        //商户银行访问统计表,商户邮箱访问统计表,商户邮箱访问统计表,商户邮箱访问统计表,以及后续会添加其他业务类型
        taskMonitorPerMinService.statMerchantAccessWithType(message);

        //任务失败环节统计表 saas_error_step_day_stat
        taskMonitorPerMinService.statSaasErrorStepDay(message);


    }
}
