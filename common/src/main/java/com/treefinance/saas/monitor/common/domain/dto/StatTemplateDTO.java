package com.treefinance.saas.monitor.common.domain.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.Date;

/**
 * Buddha Bless , No Bug !
 *
 * @author haojiahong
 * @date 2018/4/15
 */
public class StatTemplateDTO implements Serializable {

    private static final long serialVersionUID = 5495294049532566416L;

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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    @Override
    public boolean equals(Object o) {

        if (o == this) return true;
        if (!(o instanceof StatTemplateDTO)) {
            return false;
        }
        StatTemplateDTO statTemplateDTO = (StatTemplateDTO) o;

        return new EqualsBuilder()
                .append(id, statTemplateDTO.id)
                .append(templateCode, statTemplateDTO.templateCode)
                .append(templateName, statTemplateDTO.templateName)
                .append(status, statTemplateDTO.status)
                .append(statCron, statTemplateDTO.statCron)
                .append(basicDataId, statTemplateDTO.basicDataId)
                .append(basicDataFilter, statTemplateDTO.basicDataFilter)
                .append(dataObject, statTemplateDTO.dataObject)
                .append(effectiveTime, statTemplateDTO.effectiveTime)
                .append(flushDataCron, statTemplateDTO.flushDataCron)
                .append(createTime, statTemplateDTO.createTime)
                .append(lastUpdateTime, statTemplateDTO.lastUpdateTime)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(templateCode)
                .append(templateName)
                .append(status)
                .append(statCron)
                .append(basicDataId)
                .append(basicDataFilter)
                .append(dataObject)
                .append(effectiveTime)
                .append(flushDataCron)
                .append(createTime)
                .append(lastUpdateTime)
                .toHashCode();
    }

}
