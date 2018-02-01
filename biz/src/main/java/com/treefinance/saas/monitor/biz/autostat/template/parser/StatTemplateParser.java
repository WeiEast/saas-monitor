package com.treefinance.saas.monitor.biz.autostat.template.parser;

import com.treefinance.saas.monitor.dao.entity.StatTemplate;

/**
 * Created by yh-treefinance on 2018/1/23.
 */
public interface StatTemplateParser {
    /**
     * 解析统计模板
     *
     * @param statTemplate
     */
    void parse(StatTemplate statTemplate);
}
