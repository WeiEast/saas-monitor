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
import com.treefinance.saas.monitor.common.domain.dto.StatTemplateDTO;
import com.treefinance.saas.monitor.context.component.AbstractService;
import com.treefinance.saas.monitor.dao.entity.BasicData;
import com.treefinance.saas.monitor.dao.entity.StatTemplate;
import com.treefinance.saas.monitor.dao.mapper.AsBasicDataHistoryMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
public class AutoStatService extends AbstractService implements InitializingBean, SimpleJob, ApplicationContextAware {
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

    @Autowired
    private AsBasicDataHistoryMapper asBasicDataMapper;

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
        if (basicData == null) {
            logger.info("initListener failed : not exists basicDataId={}", basicDataId);
            return;
        }
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
                consumer.registerMessageListener(new BasicDataMessageListener(basicDataId, basicDataCode, basicDataFilterContext, asBasicDataMapper));
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
        //数据库中加载统计模板任务.
        startTemplates(false);

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
            startTemplates(true);
        } catch (Exception e) {
            logger.error("auto flush stat template error :", e);
        }

    }

    private void startTemplates(boolean cached) {
        List<StatTemplate> statTemplates;
        if (cached) {
            statTemplates = getNeedUpdateStatTemplates();
        } else {
            statTemplates = statTemplateService.queryAll();
        }
        if (CollectionUtils.isEmpty(statTemplates)) {
            logger.info("auto flush stat template : 不需要刷新模板任务");
            return;
        }
        logger.info("auto flush stat template : 需要刷新的模板任务cached={},statTemplates={}", cached, JSON.toJSONString(statTemplates));

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
    }

    public List<StatTemplate> getNeedUpdateStatTemplates() {
        //刷新模板任务有问题,先将模板缓存,未发生变化的模板无需刷新
        List<StatTemplate> statTemplates = statTemplateService.queryAll();
        List<StatTemplateDTO> statTemplateDTOs = convert(statTemplates, StatTemplateDTO.class);

        String templateKey = AsConstants.REDIS_AUTO_STAT_TEMPLATE_KEY;
        String statTemplateStr = redisTemplate.opsForValue().get(templateKey);

        List<StatTemplateDTO> changedStatTemplateDTOs = Lists.newArrayList();
        List<StatTemplate> result = Lists.newArrayList();
        if (StringUtils.isBlank(statTemplateStr)) {
            redisTemplate.opsForValue().set(templateKey, JSON.toJSONString(statTemplateDTOs));
            return result;

        }
        List<StatTemplateDTO> oldStatTemplates = JSON.parseArray(statTemplateStr, StatTemplateDTO.class);
        Map<String, StatTemplateDTO> oldStatTemplateMap = oldStatTemplates.stream()
                .collect(Collectors.toMap(StatTemplateDTO::getTemplateCode, template -> template));

        for (StatTemplateDTO statTemplate : statTemplateDTOs) {
            StatTemplateDTO oldStatTemplate = oldStatTemplateMap.get(statTemplate.getTemplateCode());
            if (!statTemplate.equals(oldStatTemplate)) {
                changedStatTemplateDTOs.add(statTemplate);
            }
        }
        if (CollectionUtils.isNotEmpty(changedStatTemplateDTOs)) {
            redisTemplate.opsForValue().set(templateKey, JSON.toJSONString(statTemplateDTOs));
            result = convert(changedStatTemplateDTOs, StatTemplate.class);
        }
        return result;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
