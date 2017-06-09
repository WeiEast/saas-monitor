package com.treefinance.saas.monitor.biz.mq.listener;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.treefinance.saas.monitor.biz.config.DiamondConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import static com.alibaba.rocketmq.common.protocol.heartbeat.MessageModel.CLUSTERING;

/**
 * Created by luoyihua on 2017/4/26.
 */
@Service
public class ListenerStarter {
    private static final Logger logger = LoggerFactory.getLogger(ListenerStarter.class);

    @Autowired
    private DiamondConfig diamondConfig;
    private DefaultMQPushConsumer monitorAccessConsumer;
    @Autowired
    private StatMessageListener gatewayAccessMessageListener;

    @PostConstruct
    public void init() throws MQClientException {
        initGatewayAccessMessageMQ();
    }

    @PreDestroy
    public void destroy() {
        monitorAccessConsumer.shutdown();
        logger.info("关闭监控数据的消费者");
    }

    /**
     * 初始化网关访问监控
     * @throws MQClientException
     */
    private void initGatewayAccessMessageMQ() throws MQClientException {
        monitorAccessConsumer = new DefaultMQPushConsumer(diamondConfig.getMonitorGroupName());
        monitorAccessConsumer.setInstanceName(diamondConfig.getMonitorGroupName());
        monitorAccessConsumer.setNamesrvAddr(diamondConfig.getNamesrvAddr());
        monitorAccessConsumer.subscribe(diamondConfig.getMonitorAccessTopic(), diamondConfig.getMonitorAccessTag());
        monitorAccessConsumer.setMessageModel(CLUSTERING);
        monitorAccessConsumer.registerMessageListener(gatewayAccessMessageListener);
        monitorAccessConsumer.start();
        logger.info("启动saas-gateway的消费者.nameserver:{},topic:{},tag:{}", diamondConfig.getNamesrvAddr(),
                diamondConfig.getMonitorAccessTopic(), diamondConfig.getMonitorAccessTag());
    }

}
