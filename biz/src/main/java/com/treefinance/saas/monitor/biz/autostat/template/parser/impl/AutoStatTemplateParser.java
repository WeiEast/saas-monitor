package com.treefinance.saas.monitor.biz.autostat.template.parser.impl;

import com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSON;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.google.common.collect.Maps;
import com.treefinance.saas.monitor.biz.autostat.basicdata.filter.BasicDataFilterContext;
import com.treefinance.saas.monitor.biz.autostat.elasticjob.ElasticSimpleJobService;
import com.treefinance.saas.monitor.biz.autostat.template.calc.calculator.DefaultStatDataCalculator;
import com.treefinance.saas.monitor.biz.autostat.template.job.StatCalculateLocalJob;
import com.treefinance.saas.monitor.biz.autostat.template.job.StatDataFlushJob;
import com.treefinance.saas.monitor.biz.autostat.template.parser.StatTemplateParser;
import com.treefinance.saas.monitor.biz.autostat.template.service.StatTemplateService;
import com.treefinance.saas.monitor.dao.entity.StatTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * Created by yh-treefinance on 2018/1/23.
 */
@Component
public class AutoStatTemplateParser implements StatTemplateParser {
    /**
     * logger
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    BasicDataFilterContext basicDataFilterContext;
    @Autowired
    StatTemplateService statTemplateService;
    @Autowired
    ElasticSimpleJobService elasticSimpleJobService;
    @Autowired
    DefaultStatDataCalculator statDataSpelCalculator;

    /**
     * local job
     */
    private Map<String, StatCalculateLocalJob> statCalculateLocalJobs = Maps.newConcurrentMap();


    @Override
    public void parse(StatTemplate statTemplate) {
        logger.info("auto stat template parse: statTemplate={}", JSON.toJSONString(statTemplate));
        Assert.notNull(statTemplate, "统计模板不能为空");
        Assert.notNull(statTemplate.getTemplateCode(), "统计模板编码不能为空");

        // 1.获取模板信息
        String templateCode = statTemplate.getTemplateCode();
        statTemplate = statTemplateService.queryByCode(templateCode);

        Assert.notNull(statTemplate.getStatCron(), "统计cron不能为空");
        Assert.isTrue(Byte.valueOf("1").equals(statTemplate.getStatus()), "统计模板必须先启用");

        // 2.注册过滤器（数据计算器）
        StatCalculateLocalJob statCalculateJob = new StatCalculateLocalJob(statTemplate, statDataSpelCalculator);
        basicDataFilterContext.registerFilter(statTemplate, statCalculateJob);
        statCalculateLocalJobs.put(statTemplate.getTemplateCode(), statCalculateJob);

        // 3.生成数据计算任务
//        JobCoreConfiguration statCalculateJobConf = JobCoreConfiguration
//                .newBuilder(templateCode + "-DataCalc", statTemplate.getStatCron(), 1)
//                .failover(true)
//                .description(statTemplate.getTemplateName())
//                .build();
//        elasticSimpleJobService.createJob(statCalculateJob, statCalculateJobConf);

        // 4.数据刷新定时任务
        StatDataFlushJob statDataFlushJob = new StatDataFlushJob(statTemplate, statDataSpelCalculator);
        JobCoreConfiguration statDataFlushJobConf = JobCoreConfiguration
                .newBuilder(templateCode /*+ "-DataFlush"*/, statTemplate.getFlushDataCron(), 1)
                .failover(true)
                .description(statTemplate.getTemplateName())
                .build();
        elasticSimpleJobService.createJob(statDataFlushJob, statDataFlushJobConf);
    }


    @Scheduled(fixedRate = 60000)
    public void scheduledLocalJob() {
        // 每分钟定时执行
        statCalculateLocalJobs.values().forEach(statCalculateLocalJob -> statCalculateLocalJob.execute(null));
    }
}
