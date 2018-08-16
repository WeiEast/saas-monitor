package com.treefinance.saas.monitor.biz.alarm.service.handler.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.treefinance.saas.monitor.biz.alarm.model.AlarmConfig;
import com.treefinance.saas.monitor.biz.alarm.model.AlarmContext;
import com.treefinance.saas.monitor.biz.alarm.model.AlarmMessage;
import com.treefinance.saas.monitor.biz.alarm.service.handler.AlarmHandler;
import com.treefinance.saas.monitor.biz.alarm.service.handler.Order;
import com.treefinance.saas.monitor.biz.alarm.service.message.MessageSender;
import com.treefinance.saas.monitor.biz.service.SaasWorkerService;
import com.treefinance.saas.monitor.common.enumeration.EAlarmChannel;
import com.treefinance.saas.monitor.common.enumeration.EAlarmLevel;
import com.treefinance.saas.monitor.common.enumeration.ESwitch;
import com.treefinance.saas.monitor.dao.entity.AsAlarmNotify;
import com.treefinance.saas.monitor.dao.entity.SaasWorker;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by yh-treefinance on 2018/7/30.
 */
@Order(5)
@Component
public class MessageHandler implements AlarmHandler {
    /**
     * logger
     */
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private List<MessageSender> messageSenderList;
    @Autowired
    private SaasWorkerService saasWorkerService;

    @Override
    public void handle(AlarmConfig config, AlarmContext context) {
        // 1.预警消息列表
        List<AlarmMessage> alarmMessages = context.getAlaramMessageList();
        if (CollectionUtils.isEmpty(alarmMessages)) {
            logger.info("alarm message handler: message is empty , config={}", JSON.toJSONString(config));
            return;
        }
        // 2.预警通知
        List<AsAlarmNotify> notifies = config.getAlarmNotifies();
        if (CollectionUtils.isEmpty(notifies)) {
            logger.info("alarm message handler: notify-config is empty , config={}", JSON.toJSONString(config));
            return;
        }
        Map<EAlarmLevel, AsAlarmNotify> notifyMap = notifies.stream()
                .collect(Collectors.toMap(notify -> EAlarmLevel.getLevel(notify.getAlarmLevel()), notify -> notify));
        // 预警接受人
        Map<EAlarmLevel, List<SaasWorker>> reciverMap = mappingReciver(notifyMap, context.getAlarmTime());
        // 消息发送器
        Map<EAlarmLevel, List<MessageSender>> senderMap = mappingSender(notifyMap);
        // 发送消息
        for (AlarmMessage alarmMessage : alarmMessages) {
            EAlarmLevel alarmLevel = alarmMessage.getAlarmLevel();
            List<SaasWorker> recivers = reciverMap.get(alarmLevel);
            List<MessageSender> senders = senderMap.get(alarmLevel);
            if (CollectionUtils.isEmpty(senders)) {
                logger.info("alarm message handler:  sender-config is empty, alarmlevel={}, message={}, config={}", alarmLevel, JSON.toJSONString(alarmMessage), JSON.toJSONString(config));
                continue;
            }
            senders.forEach(messageSender -> {
                try {
                    messageSender.sendMessage(alarmMessage, recivers);
                    logger.info("alarm message handler:  send {}-message success, alarmlevel={}, message={}, config={}",
                            messageSender.channel(), alarmLevel, JSON.toJSONString(alarmMessage), JSON.toJSONString(config));
                } catch (Exception e) {
                    logger.info("alarm message handler:  send {}-message exception, alarmlevel={}, message={}, config={}",
                            messageSender.channel(), alarmLevel, JSON.toJSONString(alarmMessage), JSON.toJSONString(config), e);
                }
            });

        }
    }

    /**
     * 映射<预警级别,消息发送器>
     *
     * @param notifyMap
     * @return
     */
    private Map<EAlarmLevel, List<MessageSender>> mappingSender(Map<EAlarmLevel, AsAlarmNotify> notifyMap) {
        // 消息发送器
        Map<EAlarmLevel, List<MessageSender>> senderMap = Maps.newHashMap();
        Map<EAlarmChannel, MessageSender> channelMap = messageSenderList.stream()
                .collect(Collectors.toMap(MessageSender::channel, messageSender -> messageSender));
        // 通知人员类型
        for (EAlarmLevel alarmLevel : notifyMap.keySet()) {
            AsAlarmNotify notify = notifyMap.get(alarmLevel);
            List<MessageSender> senders = Lists.newArrayList();
            if (ESwitch.isOn(notify.getEmailSwitch())) {
                senders.add(channelMap.get(EAlarmChannel.EMAIL));
            }
            if (ESwitch.isOn(notify.getSmsSwitch())) {
                senders.add(channelMap.get(EAlarmChannel.SMS));
            }
            if (ESwitch.isOn(notify.getWechatSwitch())) {
                senders.add(channelMap.get(EAlarmChannel.WECHAT));
            }
            if (ESwitch.isOn(notify.getIvrSwitch())) {
                senders.add(channelMap.get(EAlarmChannel.IVR));
            }
            senderMap.put(alarmLevel, senders);
        }
        return senderMap;
    }

    /**
     * 映射<预警级别,消息接受人>
     *
     * @param notifyMap
     * @param alarmTime
     * @return
     */
    private Map<EAlarmLevel, List<SaasWorker>> mappingReciver(Map<EAlarmLevel, AsAlarmNotify> notifyMap, Date alarmTime) {
        // 全部值班人员
        List<SaasWorker> saasWorkers = saasWorkerService.getAllSaasWorker();
        // 当前值班人员
        List<SaasWorker> activeWorkers = saasWorkerService.getActiveWorkers(alarmTime, saasWorkers);
        Map<EAlarmLevel, List<SaasWorker>> reciverMap = Maps.newHashMap();
        // 通知人员类型
        for (EAlarmLevel alarmLevel : notifyMap.keySet()) {
            AsAlarmNotify notify = notifyMap.get(alarmLevel);
            switch (notify.getReceiverType()) {
                //  0：全部
                case 0:
                    reciverMap.put(alarmLevel, Lists.newArrayList(saasWorkers));
                    break;
                // 1：值班人员
                case 1:
                    reciverMap.put(alarmLevel, Lists.newArrayList(activeWorkers));
                    break;
                //2：自定义
                case 2:
                    List<String> customReciverIds = Splitter.on(",").splitToList(notify.getReceiverIds().trim());
                    List<SaasWorker> customRecivers = saasWorkers.stream()
                            .filter(saasWorker -> customReciverIds.contains(saasWorker.getId().toString()))
                            .collect(Collectors.toList());
                    reciverMap.put(alarmLevel, customRecivers);
                    break;
            }
        }
        return reciverMap;
    }
}
