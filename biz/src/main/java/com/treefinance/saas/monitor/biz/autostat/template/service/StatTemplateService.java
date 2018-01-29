package com.treefinance.saas.monitor.biz.autostat.template.service;

import com.treefinance.saas.monitor.biz.autostat.base.BaseQueryService;
import com.treefinance.saas.monitor.dao.entity.StatTemplate;

import java.util.List;

/**
 * Created by yh-treefinance on 2018/1/22.
 */
public interface StatTemplateService extends BaseQueryService<StatTemplate> {
    /**
     * 获取所有启动模板
     *
     * @return
     */
    List<StatTemplate> getActiveList();
}
