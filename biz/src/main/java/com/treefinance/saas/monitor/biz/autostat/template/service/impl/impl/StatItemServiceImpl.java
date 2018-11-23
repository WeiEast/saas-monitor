package com.treefinance.saas.monitor.biz.autostat.template.service.impl.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Function;
import com.treefinance.commonservice.uid.UidService;
import com.treefinance.saas.monitor.biz.autostat.base.AbstractCacheService;
import com.treefinance.saas.monitor.biz.autostat.template.service.StatItemService;
import com.treefinance.saas.monitor.dao.entity.StatItem;
import com.treefinance.saas.monitor.dao.entity.StatItemCriteria;
import com.treefinance.saas.monitor.dao.mapper.StatItemMapper;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by yh-treefinance on 2018/1/23.
 */
@Component
public class StatItemServiceImpl extends AbstractCacheService<Long, List<StatItem>> implements StatItemService {
    @Autowired
    private StatItemMapper statItemMapper;
    @Resource
    private UidService uidService;

    @Override
    public List<StatItem> queryAll() {
        StatItemCriteria criteria = new StatItemCriteria();
        criteria.createCriteria();
        return statItemMapper.selectByExample(criteria);
    }

    @Override
    public StatItem queryByCode(String code) {
        StatItemCriteria criteria = new StatItemCriteria();
        criteria.createCriteria().andItemCodeEqualTo(code);
        List<StatItem> list = statItemMapper.selectByExample(criteria);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public StatItem queryById(Long id) {
        return statItemMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<StatItem> queryByTemplateId(Long templateId) {
        StatItemCriteria criteria = new StatItemCriteria();
        criteria.createCriteria().andTemplateIdEqualTo(templateId);
        return statItemMapper.selectByExample(criteria);
    }

    @Override
    public Function<Long, List<StatItem>> dataLoader() {
        return this::queryByTemplateId;
    }

    @Override
    public long addStatItem(StatItem statItem) {
        long id = uidService.getId();
        statItem.setId(id);
        logger.info("insert statitem : {}", JSON.toJSONString(statItem));
        statItemMapper.insert(statItem);
        return id;
    }

    @Override
    public int updateStatItem(StatItem statItem) {
        logger.info("update statitem : {}", JSON.toJSONString(statItem));
        return statItemMapper.updateByPrimaryKeySelective(statItem);
    }
}
