package com.treefinance.saas.monitor.biz.alarm.service.message;

import com.treefinance.saas.monitor.biz.alarm.model.AlarmMessage;
import com.treefinance.saas.monitor.common.enumeration.EAlarmChannel;
import com.treefinance.saas.monitor.dao.entity.SaasWorker;

import java.util.List;

/**
 * Created by yh-treefinance on 2018/7/30.
 */
public interface MessageSender {

    /**
     * 消息发送
     *
     * @param alarmMessage
     * @param recivers
     */
    void sendMessage(AlarmMessage alarmMessage, List<SaasWorker> recivers);


    /**
     * 获取当前消息通道
     * @return
     */
    default EAlarmChannel channel() {
        return this.getClass().getAnnotation(MsgChannel.class).value();
    }
}
