package com.treefinance.saas.monitor.biz.service.impl;

import com.treefinance.saas.monitor.biz.config.DiamondConfig;
import com.treefinance.saas.monitor.biz.mq.producer.AlarmMessageProducer;
import com.treefinance.saas.monitor.biz.service.TaskExistMonitorAlarmService;
import com.treefinance.saas.monitor.common.enumeration.EBizType;
import com.treefinance.saas.monitor.common.utils.MonitorDateUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by haojiahong on 2017/11/17.
 */
@Service
public class TaskExistMonitorAlarmServiceImpl implements TaskExistMonitorAlarmService {

    private static final Logger logger = LoggerFactory.getLogger(TaskExistMonitorAlarmService.class);

    @Autowired
    private AlarmMessageProducer alarmMessageProducer;
    @Autowired
    private DiamondConfig diamondConfig;

    @Override
    public void alarmNoTask(Date startTime, Date endTime) {

        String mailSwitch = diamondConfig.getTaskExistAlarmMailSwitch();
        String weChatSwitch = diamondConfig.getTaskExistAlarmWechatSwitch();
        if (StringUtils.equalsIgnoreCase(mailSwitch, "on")) {
            String mailDataBody = generateNoTaskMailDataBody(startTime, endTime);
            String title = generateNoTaskTitle();
            alarmMessageProducer.sendMail4TaskExistMonitor(title, mailDataBody);
        } else {
            logger.info("任务预警,发送邮件开关已关闭");
        }

        if (StringUtils.equalsIgnoreCase(weChatSwitch, "on")) {
            String weChatBody = generateNoTaskWeChatBody(startTime, endTime);
            alarmMessageProducer.sendWebChart4TaskExistMonitor(weChatBody);
        } else {
            logger.info("任务预警,发送微信开关已关闭");
        }


    }

    @Override
    public void alarmNoSuccessTask(Date startTime, Date endTime) {
        String mailSwitch = diamondConfig.getTaskExistAlarmMailSwitch();
        String weChatSwitch = diamondConfig.getTaskExistAlarmWechatSwitch();
        if (StringUtils.equalsIgnoreCase(mailSwitch, "on")) {
            String mailDataBody = generateNoSuccessTaskMailDataBody(startTime, endTime);
            String title = generateNoSuccessTaskTitle();
            alarmMessageProducer.sendMail4TaskExistMonitor(title, mailDataBody);
        } else {
            logger.info("任务预警,发送邮件开关已关闭");
        }

        if (StringUtils.equalsIgnoreCase(weChatSwitch, "on")) {
            String weChatBody = generateNoSuccessWeChatBody(startTime, endTime);
            alarmMessageProducer.sendWebChart4TaskExistMonitor(weChatBody);
        } else {
            logger.info("任务预警,发送微信开关已关闭");
        }
    }

    @Override
    public void alarmNoSuccessTaskWithType(Date startTime, Date endTime, EBizType bizType) {
        String mailSwitch = diamondConfig.getTaskExistAlarmMailSwitch();
        String weChatSwitch = diamondConfig.getTaskExistAlarmWechatSwitch();
        if (StringUtils.equalsIgnoreCase(mailSwitch, "on")) {
            String mailDataBody = generateNoSuccessTaskWithTypeMailDataBody(startTime, endTime, bizType);
            String title = generateNoSuccessTaskWithTypeTitle(bizType);
            alarmMessageProducer.sendMail4TaskExistMonitor(title, mailDataBody);
        } else {
            logger.info("任务预警,发送邮件开关已关闭");
        }

        if (StringUtils.equalsIgnoreCase(weChatSwitch, "on")) {
            String weChatBody = generateNoSuccessWithTypeWeChatBody(startTime, endTime, bizType);
            alarmMessageProducer.sendWebChart4TaskExistMonitor(weChatBody);
        } else {
            logger.info("任务预警,发送微信开关已关闭");
        }
    }

    private String generateNoSuccessWithTypeWeChatBody(Date startTime, Date endTime, EBizType bizType) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("您好,").append("saas-").append(diamondConfig.getMonitorEnvironment())
                .append("发生任务预警,在")
                .append(MonitorDateUtils.format(startTime))
                .append("--")
                .append(MonitorDateUtils.format(endTime))
                .append("时段内")
                .append("【")
                .append(bizType.getDesc())
                .append("】")
                .append("没有任务成功,").append("请及时处理!");
        return buffer.toString();
    }

    private String generateNoSuccessTaskWithTypeTitle(EBizType bizType) {
        return "saas-" + diamondConfig.getMonitorEnvironment() + "【" + bizType.getDesc() + "】" + "无成功任务预警";
    }

    private String generateNoSuccessTaskWithTypeMailDataBody(Date startTime, Date endTime, EBizType bizType) {

        StringBuffer buffer = new StringBuffer();
        buffer.append("您好,").append("saas-").append(diamondConfig.getMonitorEnvironment())
                .append("发生任务预警,在")
                .append(MonitorDateUtils.format(startTime))
                .append("--")
                .append(MonitorDateUtils.format(endTime))
                .append("时段内")
                .append("【")
                .append(bizType.getDesc())
                .append("】")
                .append("没有任务成功,").append("请及时处理!");
        return buffer.toString();
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
        buffer.append("您好,").append("saas-").append(diamondConfig.getMonitorEnvironment())
                .append("发生任务预警,在")
                .append(MonitorDateUtils.format(startTime))
                .append("--")
                .append(MonitorDateUtils.format(endTime))
                .append("时段内没有任务成功,").append("请及时处理!");
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
        buffer.append("您好,").append("saas-").append(diamondConfig.getMonitorEnvironment())
                .append("发生任务预警,在")
                .append(MonitorDateUtils.format(startTime))
                .append("--")
                .append(MonitorDateUtils.format(endTime))
                .append("时段内没有任务创建,").append("请及时处理!");
        return buffer.toString();
    }

    private String generateNoTaskTitle() {
        return "saas-" + diamondConfig.getMonitorEnvironment() + "无任务预警";
    }

    private String generateNoSuccessTaskTitle() {
        return "saas-" + diamondConfig.getMonitorEnvironment() + "无成功任务预警";
    }

}