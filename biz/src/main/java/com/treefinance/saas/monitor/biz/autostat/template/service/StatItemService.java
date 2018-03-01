package com.treefinance.saas.monitor.biz.autostat.template.service;

import com.treefinance.saas.monitor.biz.autostat.base.BaseCacheService;
import com.treefinance.saas.monitor.biz.autostat.base.BaseQueryService;
import com.treefinance.saas.monitor.dao.entity.StatItem;

import java.util.List;

/**
 * Created by yh-treefinance on 2018/1/23.
 */
public interface StatItemService extends BaseQueryService<StatItem>, BaseCacheService<Long, List<StatItem>> {

    /**
     * @param templateId
     * @return
     */
    List<StatItem> queryByTemplateId(Long templateId);
}