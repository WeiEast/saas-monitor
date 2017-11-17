package com.treefinance.saas.monitor.biz.service.impl;

import com.treefinance.saas.monitor.biz.config.DiamondConfig;
import com.treefinance.saas.monitor.biz.mq.producer.AlarmMessageProducer;
import com.treefinance.saas.monitor.biz.service.TaskExistMonitorAlarmService;
import com.treefinance.saas.monitor.common.utils.MonitorDateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by haojiahong on 2017/11/17.
 */
@Service
public class TaskExistMonitorAlarmServiceImpl implements TaskExistMonitorAlarmService {

    @Autowired
    private AlarmMessageProducer alarmMessageProducer;
    @Autowired
    private DiamondConfig diamondConfig;

    @Override
    public void alarmNoTask(Date startTime, Date endTime) {
        String mailDataBody = generateNoTaskMailDataBody(startTime, endTime);
        String title = generateTitle();
        alarmMessageProducer.sendMail4TaskExistMonitor(title, mailDataBody);
        String weChatBody = generateNoTaskWeChatBody(startTime, endTime);
        alarmMessageProducer.sendWebChart4TaskExistMonitor(weChatBody);


    }

    @Override
    public void alarmNoSuccessTask(Date startTime, Date endTime) {
        String mailDataBody = generateNoSuccessTaskMailDataBody(startTime, endTime);
        String title = generateTitle();
        alarmMessageProducer.sendMail4TaskExistMonitor(title, mailDataBody);
        String weChatBody = generateNoSuccessWeChatBody(startTime, endTime);
        alarmMessageProducer.sendWebChart4TaskExistMonitor(weChatBody);
    }

    private String generateNoSuccessWeChatBody(Date startTime, Date endTime) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("您好,").append("saas-").append(diamondConfig.getMonitorEnvironment())
                .append("发生任务预警,在")
                .append(MonitorDateUtils.format(startTime))
                .append("--")
                .append(MonitorDateUtils.format(endTime))
                .append("时段内没有任务成功,").append("请及时处理!").append("\n");
        return buffer.toString();
    }

    private String generateNoSuccessTaskMailDataBody(Date startTime, Date endTime) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<br>").append("您好,").append("saas-").append(diamondConfig.getMonitorEnvironment())
                .append("发生任务预警,在")
                .append(MonitorDateUtils.format(startTime))
                .append("--")
                .append(MonitorDateUtils.format(endTime))
                .append("时段内没有任务成功,").append("请及时处理!").append("</br>");
        return buffer.toString();
    }

    private String generateNoTaskWeChatBody(Date startTime, Date endTime) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("您好,").append("saas-").append(diamondConfig.getMonitorEnvironment())
                .append("发生任务预警,在")
                .append(MonitorDateUtils.format(startTime))
                .append("--")
                .append(MonitorDateUtils.format(endTime))
                .append("时段内没有任务创建,").append("请及时处理!").append("\n");
        return buffer.toString();
    }


    private String generateNoTaskMailDataBody(Date startTime, Date endTime) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<br>").append("您好,").append("saas-").append(diamondConfig.getMonitorEnvironment())
                .append("发生任务预警,在")
                .append(MonitorDateUtils.format(startTime))
                .append("--")
                .append(MonitorDateUtils.format(endTime))
                .append("时段内没有任务创建,").append("请及时处理!").append("</br>");
        return buffer.toString();
    }

    private String generateTitle() {
        return "saas-" + diamondConfig.getMonitorEnvironment() + "任务预警";
    }
}
