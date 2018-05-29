package com.treefinance.saas.monitor.facade.domain.request;

import com.treefinance.saas.monitor.facade.domain.base.BaseRequest;

/**
 * @author:guoguoyun
 * @date:Created in 2018/4/26下午7:53
 */
public class StatGroupRequest extends BaseRequest{

    /**
     *  主键ID
     */
    private Long id;

    /**
     * 分组序号：每个模板对应多个组别
     */
    private Integer groupIndex;

    /**
     * 编码
     */
    private String groupCode;

    /**
     * 名称
     */
    private String groupName;

    /**
     * 分组表达式
     */
    private String groupExpression;

    /**
     * 模板ID
     */
    private Long templateId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getGroupIndex() {
        return groupIndex;
    }

    public void setGroupIndex(Integer groupIndex) {
        this.groupIndex = groupIndex;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupExpression() {
        return groupExpression;
    }

    public void setGroupExpression(String groupExpression) {
        this.groupExpression = groupExpression;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    @Override
    public String toString() {
        return "StatGroupRequest{" +
                "id=" + id +
                ", groupIndex=" + groupIndex +
                ", groupCode='" + groupCode + '\'' +
                ", groupName='" + groupName + '\'' +
                ", groupExpression='" + groupExpression + '\'' +
                ", templateId=" + templateId +
                '}';
    }
}
