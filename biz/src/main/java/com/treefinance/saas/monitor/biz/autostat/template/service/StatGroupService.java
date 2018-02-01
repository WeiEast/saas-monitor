package com.treefinance.saas.monitor.biz.autostat.template.service;

import com.treefinance.saas.monitor.biz.autostat.base.BaseCacheService;
import com.treefinance.saas.monitor.biz.autostat.base.BaseQueryService;
import com.treefinance.saas.monitor.dao.entity.StatGroup;

import java.util.List;

/**
 * Created by yh-treefinance on 2018/1/23.
 */
public interface StatGroupService extends BaseQueryService<StatGroup>,BaseCacheService<Long, List<StatGroup>> {

    /**
     * 根据模板ID获取
     *
     * @param templateId
     * @return
     */
    List<StatGroup> queryByTemplateId(Long templateId);
}
