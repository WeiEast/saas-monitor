package com.treefinance.saas.monitor.biz.alarm.service.message.impl;

import com.alibaba.fastjson.JSON;
import com.datatrees.notify.async.body.NotifyEnum;
import com.datatrees.notify.async.body.mail.MailBody;
import com.datatrees.notify.async.body.mail.MailEnum;
import com.datatrees.notify.async.body.wechat.WeChatBody;
import com.datatrees.notify.async.body.wechat.WeChatEnum;
import com.datatrees.notify.async.body.wechat.message.TXTMessage;
import com.treefinance.saas.monitor.biz.alarm.model.AlarmMessage;
import com.treefinance.saas.monitor.biz.alarm.service.message.AbstractMqSender;
import com.treefinance.saas.monitor.biz.alarm.service.message.MsgChannel;
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
@MsgChannel(EAlarmChannel.WECHAT)
public class WechatSender extends AbstractMqSender {
    /**
     * 微信渠道
     */
    private static final String AGENT_ID = "67";

    @Autowired
    private DiamondConfig diamondConfig;

    @Override
    public void sendMessage(AlarmMessage alaramMessage, List<SaasWorker> recivers) {
        if (!ESwitch.isOn(diamondConfig.getMonitorAlarmWechatSwitch())) {
            logger.info("webchat message sender closed : alaramMessage={}, recivers={}", JSON.toJSONString(alaramMessage), JSON.toJSONString(recivers));
            return;
        }
        super.sendMessage(alaramMessage, recivers);
    }

    @Override
    public Object generateBody(AlarmMessage alarmMessage, List<SaasWorker> recivers) {
        WeChatBody body = new WeChatBody();
        body.setAgentId(AGENT_ID);
        TXTMessage msg = new TXTMessage();
        msg.setMessage(alarmMessage.getMessage());
        body.setMessage(msg);

        //设置邮件方式，具体看枚举值
        switch (alarmMessage.getMessageType()) {
            case HTML:
                body.setWeChatEnum(WeChatEnum.DASHU_AN_APP_TXPIC);
                break;
            case TEXT:
                body.setWeChatEnum(WeChatEnum.DASHU_AN_APP_TXT);
                break;
        }
        body.setNotifyEnum(NotifyEnum.WECHAT);
        return body;
    }

    @Override
    public String topic() {
        return diamondConfig.getMonitorAlarmTopic();
    }

    @Override
    public String tag() {
        return diamondConfig.getMonitorAlarmWebchartTag();
    }
}
