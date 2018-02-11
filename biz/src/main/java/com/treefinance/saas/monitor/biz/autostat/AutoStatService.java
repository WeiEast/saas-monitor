package com.treefinance.saas.monitor.biz.autostat;

import com.alibaba.fastjson.JSON;
import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.protocol.heartbeat.MessageModel;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.google.common.collect.Maps;
import com.treefinance.saas.monitor.biz.autostat.basicdata.filter.BasicDataFilterContext;
import com.treefinance.saas.monitor.biz.autostat.basicdata.listener.BasicDataMessageListener;
import com.treefinance.saas.monitor.biz.autostat.basicdata.service.BasicDataService;
import com.treefinance.saas.monitor.biz.autostat.elasticjob.ElasticSimpleJobService;
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
import java.util.stream.Collectors;

/**
 * Created by yh-treefinance on 2018/1/29.
 */
@Component
public class AutoStatService implements InitializingBean, SimpleJob {
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
     * elastic-job
     */
    @Autowired
    private ElasticSimpleJobService elasticSimpleJobService;

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
     * 启动模板
     *
     * @param statTemplate
     */
    protected void startTemplate(StatTemplate statTemplate) {
        long start = System.currentTimeMillis();
        try {
            statTemplateParser.parse(statTemplate);
        } catch (Exception e) {
            logger.info("parse template error : template={} ", JSON.toJSONString(statTemplate), e);
        } finally {
            logger.info("start stat template success cost {}ms : template={} ", System.currentTimeMillis() - start, JSON.toJSONString(statTemplate));
        }
    }

    /**
     * 停用模板
     *
     * @param statTemplate
     */
    protected void stopTemplate(StatTemplate statTemplate) {
        String jobName = statTemplate.getTemplateCode();
        if (elasticSimpleJobService.exists(jobName)) {
            elasticSimpleJobService.removeJob(jobName);
        }
        logger.info("stopTemplate : statTemplate={}", JSON.toJSONString(statTemplate));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
//        List<StatTemplate> activeTemplates = statTemplateService.queryActiveList();
//        activeTemplates.stream().map(StatTemplate::getBasicDataId).distinct().forEach(basicDataId -> initListener(basicDataId));
//        threadPoolTaskExecutor.execute(() -> activeTemplates.forEach(statTemplate -> startTemplate(statTemplate)));

        String autoJobName = "flush-stat-template";
        JobCoreConfiguration statCalculateJobConf = JobCoreConfiguration
                .newBuilder(autoJobName, "0 0/5 * * * ? ", 1)
                .failover(true)
                .description("统计模板刷新")
                .build();
        elasticSimpleJobService.createJob(this, statCalculateJobConf);
        elasticSimpleJobService.triggerJob(autoJobName);
    }


    @Override
    public void execute(ShardingContext shardingContext) {
        logger.info("auto flush stat template....");
        List<StatTemplate> statTemplates = statTemplateService.queryAll();
        // 1.激活状态的模板
        List<StatTemplate> activeTemplates = statTemplates.stream().filter(statTemplate -> Byte.valueOf("1").equals(statTemplate.getStatus())).collect(Collectors.toList());

        // 2.新增的基础数据源，自动增加数据监停
        List<Long> basicIds = activeTemplates.stream().map(StatTemplate::getBasicDataId).distinct().collect(Collectors.toList());
        basicIds.forEach(basicId -> {
            if (!consumerContext.containsKey(basicId)) {
                initListener(basicId);
            }
        });

        // # 3.无激活模板的，停止监听 TODO

        // # 4.未启用的模板，停止job
        statTemplates.stream().filter(statTemplate -> !Byte.valueOf("1").equals(statTemplate.getStatus()))
                .forEach(this::stopTemplate);

        // # 5.启用模板，启用job
        activeTemplates.forEach(this::startTemplate);

    }
}
