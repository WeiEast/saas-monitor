package com.treefinance.saas.monitor.biz.autostat.template.service.impl.impl;

import com.google.common.base.Function;
import com.treefinance.saas.monitor.biz.autostat.base.AbstractCacheService;
import com.treefinance.saas.monitor.biz.autostat.template.service.StatGroupService;
import com.treefinance.saas.monitor.dao.entity.StatGroup;
import com.treefinance.saas.monitor.dao.entity.StatGroupCriteria;
import com.treefinance.saas.monitor.dao.mapper.StatGroupMapper;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by yh-treefinance on 2018/1/23.
 */
@Service
public class StatGroupServiceImpl extends AbstractCacheService<Long, List<StatGroup>> implements StatGroupService {

    @Autowired
    private StatGroupMapper statGroupMapper;

    @Override
    public List<StatGroup> queryAll() {
        StatGroupCriteria criteria = new StatGroupCriteria();
        criteria.createCriteria();
        return statGroupMapper.selectByExample(criteria);
    }

    @Override
    public StatGroup queryByCode(String code) {
        StatGroupCriteria criteria = new StatGroupCriteria();
        criteria.createCriteria().andGroupCodeEqualTo(code);
        List<StatGroup> list = statGroupMapper.selectByExample(criteria);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public StatGroup queryById(Long id) {
        return statGroupMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<StatGroup> queryByTemplateId(Long templateId) {
        StatGroupCriteria criteria = new StatGroupCriteria();
        criteria.createCriteria().andTemplateIdEqualTo(templateId);
        return statGroupMapper.selectByExample(criteria);
    }

    @Override
    public Function<Long, List<StatGroup>> dataLoader() {
        return this::queryByTemplateId;
    }
}
