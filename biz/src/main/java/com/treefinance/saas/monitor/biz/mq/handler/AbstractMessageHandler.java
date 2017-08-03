package com.treefinance.saas.monitor.biz.mq.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import static com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus.CONSUME_SUCCESS;

/**
 * 基于JSON的消息处理
 */
public abstract class AbstractMessageHandler<T> implements TagBaseMessageHandler {
    /**
     * logger
     */
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 消息体class
     */
    protected Class<T> messageClass;

    public AbstractMessageHandler() {
        Class c = getClass();
        Type t = c.getGenericSuperclass();
        if (t instanceof ParameterizedType) {
            Type[] p = ((ParameterizedType) t).getActualTypeArguments();
            this.messageClass = (Class<T>) p[0];
        }
    }

    @Override
    public void handleMessage(byte[] messageBody) {
        T message = convertMessage(messageBody);
        List<T> list = Lists.newArrayList(message);
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
     * 消息转为对象： 默认为JSON格式
     *
     * @param messageBody
     * @return
     */
    protected List<T> convertMessageList(byte[] messageBody) {
        String message = new String(messageBody);
        return JSON.parseArray(message, messageClass);
    }
}
