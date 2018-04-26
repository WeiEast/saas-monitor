package com.treefinance.saas.monitor.facade.service.stat;

import com.treefinance.saas.monitor.facade.domain.base.PageRequest;
import com.treefinance.saas.monitor.facade.domain.request.TemplateStatRequest;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;
import com.treefinance.saas.monitor.facade.domain.ro.StatTemplateRO;

import java.util.List;

/**
 * @author:guoguoyun
 * @date:Created in 2018/4/25下午4:10
 */
public interface TemplateStatFacade {


    /**
     * 根据templateName或status 查询统计模板（(分页）
     * @param templateStatRequest
     * @return
     */
    MonitorResult<List<StatTemplateRO>>  queryStatTemplateByNameOrStatus(TemplateStatRequest  templateStatRequest);


    /**
     * 无条件查询所有模板（分页）
     * @param pageRequest
     * @return
     */
    MonitorResult<List<StatTemplateRO>>  queryStatTemplate(PageRequest pageRequest);


    /**
     * 增加一个模板数据
     * @param templateStatRequest
     * @return
     */
    MonitorResult<Boolean>  addStatTemplate(TemplateStatRequest templateStatRequest);


    /**
     * 更新模板数据
     * @param templateStatRequest
     * @return
     */
    MonitorResult<Boolean> updateStatTemplate(TemplateStatRequest templateStatRequest)；
}
