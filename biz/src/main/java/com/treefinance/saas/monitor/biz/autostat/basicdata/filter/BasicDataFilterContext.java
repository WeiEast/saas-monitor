package com.treefinance.saas.monitor.biz.autostat.basicdata.filter;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.treefinance.saas.monitor.dao.entity.StatTemplate;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by yh-treefinance on 2018/1/23.
 */
@Component
public class BasicDataFilterContext {
    /**
     * logger
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * template - filter
     */
    private Map<String, BasicDataFilter> contextMap = Maps.newConcurrentMap();

    /**
     * basicData - template
     */
    private Multimap<Long, String> basicDataContext = ArrayListMultimap.create();

    /**
     * 注册
     *
     * @param statTemplate
     * @param basicDataFilter
     */
    public void registerFilter(StatTemplate statTemplate, BasicDataFilter basicDataFilter) {
        synchronized (this) {
            String templateCode = statTemplate.getTemplateCode();
            Long basicDataId = statTemplate.getBasicDataId();
            // 注册filter
            contextMap.put(templateCode, basicDataFilter);
            // 已有不再重复添加
            Collection<String> templateCodes = basicDataContext.get(basicDataId);
            if (templateCodes != null && !templateCodes.contains(templateCode)) {
                basicDataContext.put(basicDataId, templateCode);
            }
            logger.info("registerFilter : statTemplate={}, templateCodes={}...", JSON.toJSONString(statTemplate), templateCodes);
        }
    }

    /**
     * 获取filter
     *
     * @return
     */
    public List<BasicDataFilter> getBasicDataFilters(Long basicDataId) {
        List<BasicDataFilter> dataFilters = Lists.newArrayList();
        Collection<String> templates = basicDataContext.get(basicDataId);
        if (CollectionUtils.isNotEmpty(templates)) {
            templates.forEach(templateCode -> {
                BasicDataFilter filter = contextMap.get(templateCode);
                if (!dataFilters.contains(filter)) {
                    dataFilters.add(filter);
                }
            });
        }
        return dataFilters;
    }

    /**
     * 获取所有Filter
     *
     * @return
     */
    public List<BasicDataFilter> getAllBasicDataFilters() {
        return Lists.newArrayList(contextMap.values());
    }
}
