package com.treefinance.saas.monitor.facade.domain.request;

import com.treefinance.saas.monitor.facade.domain.base.PageRequest;

/**
 * @author:guoguoyun
 * @date:Created in 2018/4/25下午4:21
 */
public class TemplateStatRequest extends PageRequest {
    /**
     * 统计模板的ID
     */
    private Long id;

    /**
     * 统计模板关键字
     */
    private String templateName;

    /**
     * 模板状态（0：不启用，1：启用，2：全部）
     */
    private Byte status;

    /**
     * 统计模板编码
     */
    private String templateCode;

    /**
     * 统计时间cron表达式
     */
    private String statCron;

    /**
     * 基础数据来源
     */
    private Long basicDataId;

    /**
     * 基础数据过滤
     */
    private String basicDataFilter;


    /**
     * 数据有效期：单位分钟
     */
    private Integer effectiveTime;

    /**
     * 数据刷新时间(cron表达式)
     */
    private String flushDataCron;


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
    public String toString() {
        return "TemplateStatRequest{" +
                "id=" + id +
                ", templateName='" + templateName + '\'' +
                ", status=" + status +
                ", templateCode='" + templateCode + '\'' +
                ", statCron='" + statCron + '\'' +
                ", basicDataId=" + basicDataId +
                ", basicDataFilter='" + basicDataFilter + '\'' +
                ", effectiveTime=" + effectiveTime +
                ", flushDataCron='" + flushDataCron + '\'' +
                '}';
    }
}
