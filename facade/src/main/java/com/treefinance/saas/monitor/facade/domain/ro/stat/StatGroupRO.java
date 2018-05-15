package com.treefinance.saas.monitor.facade.domain.ro.stat;

import com.treefinance.saas.monitor.facade.domain.base.BaseRO;


/**
 * @author:guoguoyun
 * @date:Created in 2018/4/26下午7:47
 */
public class StatGroupRO extends BaseRO{


    private Long id;


    private Integer groupIndex;

    private String groupCode;

    private String groupName;


    private String groupExpression;


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
        return "StatGroupRO{" +
                "id=" + id +
                ", groupIndex=" + groupIndex +
                ", groupCode='" + groupCode + '\'' +
                ", groupName='" + groupName + '\'' +
                ", groupExpression='" + groupExpression + '\'' +
                ", templateId=" + templateId +
                '}';
    }
}
