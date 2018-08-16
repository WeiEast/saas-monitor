package com.treefinance.saas.monitor.biz.alarm.service.message;

import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSON;
import com.treefinance.saas.monitor.biz.config.DiamondConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Created by yh-treefinance on 2018/7/30.
 */
@Component
public class ProducerHolder {

    private static final Logger logger = LoggerFactory.getLogger(ProducerHolder.class);

    private DefaultMQProducer producer;
    @Autowired
    private DiamondConfig diamondConfig;

    @PostConstruct
    public void init() throws MQClientException {
        producer = new DefaultMQProducer(diamondConfig.getMonitorAlarmGroupName());
        producer.setNamesrvAddr(diamondConfig.getNamesrvAddr());
        producer.setMaxMessageSize(1024 * 1024 * 2);
        producer.start();
        logger.info("messgae producer inited : config={}", JSON.toJSONString(diamondConfig));
    }

    @PreDestroy
    public void destroy() {
        producer.shutdown();
    }

    /**
     * 获取消息发送producer
     *
     * @return
     */
    public DefaultMQProducer get() {
        return producer;
    }
}
