package com.treefinance.saas.monitor.facade.domain.request.autostat;

import com.treefinance.saas.monitor.facade.domain.base.BaseRequest;

/**
 * Created by yh-treefinance on 2018/5/23.
 */
public class TemplateExpressionTestRequest extends BaseRequest {
    /**
     * 模板ID
     */
    private Long templateId;

    /**
     * 基础数据json
     */
    private String dataJson;

    /**
     * 表达式
     */
    private String expression;


    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public String getDataJson() {
        return dataJson;
    }

    public void setDataJson(String dataJson) {
        this.dataJson = dataJson;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }
}
