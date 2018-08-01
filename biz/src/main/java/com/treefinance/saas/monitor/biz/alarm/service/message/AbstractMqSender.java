package com.treefinance.saas.monitor.biz.alarm.service.message;

import com.alibaba.fastjson.JSON;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.client.producer.SendStatus;
import com.alibaba.rocketmq.common.message.Message;
import com.datatrees.notify.async.util.BeanUtil;
import com.treefinance.saas.monitor.biz.alarm.model.AlarmMessage;
import com.treefinance.saas.monitor.biz.alarm.service.message.MessageSender;
import com.treefinance.saas.monitor.biz.alarm.service.message.ProducerHolder;
import com.treefinance.saas.monitor.dao.entity.SaasWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

/**
 * Created by yh-treefinance on 2018/7/30.
 */
public abstract class AbstractMqSender implements MessageSender, InitializingBean {
    /**
     * logger
     */
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ProducerHolder producerHolder;

    @Override
    public void sendMessage(AlarmMessage alaramMessage, List<SaasWorker> recivers) {
        String key = generateKey(alaramMessage, recivers);
        Object body = generateBody(alaramMessage, recivers);
        doSend(key, BeanUtil.objectToByte(body));
        logger.info("send message to mq server: topic={},tag={}, key={}, message-body={}", topic(), tag(), key, JSON.toJSONString(body));
    }

    /**
     * 消息发送topic
     *
     * @return
     */
    public abstract String topic();

    /**
     * 消息发送tag
     *
     * @return
     */
    public abstract String tag();

    /**
     * 生成消息结构体
     *
     * @param alarmMessage
     * @param recivers
     * @return
     */
    public abstract Object generateBody(AlarmMessage alarmMessage, List<SaasWorker> recivers);

    /**
     * 生成消息key
     *
     * @param alarmMessage
     * @param recivers
     * @return
     */
    String generateKey(AlarmMessage alarmMessage, List<SaasWorker> recivers) {
        String key = UUID.randomUUID().toString() + "_" + tag();
        return key;
    }

    /**
     * 发送消息
     *
     * @param key
     * @param body
     */
    protected void doSend(String key, byte[] body) {
        try {
            SendResult sendResult = producerHolder.get().send(new Message(topic(), tag(), key, body));
            if (logger.isInfoEnabled()) {
                logger.info("已发送消息[topic={},tag={},key={},body={},发送状态={}]", topic(), tag(), key, body, JSON.toJSONString(sendResult));
            }
            if (sendResult.getSendStatus() != SendStatus.SEND_OK) {
                logger.error(String.format("发送MQ消息[topic=%s,tag=%s,key=%s,body=%s]到消息中间件失败,发送状态为%s", topic(), tag(), key, body, sendResult.getSendStatus()));
            }
        } catch (Exception e) {
            logger.error(String.format("发送MQ消息[topic=%s,tag=%s,key=%s,body=%s]到消息中间件失败", topic(), tag(), key, body), e);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
    }
}
