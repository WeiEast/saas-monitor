package com.treefinance.saas.monitor.facade.domain.ro;

import com.treefinance.saas.monitor.facade.domain.base.BaseRO;

import java.util.Date;

/**
 * @author:guoguoyun
 * @date:Created in 2018/4/25下午4:13
 */
public class StatTemplateRO extends BaseRO {

    private Long id;


    private String templateCode;


    private String templateName;


    private Byte status;


    private String statCron;


    private Long basicDataId;


    private String basicDataFilter;

    private String dataObject;


    private Integer effectiveTime;


    private String flushDataCron;


    private Date createTime;


    private Date lastUpdateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public String getStatCron() {
        return statCron;
    }

    public void setStatCron(String statCron) {
        this.statCron = statCron;
    }

    public Long getBasicDataId() {
        return basicDataId;
    }

    public void setBasicDataId(Long basicDataId) {
        this.basicDataId = basicDataId;
    }

    public String getBasicDataFilter() {
        return basicDataFilter;
    }

    public void setBasicDataFilter(String basicDataFilter) {
        this.basicDataFilter = basicDataFilter;
    }

    public String getDataObject() {
        return dataObject;
    }

    public void setDataObject(String dataObject) {
        this.dataObject = dataObject;
    }

    public Integer getEffectiveTime() {
        return effectiveTime;
    }

    public void setEffectiveTime(Integer effectiveTime) {
        this.effectiveTime = effectiveTime;
    }

    public String getFlushDataCron() {
        return flushDataCron;
    }

    public void setFlushDataCron(String flushDataCron) {
        this.flushDataCron = flushDataCron;
    }

    @Override
    public Date getCreateTime() {
        return createTime;
    }

    @Override
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    @Override
    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    @Override
    public String toString() {
        return "StatTemplateRO{" +
                "id=" + id +
                ", templateCode='" + templateCode + '\'' +
                ", templateName='" + templateName + '\'' +
                ", status=" + status +
                ", statCron='" + statCron + '\'' +
                ", basicDataId=" + basicDataId +
                ", basicDataFilter='" + basicDataFilter + '\'' +
                ", dataObject='" + dataObject + '\'' +
                ", effectiveTime=" + effectiveTime +
                ", flushDataCron='" + flushDataCron + '\'' +
                ", createTime=" + createTime +
                ", lastUpdateTime=" + lastUpdateTime +
                '}';
    }
}
