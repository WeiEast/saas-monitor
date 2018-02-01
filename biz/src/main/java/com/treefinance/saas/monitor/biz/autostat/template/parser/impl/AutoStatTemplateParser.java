package com.treefinance.saas.monitor.biz.autostat.template.parser.impl;

import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.treefinance.saas.monitor.biz.autostat.basicdata.filter.BasicDataFilterContext;
import com.treefinance.saas.monitor.biz.autostat.elasticjob.ElasticSimpleJobService;
import com.treefinance.saas.monitor.biz.autostat.template.calc.calculator.DefaultStatDataCalculator;
import com.treefinance.saas.monitor.biz.autostat.template.job.StatCalculateJob;
import com.treefinance.saas.monitor.biz.autostat.template.job.StatDataFlushJob;
import com.treefinance.saas.monitor.biz.autostat.template.parser.StatTemplateParser;
import com.treefinance.saas.monitor.biz.autostat.template.service.StatGroupService;
import com.treefinance.saas.monitor.biz.autostat.template.service.StatItemService;
import com.treefinance.saas.monitor.biz.autostat.template.service.StatTemplateService;
import com.treefinance.saas.monitor.dao.entity.StatGroup;
import com.treefinance.saas.monitor.dao.entity.StatItem;
import com.treefinance.saas.monitor.dao.entity.StatTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;

/**
 * Created by yh-treefinance on 2018/1/23.
 */
@Component
public class AutoStatTemplateParser implements StatTemplateParser {
    @Autowired
    BasicDataFilterContext basicDataFilterContext;
    @Autowired
    StatTemplateService statTemplateService;
    @Autowired
    StatGroupService statGroupService;
    @Autowired
    StatItemService statItemService;
    @Autowired
    ElasticSimpleJobService elasticSimpleJobService;
    @Autowired
    DefaultStatDataCalculator statDataSpelCalculator;


    @Override
    public void parse(StatTemplate statTemplate) {
        Assert.notNull(statTemplate, "统计模板不能为空");
        Assert.notNull(statTemplate.getTemplateCode(), "统计模板编码不能为空");

        // 1.获取模板信息
        String templateCode = statTemplate.getTemplateCode();
        statTemplate = statTemplateService.queryByCode(templateCode);

        Assert.notNull(statTemplate.getStatCron(), "统计cron不能为空");
        Assert.isTrue(Byte.valueOf("1").equals(statTemplate.getStatus()), "统计模板必须先启用");

        Long templateId = statTemplate.getId();
        List<StatGroup> statGroups = statGroupService.get(templateId);
        List<StatItem> statItems = statItemService.get(templateId);

        // 2.注册过滤器（数据计算器）
        StatCalculateJob statCalculateJob = new StatCalculateJob(statTemplate, statDataSpelCalculator);
        basicDataFilterContext.registerFilter(statTemplate, statCalculateJob);

        // 3.生成数据计算任务
        JobCoreConfiguration statCalculateJobConf = JobCoreConfiguration
                .newBuilder(templateCode + "-DataCalc", statTemplate.getStatCron(), 1)
                .failover(true)
                .description(statTemplate.getTemplateName())
                .build();
        elasticSimpleJobService.createJob(statCalculateJob, statCalculateJobConf);

        // 4.数据刷新定时任务
        StatDataFlushJob statDataFlushJob = new StatDataFlushJob(statTemplate, statDataSpelCalculator);
        JobCoreConfiguration statDataFlushJobConf = JobCoreConfiguration
                .newBuilder(templateCode + "-DataFlush", statTemplate.getStatCron(), 1)
                .failover(true)
                .description(statTemplate.getTemplateName())
                .build();
        elasticSimpleJobService.createJob(statDataFlushJob, statDataFlushJobConf);
    }
}
