package com.treefinance.saas.monitor.biz.autostat;

import com.alibaba.fastjson.JSON;
import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.protocol.heartbeat.MessageModel;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.treefinance.saas.monitor.biz.autostat.basicdata.filter.BasicDataFilterContext;
import com.treefinance.saas.monitor.biz.autostat.basicdata.listener.BasicDataMessageListener;
import com.treefinance.saas.monitor.biz.autostat.basicdata.service.BasicDataService;
import com.treefinance.saas.monitor.biz.autostat.elasticjob.ElasticSimpleJobService;
import com.treefinance.saas.monitor.biz.autostat.model.AsConstants;
import com.treefinance.saas.monitor.biz.autostat.template.parser.StatTemplateParser;
import com.treefinance.saas.monitor.biz.autostat.template.service.StatTemplateService;
import com.treefinance.saas.monitor.biz.config.DiamondConfig;
import com.treefinance.saas.monitor.dao.entity.BasicData;
import com.treefinance.saas.monitor.dao.entity.StatTemplate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by yh-treefinance on 2018/1/29.
 */
@Component
public class AutoStatService implements InitializingBean, SimpleJob, ApplicationContextAware {
    /**
     * logger
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private StatTemplateService statTemplateService;
    @Autowired
    private StatTemplateParser statTemplateParser;
    @Autowired
    private BasicDataFilterContext basicDataFilterContext;
    @Autowired
    private BasicDataService basicDataService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    /**
     * elastic-job
     */
    @Autowired
    private ElasticSimpleJobService elasticSimpleJobService;

    @Autowired
    private DiamondConfig diamondConfig;

    /**
     * 已经启动的监听器
     */
    private Map<Long, DefaultMQPushConsumer> consumerContext = Maps.newConcurrentMap();
    /**
     * applicationContext
     */
    private ApplicationContext applicationContext;


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
            String namesrvAddr = diamondConfig.getNamesrvAddr();
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
        logger.info("auto load stat template job running...");
    }


    @Override
    public void execute(ShardingContext shardingContext) {
        try {

            List<StatTemplate> statTemplates = getNeedUpdateStatTemplates();
            if (CollectionUtils.isEmpty(statTemplates)) {
                logger.info("auto flush stat template : 不需要刷新模板任务");
                return;
            }
            logger.info("auto flush stat template : 需要刷新的模板任务statTemplates={}", JSON.toJSONString(statTemplates));

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
            activeTemplates.stream().forEach(statTemplate -> {
                logger.info("auto flush stat template : start {}", JSON.toJSONString(statTemplate));
                this.startTemplate(statTemplate);
            });
            logger.info("auto flush stat template : activeTemplates={}", JSON.toJSONString(activeTemplates));
        } catch (Exception e) {
            logger.error("auto flush stat template error :", e);
        }

    }

    private List<StatTemplate> getNeedUpdateStatTemplates() {
        //刷新模板任务有问题,先将模板缓存,未发生变化的模板无需刷新
        List<StatTemplate> statTemplates = statTemplateService.queryAll();
        String templateKey = AsConstants.REDIS_AUTO_STAT_TEMPLATE_KEY;
        String statTemplateStr = redisTemplate.opsForValue().get(templateKey);

        List<StatTemplate> changedStatTemplates = Lists.newArrayList();
        if (StringUtils.isBlank(statTemplateStr)) {
            changedStatTemplates.addAll(statTemplates);
        } else {
            List<StatTemplate> oldStatTemplates = JSON.parseArray(statTemplateStr, StatTemplate.class);
            Map<String, StatTemplate> oldStatTemplateMap = oldStatTemplates.stream()
                    .collect(Collectors.toMap(StatTemplate::getTemplateCode, template -> template));

            for (StatTemplate statTemplate : statTemplates) {
                StatTemplate oldStatTemplate = oldStatTemplateMap.get(statTemplate.getTemplateCode());
                if (oldStatTemplate == null || !statTemplate.equals(oldStatTemplate)) {
                    changedStatTemplates.add(statTemplate);
                }
            }
        }
        if (CollectionUtils.isNotEmpty(changedStatTemplates)) {
            redisTemplate.opsForValue().set(templateKey, JSON.toJSONString(statTemplates));
        }
        return changedStatTemplates;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
