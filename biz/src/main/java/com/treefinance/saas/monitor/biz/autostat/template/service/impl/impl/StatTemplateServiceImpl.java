package com.treefinance.saas.monitor.biz.autostat.template.service.impl.impl;

import com.google.common.base.Function;
import com.treefinance.saas.monitor.biz.autostat.base.AbstractCacheService;
import com.treefinance.saas.monitor.biz.autostat.template.service.StatTemplateService;
import com.treefinance.saas.monitor.dao.entity.StatTemplate;
import com.treefinance.saas.monitor.dao.entity.StatTemplateCriteria;
import com.treefinance.saas.monitor.dao.mapper.StatTemplateMapper;
import com.treefinance.saas.monitor.facade.domain.request.StatTemplateRequest;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by yh-treefinance on 2018/1/23.
 */
@Component
public class StatTemplateServiceImpl extends AbstractCacheService<String, StatTemplate> implements StatTemplateService {

    @Autowired
    private StatTemplateMapper statTemplateMapper;

    @Override
    public List<StatTemplate> queryAll() {
        StatTemplateCriteria criteria = new StatTemplateCriteria();
        criteria.createCriteria();
        return statTemplateMapper.selectByExample(criteria);
    }

    @Override
    public StatTemplate queryByCode(String code) {
        StatTemplateCriteria criteria = new StatTemplateCriteria();
        criteria.createCriteria().andTemplateCodeEqualTo(code);

        List<StatTemplate> list = statTemplateMapper.selectByExample(criteria);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public StatTemplate queryById(Long id) {
        return statTemplateMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<StatTemplate> queryActiveList() {
        StatTemplateCriteria criteria = new StatTemplateCriteria();
        criteria.createCriteria().andStatusEqualTo((byte) 1);
        return statTemplateMapper.selectByExample(criteria);
    }

    @Override
    public Function<String, StatTemplate> dataLoader() {
        return this::queryByCode;
    }


    @Override
    public List<StatTemplate> queryStatTemplate(StatTemplateRequest templateStatRequest) {
        StatTemplateCriteria statTemplateCriteria = new StatTemplateCriteria();

        statTemplateCriteria.setLimit(templateStatRequest.getPageSize());
        statTemplateCriteria.setOffset(templateStatRequest.getOffset());


        if (templateStatRequest.getStatus() == null||templateStatRequest.getStatus() == 2) {
            if (templateStatRequest.getTemplateName() == null) {
                statTemplateCriteria.createCriteria();
            } else {
                statTemplateCriteria.createCriteria().andTemplateNameLike("%"+templateStatRequest.getTemplateName()+"%");
            }

        } else {
            if (templateStatRequest.getTemplateName() == null) {
                statTemplateCriteria.createCriteria().andStatusEqualTo(templateStatRequest.getStatus());
            } else {
                statTemplateCriteria.createCriteria().andStatusEqualTo(templateStatRequest.getStatus()).andTemplateNameLike("%"+templateStatRequest.getTemplateName()+"%");
            }

        }

        return statTemplateMapper.selectPaginationByExample(statTemplateCriteria);

    }


    @Override
    public Long countStatTemplate(StatTemplateRequest templateStatRequest) {
        StatTemplateCriteria statTemplateCriteria = new StatTemplateCriteria();

        if (templateStatRequest.getStatus() == null||templateStatRequest.getStatus() == 2) {
            if (templateStatRequest.getTemplateName() == null) {
                statTemplateCriteria.createCriteria();
            } else {
                statTemplateCriteria.createCriteria().andTemplateNameLike("%"+templateStatRequest.getTemplateName()+"%");
            }

        } else {
            if (templateStatRequest.getTemplateName() == null) {
                statTemplateCriteria.createCriteria().andStatusEqualTo(templateStatRequest.getStatus());
            } else {
                statTemplateCriteria.createCriteria().andStatusEqualTo(templateStatRequest.getStatus()).andTemplateNameLike("%"+templateStatRequest.getTemplateName()+"%");
            }

        }
        return statTemplateMapper.countByExample(statTemplateCriteria);
    }

    @Override
    public Long countStatTemplateByNameOrStatus(StatTemplateRequest templateStatRequest) {
        StatTemplateCriteria statTemplateCriteria = new StatTemplateCriteria();
        if (templateStatRequest.getStatus() == 2) {
            if (templateStatRequest.getTemplateName() != null) {
                statTemplateCriteria.createCriteria();
            } else {
                statTemplateCriteria.createCriteria().andTemplateNameLike(templateStatRequest.getTemplateName());
            }

        } else {
            if (templateStatRequest.getTemplateName() != null) {
                statTemplateCriteria.createCriteria().andStatusEqualTo(templateStatRequest.getStatus());
            } else {
                statTemplateCriteria.createCriteria().andStatusEqualTo(templateStatRequest.getStatus()).andTemplateNameLike(templateStatRequest.getTemplateName());
            }

        }

        return statTemplateMapper.countByExample(statTemplateCriteria);

    }

    @Override
    public int addStatTemplate(StatTemplate statTemplate) {
        return statTemplateMapper.insert(statTemplate);

    }

    @Override
    public int updateStatTemplate(StatTemplate statTemplate) {
        return statTemplateMapper.updateByPrimaryKey(statTemplate);

    }
}
