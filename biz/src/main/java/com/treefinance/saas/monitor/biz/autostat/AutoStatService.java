package com.treefinance.saas.monitor.biz.autostat;

import com.alibaba.fastjson.JSON;
import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.protocol.heartbeat.MessageModel;
import com.google.common.collect.Maps;
import com.treefinance.saas.monitor.biz.autostat.basicdata.filter.BasicDataFilterContext;
import com.treefinance.saas.monitor.biz.autostat.basicdata.listener.BasicDataMessageListener;
import com.treefinance.saas.monitor.biz.autostat.basicdata.service.BasicDataService;
import com.treefinance.saas.monitor.biz.autostat.template.parser.StatTemplateParser;
import com.treefinance.saas.monitor.biz.autostat.template.service.StatTemplateService;
import com.treefinance.saas.monitor.dao.entity.BasicData;
import com.treefinance.saas.monitor.dao.entity.StatTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * Created by yh-treefinance on 2018/1/29.
 */
@Component
public class AutoStatService implements InitializingBean {
    /**
     * logger
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private StatTemplateService statTemplateService;
    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Autowired
    private StatTemplateParser statTemplateParser;
    @Autowired
    private BasicDataFilterContext basicDataFilterContext;
    @Autowired
    private BasicDataService basicDataService;


    /**
     * 已经启动的监听器
     */
    private Map<Long, DefaultMQPushConsumer> consumerContext = Maps.newConcurrentMap();

    /**
     * 启动基础数据监听器
     *
     * @param basicDataId
     */
    public void initListener(Long basicDataId) {
        if (consumerContext.containsKey(basicDataId)) {
            return;
        }
        BasicData basicData = basicDataService.queryById(basicDataId);
        String basicDataCode = basicData.getDataCode();
        synchronized (this) {
            Map<String, Object> configMap = JSON.parseObject(basicData.getDataSourceConfigJson());
            String topic = (String) configMap.get("topic");
            String tag = (String) configMap.get("tag");
            String namesrvAddr = (String) configMap.get("namesrvAddr");
            String group = (String) configMap.get("group");

            DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(group);
            try {
                consumer.setNamesrvAddr(namesrvAddr);
                consumer.subscribe(topic, tag);
                consumer.setMessageModel(MessageModel.CLUSTERING);
                consumer.registerMessageListener(new BasicDataMessageListener(basicDataId, basicDataCode, basicDataFilterContext));
                consumer.start();
            } catch (MQClientException e) {
                throw new RuntimeException("start " + basicDataCode + " consumer error", e);
            }
            consumerContext.put(basicDataId, consumer);
        }
        logger.info("start basic data listener : {}", JSON.toJSONString(basicData));
    }

    /**
     * 加载模板
     *
     * @param statTemplate
     */
    protected void loadTemplate(StatTemplate statTemplate) {
        long start = System.currentTimeMillis();
        try {
            statTemplateParser.parse(statTemplate);
        } catch (Exception e) {
            logger.info("parse template error : template={} ", JSON.toJSONString(statTemplate), e);
        } finally {
            logger.info("parse template success cost {}ms : template={} ", System.currentTimeMillis() - start, JSON.toJSONString(statTemplate));
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        List<StatTemplate> activeTemplates = statTemplateService.queryActiveList();
        activeTemplates.stream().map(StatTemplate::getBasicDataId).distinct().forEach(basicDataId -> initListener(basicDataId));
        threadPoolTaskExecutor.execute(() -> activeTemplates.forEach(statTemplate -> loadTemplate(statTemplate)));
    }

}
