package com.treefinance.saas.monitor.facade.service.autostat;

import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;
import com.treefinance.saas.monitor.facade.domain.ro.autostat.StatItemRO;

import java.util.List;

/**
 * Created by yh-treefinance on 2018/5/21.
 */
public interface StatItemFacade {

    /**
     * 根据模板ID查询数据项
     *
     * @param templateId
     * @return
     */
    MonitorResult<List<StatItemRO>> queryByTemplateId(Long templateId);

    /**
     * 添加数据项
     *
     * @param statItemRO
     * @return
     */
    MonitorResult<Long> addStatItem(StatItemRO statItemRO);

    /**
     * 更新数据项
     *
     * @param statItemRO
     * @return
     */
    MonitorResult<Boolean> updateStatItem(StatItemRO statItemRO);
}
