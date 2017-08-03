package com.treefinance.saas.monitor.biz.mq.listener;

import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.treefinance.saas.monitor.biz.mq.handler.TagBaseMessageHandler;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus.CONSUME_SUCCESS;

/**
 * Created by yh-treefinance on 2017/6/6.
 */
@Component
public class TagBaseMonitorMessageListener implements MessageListenerConcurrently {

    /**
     * logger
     */
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private List<TagBaseMessageHandler> tagHandlers;


    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        if (CollectionUtils.isEmpty(list)) {
            logger.info("收到消息无效==>{}", list);
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
        MessageExt msg = null;
        try {
            logger.info("收到消息==>{}", list);
            for (MessageExt messageExt : list) {
                msg = messageExt;
                for (TagBaseMessageHandler handler : tagHandlers) {
                    if (handler.isHandleAble(messageExt)) {
                        handler.handleMessage(messageExt.getBody());
                    }
                }
            }
        } catch (Throwable cause) {
            if (msg == null) {
                logger.error(String.format("消费消息时出错, 即将再重试一次body=%s", msg), cause);
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            } else if (msg.getReconsumeTimes() > 0) {
                logger.error(String.format("丢弃消息,因为重试一次之后, 处理消息仍然出错.body=%s", msg), cause);
                return CONSUME_SUCCESS;
            } else {
                logger.error(String.format("消费消息时出错, 即将再重试一次body=%s", msg), cause);
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
        }
        return CONSUME_SUCCESS;
    }

}
