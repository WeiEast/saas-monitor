package com.treefinance.saas.monitor.biz.facade;

import com.google.common.collect.Lists;
import com.treefinance.saas.monitor.biz.autostat.template.service.StatItemService;
import com.treefinance.saas.monitor.common.utils.DataConverterUtils;
import com.treefinance.saas.monitor.dao.entity.StatItem;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResultBuilder;
import com.treefinance.saas.monitor.facade.domain.ro.autostat.StatItemRO;
import com.treefinance.saas.monitor.facade.exception.ParamCheckerException;
import com.treefinance.saas.monitor.facade.service.autostat.StatItemFacade;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by yh-treefinance on 2018/5/21.
 */
@Service("statItemFacade")
public class StatItemFacadeImpl implements StatItemFacade {

    @Autowired
    private StatItemService statItemService;

    @Override
    public MonitorResult<List<StatItemRO>> queryByTemplateId(Long templateId) {
        if (templateId == null) {
            throw new ParamCheckerException("模板ID不能为空");
        }
        List<StatItem> statItems = statItemService.queryByTemplateId(templateId);
        if (statItems == null) {
            statItems = Lists.newArrayList();
        }
        List<StatItemRO> statItemROS = DataConverterUtils.convert(statItems, StatItemRO.class);
        return MonitorResultBuilder.build(statItemROS);
    }

    @Override
    public MonitorResult<Long> addStatItem(StatItemRO statItemRO) {
        if (statItemRO == null) {
            throw new ParamCheckerException("统计数据项信息不能为空");
        }
        if (StringUtils.isEmpty(statItemRO.getItemCode())) {
            throw new ParamCheckerException("数据项编码不能为空");
        }
        if (StringUtils.isEmpty(statItemRO.getItemName())) {
            throw new ParamCheckerException("数据项名称不能为空");
        }
        if (StringUtils.isEmpty(statItemRO.getItemExpression())) {
            throw new ParamCheckerException("数据项表达式不能为空");
        }
        if (statItemRO.getTemplateId() == null) {
            throw new ParamCheckerException("关联模板ID不能为空");
        }
        if (statItemRO.getDataSource() == null) {
            throw new ParamCheckerException("数据来源不能为空");
        }
        StatItem statItem = DataConverterUtils.convert(statItemRO, StatItem.class);
        Long id = statItemService.addStatItem(statItem);
        return MonitorResultBuilder.build(id);
    }

    @Override
    public MonitorResult<Boolean> updateStatItem(StatItemRO statItemRO) {
        if (statItemRO == null) {
            throw new ParamCheckerException("统计数据项信息不能为空");
        }
        if (statItemRO.getId() == null) {
            throw new ParamCheckerException("数据项ID不能为空");
        }
        StatItem statItem = DataConverterUtils.convert(statItemRO, StatItem.class);
        int result = statItemService.updateStatItem(statItem);
        return MonitorResultBuilder.build(result >= 0);
    }
}
