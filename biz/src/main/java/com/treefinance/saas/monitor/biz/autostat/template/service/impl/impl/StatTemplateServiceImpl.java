package com.treefinance.saas.monitor.biz.autostat.template.service.impl.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.treefinance.saas.monitor.biz.autostat.base.AbstractCacheService;
import com.treefinance.saas.monitor.biz.autostat.model.AsConstants;
import com.treefinance.saas.monitor.biz.autostat.template.calc.ExpressionCalculator;
import com.treefinance.saas.monitor.biz.autostat.template.calc.StatDataCalculator;
import com.treefinance.saas.monitor.biz.autostat.template.service.StatTemplateService;
import com.treefinance.saas.monitor.dao.entity.StatTemplate;
import com.treefinance.saas.monitor.dao.entity.StatTemplateCriteria;
import com.treefinance.saas.monitor.dao.mapper.StatTemplateMapper;
import com.treefinance.saas.monitor.facade.domain.request.StatTemplateRequest;
import com.treefinance.saas.monitor.facade.domain.request.autostat.TemplateExpressionTestRequest;
import com.treefinance.saas.monitor.facade.domain.request.autostat.TemplateTestRequest;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by yh-treefinance on 2018/1/23.
 */
@Component
public class StatTemplateServiceImpl extends AbstractCacheService<String, StatTemplate> implements StatTemplateService {

    @Autowired
    private StatTemplateMapper statTemplateMapper;

    @Autowired
    private ExpressionCalculator expressionCalculator;
    @Autowired
    private StatDataCalculator statDataCalculator;

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


        if (templateStatRequest.getStatus() == null || templateStatRequest.getStatus() == 2) {
            if (templateStatRequest.getTemplateName() == null) {
                statTemplateCriteria.createCriteria();
            } else {
                statTemplateCriteria.createCriteria().andTemplateNameLike("%" + templateStatRequest.getTemplateName() + "%");
            }

        } else {
            if (templateStatRequest.getTemplateName() == null) {
                statTemplateCriteria.createCriteria().andStatusEqualTo(templateStatRequest.getStatus());
            } else {
                statTemplateCriteria.createCriteria().andStatusEqualTo(templateStatRequest.getStatus()).andTemplateNameLike("%" + templateStatRequest.getTemplateName() + "%");
            }

        }

        return statTemplateMapper.selectPaginationByExample(statTemplateCriteria);

    }


    @Override
    public Long countStatTemplate(StatTemplateRequest templateStatRequest) {
        StatTemplateCriteria statTemplateCriteria = new StatTemplateCriteria();

        if (templateStatRequest.getStatus() == null || templateStatRequest.getStatus() == 2) {
            if (templateStatRequest.getTemplateName() == null) {
                statTemplateCriteria.createCriteria();
            } else {
                statTemplateCriteria.createCriteria().andTemplateNameLike("%" + templateStatRequest.getTemplateName() + "%");
            }

        } else {
            if (templateStatRequest.getTemplateName() == null) {
                statTemplateCriteria.createCriteria().andStatusEqualTo(templateStatRequest.getStatus());
            } else {
                statTemplateCriteria.createCriteria().andStatusEqualTo(templateStatRequest.getStatus()).andTemplateNameLike("%" + templateStatRequest.getTemplateName() + "%");
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

    @Override
    public String testTemplateExpression(TemplateExpressionTestRequest request) {
        Object value = null;
        try {
            Long expressionId = -Math.abs(RandomUtils.nextLong());
            StatTemplate statTemplate = queryById(request.getTemplateId());
            if (statTemplate == null) {
                return "模板不存在！";
            }
            String expression = request.getExpression();
            if (StringUtils.isEmpty(expression)) {
                return "表达式不能为空！";
            }
            String dataJson = request.getDataJson();
            if (StringUtils.isEmpty(dataJson)) {
                return "dataJson不能为空！";
            }

            Map<String, Object> dataMap = Maps.newHashMap();
            try {
                dataMap = JSON.parseObject(dataJson);
            } catch (Exception e) {
                return "dataJson格式不正确！";
            }
            // 计算模板
            expressionCalculator.initContext(AsConstants.STAT_TEMPLATE, statTemplate);
            value = expressionCalculator.calculate(expressionId, expression, dataMap);
        } catch (Exception e) {
            StringWriter stringWriter = new StringWriter();
            e.printStackTrace(new PrintWriter(stringWriter));
            value = stringWriter.toString();
        }
        logger.info("testTemplateExpression : request={} ,result={}", JSON.toJSONString(request), value);
        return value == null ? "" : value.toString();
    }

    @Override
    public String testTemplate(TemplateTestRequest request) {
        Object value = null;
        try {
            StatTemplate statTemplate = queryById(request.getTemplateId());
            if (statTemplate == null) {
                return "模板不存在！";
            }
            if (CollectionUtils.isEmpty(request.getDataJsons())) {
                return "dataJson不能为空！";
            }

            List<Map<String, Object>> dataList = Lists.newArrayList();
            for (String dataJson : request.getDataJsons()) {
                try {
                    Map<String, Object> dataMap = JSON.parseObject(dataJson);
                    dataList.add(dataMap);
                } catch (Exception e) {
                    return "dataJson格式不正确: " + dataJson;
                }
            }
            Integer[] _groupIndexs = new Integer[]{};
            if (CollectionUtils.isNotEmpty(request.getGroupIndexs())) {
                _groupIndexs = request.getGroupIndexs().toArray(new Integer[]{});
            }
            // 计算模板
            Map<Integer, List<Map<String, Object>>> valueMap = statDataCalculator.calculate(statTemplate, dataList, _groupIndexs);
            Map<String, List<Map<String, Object>>> _valueMap = Maps.newHashMap();
            valueMap.keySet().forEach(groupIndex -> {
                _valueMap.put(groupIndex + "", valueMap.get(groupIndex));
            });
            value = _valueMap;
            return JSON.toJSONString(value);
        } catch (Exception e) {
            StringWriter stringWriter = new StringWriter();
            e.printStackTrace(new PrintWriter(stringWriter));
            value = stringWriter.toString();
        }

        logger.info("testTemplate : request={} ,result={}", JSON.toJSONString(request), value);
        return value == null ? "" : value.toString();
    }
}
