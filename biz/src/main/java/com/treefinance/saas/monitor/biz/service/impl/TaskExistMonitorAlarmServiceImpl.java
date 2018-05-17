package com.treefinance.saas.monitor.biz.service.impl;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.monitor.biz.config.DiamondConfig;
import com.treefinance.saas.monitor.biz.mq.producer.AlarmMessageProducer;
import com.treefinance.saas.monitor.biz.service.IvrNotifyService;
import com.treefinance.saas.monitor.biz.service.SmsNotifyService;
import com.treefinance.saas.monitor.biz.service.TaskExistMonitorAlarmService;
import com.treefinance.saas.monitor.common.domain.dto.TaskExistAlarmNoSuccessTaskConfigDTO;
import com.treefinance.saas.monitor.common.domain.dto.TaskExistAlarmNoTaskConfigDTO;
import com.treefinance.saas.monitor.common.enumeration.EAlarmLevel;
import com.treefinance.saas.monitor.common.enumeration.EAlarmType;
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
    private IvrNotifyService ivrNotifyService;
    @Autowired
    private SmsNotifyService smsNotifyService;
    @Autowired
    private DiamondConfig diamondConfig;


    @Override
    public void alarmNoSuccessTaskWithConfig(Date startTime, Date endTime, TaskExistAlarmNoSuccessTaskConfigDTO config) {
        logger.info("无成功任务预警,config={}", JSON.toJSONString(config));
        if (!StringUtils.equalsIgnoreCase(config.getAlarmSwitch(), "on")) {
            logger.info("无成功任务预警,预警总开关已关闭");
            return;
        }
        EBizType bizType = EBizType.getBizType(config.getBizType());
        if (StringUtils.equalsIgnoreCase(config.getMailAlarmSwitch(), "on")) {
            String mailDataBody = generateNoSuccessTaskWithTypeMailDataBody(startTime, endTime, config);
            String title = diamondConfig.getSaasMonitorEnvironment() + "【" + config.getSaasEnvDesc() + "】" + "【" + config.getBizTypeDesc() + "】" + "无成功任务预警";
            alarmMessageProducer.sendMail4TaskExistMonitor(title, mailDataBody);
        } else {
            logger.info("无成功任务预警,发送邮件开关已关闭");
        }

        if (StringUtils.equalsIgnoreCase(config.getWeChatAlarmSwitch(), "on")) {
            String weChatBody = generateNoSuccessWithTypeWeChatBody(startTime, endTime, config);
            alarmMessageProducer.sendWebChart4TaskExistMonitor(weChatBody);
        } else {
            logger.info("无成功任务预警,发送微信开关已关闭");
        }
        // 增加ivr服务通知
        if (EBizType.OPERATOR.equals(bizType)) {
            ivrNotifyService.notifyIvr(EAlarmLevel.error, EAlarmType.no_success_task, "运营商无成功任务", config.getSaasEnvDesc());
        }
    }

    @Override
    public void alarmNoTaskWithConfig(Date startTime, Date endTime, TaskExistAlarmNoTaskConfigDTO config) {
        logger.info("无任务预警,config={}", JSON.toJSONString(config));
        if (!StringUtils.equalsIgnoreCase(config.getAlarmSwitch(), "on")) {
            logger.info("无任务预警,预警总开关已关闭");
            return;
        }

        if (StringUtils.equalsIgnoreCase(config.getMailAlarmSwitch(), "on")) {
            String mailDataBody = generateNoTaskMailDataBody(startTime, endTime, config);
            String title = diamondConfig.getSaasMonitorEnvironment() + "【" + config.getSaasEnvDesc() + "】" + "【" + config.getBizTypeDesc() + "】" + "无任务预警";
            alarmMessageProducer.sendMail4TaskExistMonitor(title, mailDataBody);
        } else {
            logger.info("无任务预警,发送邮件开关已关闭");
        }

        if (StringUtils.equalsIgnoreCase(config.getWeChatAlarmSwitch(), "on")) {
            String weChatBody = generateNoTaskWeChatBody(startTime, endTime, config);
            alarmMessageProducer.sendWebChart4TaskExistMonitor(weChatBody);
        } else {
            logger.info("无任务预警,发送微信开关已关闭");
        }

        if (StringUtils.equalsIgnoreCase(config.getSmsAlarmSwitch(), "on")) {
            String smsBody = generateNoTaskSmsBody(startTime, endTime, config);
            smsNotifyService.send(smsBody);
        } else {
            logger.info("无任务预警,发送短信开关已关闭");
        }

        // 增加ivr服务通知
        ivrNotifyService.notifyIvr(EAlarmLevel.error, EAlarmType.no_task, "大盘无任务", config.getSaasEnvDesc());
    }

    private String generateNoSuccessWithTypeWeChatBody(Date startTime, Date endTime, TaskExistAlarmNoSuccessTaskConfigDTO config) {
        StringBuffer buffer = new StringBuffer();
        if (EBizType.OPERATOR.getCode().equals(config.getBizType()) || Byte.valueOf("0").equals(config.getBizType())) {
            buffer.append("【").append(EAlarmLevel.error).append("】");
        } else {
            buffer.append("【").append(EAlarmLevel.warning).append("】");
        }
        buffer.append("您好,").append(diamondConfig.getSaasMonitorEnvironment())
                .append("【").append(config.getSaasEnvDesc()).append("】")
                .append("发生任务预警,在")
                .append(MonitorDateUtils.format(startTime))
                .append("--")
                .append(MonitorDateUtils.format(endTime))
                .append("时段内")
                .append("【")
                .append(config.getBizTypeDesc())
                .append("】")
                .append("没有任务成功,").append("请及时处理!");
        return buffer.toString();
    }


    private String generateNoSuccessTaskWithTypeMailDataBody(Date startTime, Date endTime, TaskExistAlarmNoSuccessTaskConfigDTO config) {

        StringBuffer buffer = new StringBuffer();
        if (EBizType.OPERATOR.getCode().equals(config.getBizType()) || Byte.valueOf("0").equals(config.getBizType())) {
            buffer.append("【").append(EAlarmLevel.error).append("】");
        } else {
            buffer.append("【").append(EAlarmLevel.warning).append("】");
        }
        buffer.append("您好,").append(diamondConfig.getSaasMonitorEnvironment())
                .append("【").append(config.getSaasEnvDesc()).append("】")
                .append("发生任务预警,在")
                .append(MonitorDateUtils.format(startTime))
                .append("--")
                .append(MonitorDateUtils.format(endTime))
                .append("时段内")
                .append("【")
                .append(config.getBizTypeDesc())
                .append("】")
                .append("没有任务成功,").append("请及时处理!");
        return buffer.toString();
    }


    private String generateNoTaskWeChatBody(Date startTime, Date endTime, TaskExistAlarmNoTaskConfigDTO config) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("【").append(EAlarmLevel.error).append("】");
        buffer.append("您好,").append(diamondConfig.getSaasMonitorEnvironment())
                .append("【").append(config.getSaasEnvDesc()).append("】")
                .append("发生任务预警,在")
                .append(MonitorDateUtils.format(startTime))
                .append("--")
                .append(MonitorDateUtils.format(endTime))
                .append("时段内")
                .append("【")
                .append(config.getBizTypeDesc())
                .append("】")
                .append("没有任务创建,").append("请及时处理!").append("\n");
        return buffer.toString();
    }

    private String generateNoTaskSmsBody(Date startTime, Date endTime, TaskExistAlarmNoTaskConfigDTO config) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(EAlarmLevel.error).append(",");
        buffer.append("您好,").append(diamondConfig.getSaasMonitorEnvironment())
                .append("【").append(config.getSaasEnvDesc()).append("】")
                .append("发生任务预警,在")
                .append(MonitorDateUtils.format(startTime))
                .append("--")
                .append(MonitorDateUtils.format(endTime))
                .append("时段内")
                .append("【")
                .append(config.getBizTypeDesc())
                .append("】")
                .append("没有任务创建,").append("请及时处理!").append("\n");
        return buffer.toString();
    }


    private String generateNoTaskMailDataBody(Date startTime, Date endTime, TaskExistAlarmNoTaskConfigDTO config) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("【").append(EAlarmLevel.error).append("】");
        buffer.append("您好,").append(diamondConfig.getSaasMonitorEnvironment())
                .append("【").append(config.getSaasEnvDesc()).append("】")
                .append("发生任务预警,在")
                .append(MonitorDateUtils.format(startTime))
                .append("--")
                .append(MonitorDateUtils.format(endTime))
                .append("时段内")
                .append("【")
                .append(config.getBizTypeDesc())
                .append("】")
                .append("没有任务创建,").append("请及时处理!");
        return buffer.toString();
    }


}
