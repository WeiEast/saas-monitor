package com.treefinance.saas.monitor.biz.autostat.template.service;

import com.treefinance.saas.monitor.biz.autostat.base.BaseCacheService;
import com.treefinance.saas.monitor.biz.autostat.base.BaseQueryService;
import com.treefinance.saas.monitor.dao.entity.StatGroup;
import com.treefinance.saas.monitor.facade.domain.request.StatGroupRequest;

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
    /**
     * 统计模板分组列表查询
     *
     * @param groupStatRequest
     * @return
     */
    List<StatGroup> queryStatGroup(StatGroupRequest groupStatRequest);

    /**
     * 新增统计分组
     *
     * @param statGroup
     */
    int addStatGroup(StatGroup statGroup);

    /**
     * 新统计分组
     *
     * @param statGroup
     */
    int updateStatGroup(StatGroup statGroup);

}
