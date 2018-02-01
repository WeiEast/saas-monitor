package com.treefinance.saas.monitor.biz.autostat.template.service.impl.impl;

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
public class StatGroupServiceImpl implements StatGroupService {

    @Autowired
    private StatGroupMapper statGroupMapper;

    @Override
    public List<StatGroup> getAll() {
        StatGroupCriteria criteria = new StatGroupCriteria();
        criteria.createCriteria();
        return statGroupMapper.selectByExample(criteria);
    }

    @Override
    public StatGroup getByCode(String code) {
        StatGroupCriteria criteria = new StatGroupCriteria();
        criteria.createCriteria().andGroupCodeEqualTo(code);
        List<StatGroup> list = statGroupMapper.selectByExample(criteria);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public StatGroup getById(Long id) {
        return statGroupMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<StatGroup> getByTemplateId(Long templateId) {
        StatGroupCriteria criteria = new StatGroupCriteria();
        criteria.createCriteria().andTemplateIdEqualTo(templateId);
        return statGroupMapper.selectByExample(criteria);
    }
}
