package com.treefinance.saas.monitor.facade.service.autostat;

import com.treefinance.saas.monitor.facade.domain.request.StatTemplateRequest;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;
import com.treefinance.saas.monitor.facade.domain.ro.StatTemplateRO;

import java.util.List;

/**
 * @author:guoguoyun
 * @date:Created in 2018/4/25下午4:10
 */
public interface StatTemplateFacade {
    /**
     * 列表查询所有模板（分页）
     *
     * @param templateStatRequest
     * @return List<StatTemplateRO>
     */
    MonitorResult<List<StatTemplateRO>> queryStatTemplate(StatTemplateRequest templateStatRequest);


    /**
     * 增加一个模板数据
     *
     * @param templateStatRequest
     * @return
     */
    MonitorResult<Boolean> addStatTemplate(StatTemplateRequest templateStatRequest);


    /**
     * 更新模板数据
     *
     * @param templateStatRequest
     * @return
     */
    MonitorResult<Boolean> updateStatTemplate(StatTemplateRequest templateStatRequest);
}
