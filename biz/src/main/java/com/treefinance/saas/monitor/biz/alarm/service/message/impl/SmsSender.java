package com.treefinance.saas.monitor.biz.alarm.service.message.impl;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.monitor.biz.alarm.model.AlarmMessage;
import com.treefinance.saas.monitor.biz.alarm.service.message.MessageSender;
import com.treefinance.saas.monitor.biz.alarm.service.message.MsgChannel;
import com.treefinance.saas.monitor.biz.config.DiamondConfig;
import com.treefinance.saas.monitor.biz.service.SmsNotifyService;
import com.treefinance.saas.monitor.common.enumeration.EAlarmChannel;
import com.treefinance.saas.monitor.common.enumeration.ESwitch;
import com.treefinance.saas.monitor.dao.entity.SaasWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by yh-treefinance on 2018/7/31.
 */
@Component
@MsgChannel(EAlarmChannel.SMS)
public class SmsSender implements MessageSender {
    /**
     * logger
     */
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private SmsNotifyService smsNotifyService;
    @Autowired
    private DiamondConfig diamondConfig;

    @Override
    public void sendMessage(AlarmMessage alarmMessage, List<SaasWorker> recivers) {
        if (!ESwitch.isOn(diamondConfig.getMonitorAlarmWechatSwitch())) {
            logger.info("sms message sender closed : alarmMessage={}, recivers={}", JSON.toJSONString(alarmMessage), JSON.toJSONString(recivers));
            return;
        }
        List<String> tolist = recivers.stream()
                .filter(reciver -> !StringUtils.isEmpty(reciver.getMobile()))
                .map(SaasWorker::getMobile).collect(Collectors.toList());
        smsNotifyService.send(alarmMessage.getMessage(), tolist);
    }
}
