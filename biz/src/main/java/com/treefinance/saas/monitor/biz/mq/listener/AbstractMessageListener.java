package com.treefinance.saas.monitor.biz.mq.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import static com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus.CONSUME_SUCCESS;

/**
 * 消息监听
 */
public abstract class AbstractMessageListener<T> implements MessageListenerConcurrently {
    /**
     * logger
     */
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 消息体class
     */
    private Class<T> messageClass;

    public AbstractMessageListener() {
        Class c = getClass();
        Type t = c.getGenericSuperclass();
        if (t instanceof ParameterizedType) {
            Type[] p = ((ParameterizedType) t).getActualTypeArguments();
            this.messageClass = (Class<T>) p[0];
        }
    }

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        MessageExt msg = null;
        T message = null;
        try {
            logger.info("收到消息==>{}", JSON.toJSONString(list));
            msg = list.get(0);
            message = convertMessage(msg.getBody());
            handleMessage(message);
        } catch (Throwable cause) {
            if (msg.getReconsumeTimes() > 0) {
                logger.error(String.format("丢弃消息,因为重试一次之后, 处理消息仍然出错.body=%s", message), cause);
                return CONSUME_SUCCESS;
            } else {
                logger.error(String.format("消费消息时出错, 即将再重试一次body=%s", message), cause);
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
        }
        return CONSUME_SUCCESS;
    }

    /**
     * 消息转为对象： 默认为JSON格式
     *
     * @param messageBody
     * @return
     */
    protected T convertMessage(byte[] messageBody) {
        String message = new String(messageBody);
        return JSON.parseObject(message, messageClass);
    }


    /**
     * 消息处理
     *
     * @param message
     */
    public abstract void handleMessage(T message);
}
