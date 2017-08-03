package com.treefinance.saas.monitor.biz.mq.handler;

import com.alibaba.rocketmq.common.message.MessageExt;

/**
 * 基于Tag标记的消息处理器
 * Created by yh-treefinance on 2017/7/13.
 */
public interface TagBaseMessageHandler {
    /**
     * 是否能够处理消息
     *
     * @param messageExt
     * @return
     */
    boolean isHandleAble(MessageExt messageExt);

    /**
     * 处理消息体
     *
     * @param messageBody
     */
    void handleMessage(byte[] messageBody);
}
