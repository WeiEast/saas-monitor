package com.treefinance.saas.monitor.biz.autostat.template.service;

import com.treefinance.saas.monitor.biz.autostat.base.BaseCacheService;
import com.treefinance.saas.monitor.biz.autostat.base.BaseQueryService;
import com.treefinance.saas.monitor.dao.entity.StatTemplate;
import com.treefinance.saas.monitor.facade.domain.request.StatTemplateRequest;
import com.treefinance.saas.monitor.facade.domain.request.autostat.TemplateExpressionTestRequest;
import com.treefinance.saas.monitor.facade.domain.request.autostat.TemplateTestRequest;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;

import java.util.List;

/**
 * Created by yh-treefinance on 2018/1/22.
 */
public interface StatTemplateService extends BaseQueryService<StatTemplate>, BaseCacheService<String, StatTemplate> {
    /**
     * 获取所有启动模板
     *
     * @return
     */
    List<StatTemplate> queryActiveList();


    /**
     * 获取所有启动模板(分页)
     *
     * @return
     */
    List<StatTemplate> queryStatTemplate(StatTemplateRequest templateStatRequest);


    /**
     * 计算所有模板数据数目
     *
     * @return
     */
    Long countStatTemplate(StatTemplateRequest templateStatRequest);

    /**
     * 根据模板名字或状态计算所有模板数据数目
     *
     * @param templateStatRequest
     * @return
     */
    Long countStatTemplateByNameOrStatus(StatTemplateRequest templateStatRequest);


    /**
     * 新增一个模板数据
     *
     * @param statTemplate
     */
    int addStatTemplate(StatTemplate statTemplate);

    /**
     * 更新模板数据
     *
     * @param statTemplate
     */
    int updateStatTemplate(StatTemplate statTemplate);


    /**
     * 模板表达式测试
     *
     * @param request
     * @return
     */
    String testTemplateExpression(TemplateExpressionTestRequest request);

    /**
     * 模板测试
     *
     * @param request
     * @return
     */
    String testTemplate(TemplateTestRequest request);
}
