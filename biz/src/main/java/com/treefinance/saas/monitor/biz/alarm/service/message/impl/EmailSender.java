package com.treefinance.saas.monitor.biz.alarm.service.message.impl;

import com.alibaba.fastjson.JSON;
import com.datatrees.notify.async.body.mail.MailBody;
import com.datatrees.notify.async.body.mail.MailEnum;
import com.treefinance.saas.monitor.biz.alarm.model.AlarmMessage;
import com.treefinance.saas.monitor.biz.alarm.service.message.MsgChannel;
import com.treefinance.saas.monitor.biz.alarm.service.message.AbstractMqSender;
import com.treefinance.saas.monitor.biz.config.DiamondConfig;
import com.treefinance.saas.monitor.common.enumeration.EAlarmChannel;
import com.treefinance.saas.monitor.common.enumeration.ESwitch;
import com.treefinance.saas.monitor.dao.entity.SaasWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by yh-treefinance on 2018/7/30.
 */
@Component
@MsgChannel(EAlarmChannel.EMAIL)
public class EmailSender extends AbstractMqSender {
    @Autowired
    private DiamondConfig diamondConfig;

    @Override
    public void sendMessage(AlarmMessage alaramMessage, List<SaasWorker> recivers) {
        if (!ESwitch.isOn(diamondConfig.getMonitorAlarmMailSwitch())) {
            logger.info("mail message sender closed : alaramMessage={}, recivers={}", JSON.toJSONString(alaramMessage), JSON.toJSONString(recivers));
            return;
        }
        super.sendMessage(alaramMessage, recivers);
    }

    @Override
    public Object generateBody(AlarmMessage alarmMessage, List<SaasWorker> recivers) {
        List<String> tolist = recivers.stream()
                .filter(reciver -> !StringUtils.isEmpty(reciver.getEmail()))
                .map(SaasWorker::getEmail).collect(Collectors.toList());

        MailBody body = new MailBody();
        //设置邮件方式，具体看枚举值
        switch (alarmMessage.getMessageType()) {
            case HTML:
                body.setMailEnum(MailEnum.HTML_MAIL);
                body.setBusiness("alarm-html");
                break;
            case TEXT:
                body.setMailEnum(MailEnum.SIMPLE_MAIL);
                body.setBusiness("alarm-text");
                break;
        }
        //设置业务线，预警设置为alarm
        //设置发送给谁
        body.setToList(tolist);
        body.setSubject(alarmMessage.getTitle());
        body.setBody(alarmMessage.getMessage());
        return body;
    }

    @Override
    public String topic() {
        return diamondConfig.getMonitorAlarmTopic();
    }

    @Override
    public String tag() {
        return diamondConfig.getMonitorAlarmMailTag();
    }
}
