package com.treefinance.saas.monitor.biz.autostat.template.parser;

import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.treefinance.saas.monitor.biz.autostat.basicdata.filter.BasicDataFilterContext;
import com.treefinance.saas.monitor.biz.autostat.elasticjob.ElasticSimpleJobService;
import com.treefinance.saas.monitor.biz.autostat.template.calc.spel.SpelExpressionCalculator;
import com.treefinance.saas.monitor.biz.autostat.template.calc.spel.StatDataSpelCalculator;
import com.treefinance.saas.monitor.biz.autostat.template.job.StatCalculateJob;
import com.treefinance.saas.monitor.biz.autostat.template.service.StatGroupService;
import com.treefinance.saas.monitor.biz.autostat.template.service.StatItemService;
import com.treefinance.saas.monitor.biz.autostat.template.service.StatTemplateService;
import com.treefinance.saas.monitor.dao.entity.StatGroup;
import com.treefinance.saas.monitor.dao.entity.StatItem;
import com.treefinance.saas.monitor.dao.entity.StatTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.List;

/**
 * Created by yh-treefinance on 2018/1/23.
 */
public class AbstractStatTemplateParser implements StatTemplateParser {
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
    StatDataSpelCalculator statDataSpelCalculator;


    @Override
    public void parse(StatTemplate statTemplate) {
        Assert.notNull(statTemplate, "统计模板不能为空");
        Assert.notNull(statTemplate.getTemplateCode(), "统计模板编码不能为空");

        // 1.获取模板信息
        String templateCode = statTemplate.getTemplateCode();
        statTemplate = statTemplateService.getByCode(templateCode);

        Assert.notNull(statTemplate.getStatCron(), "统计cron不能为空");
        Assert.isTrue(!Byte.valueOf("1").equals(statTemplate.getStatus()), "统计模板必须先启用");

        Long templateId = statTemplate.getId();
        List<StatGroup> statGroups = statGroupService.getByTemplateId(templateId);
        List<StatItem> statItems = statItemService.getByTemplateId(templateId);

        // 2.注册过滤器（数据计算器）
        StatCalculateJob statCalculateJob = new StatCalculateJob(statTemplate, statGroups, statItems, statDataSpelCalculator);
        basicDataFilterContext.registerFilter(statTemplate, statCalculateJob);

        // 3.生成数据计算任务
        JobCoreConfiguration jobCoreConfiguration = JobCoreConfiguration
                .newBuilder(templateCode, statTemplate.getStatCron(), 1)
                .failover(true)
                .description(statTemplate.getTemplateName())
                .build();
        elasticSimpleJobService.createJob(statCalculateJob, jobCoreConfiguration);

        // 4.数据刷新定时任务


    }
}
