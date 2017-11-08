package com.treefinance.saas.monitor.common.domain.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class OperatorStatAccessDTO implements Serializable {

    private Long id;

    private String groupCode;

    private String groupName;

    private Date dataTime;

    private Integer confirmMobileCount;

    private Integer startLoginCount;

    private Integer loginSuccessCount;

    private Integer crawlSuccessCount;

    private Integer processSuccessCount;

    private BigDecimal loginConversionRate;

    private BigDecimal loginSuccessRate;

    private BigDecimal crawlSuccessRate;

    private BigDecimal processSuccessRate;

    /**
     * 前n天的相同时刻登录转化率平均值
     */
    private BigDecimal previousLoginConversionRate;

    /**
     * 前n天的相同时刻登陆成功率平均值
     */
    private BigDecimal previousLoginSuccessRate;

    /**
     * 前n天的相同时刻抓取成功率平均值
     */
    private BigDecimal previousCrawlSuccessRate;

    /**
     * 前n天的相同时刻洗数成功率平均值
     */
    private BigDecimal previousProcessSuccessRate;


    private Date createTime;

    private Date lastUpdateTime;

    private static final long serialVersionUID = 1L;

    public BigDecimal getLoginSuccessRate() {
        return loginSuccessRate;
    }

    public void setLoginSuccessRate(BigDecimal loginSuccessRate) {
        this.loginSuccessRate = loginSuccessRate;
    }

    public BigDecimal getPreviousLoginSuccessRate() {
        return previousLoginSuccessRate;
    }

    public void setPreviousLoginSuccessRate(BigDecimal previousLoginSuccessRate) {
        this.previousLoginSuccessRate = previousLoginSuccessRate;
    }

    public Integer getStartLoginCount() {
        return startLoginCount;
    }

    public void setStartLoginCount(Integer startLoginCount) {
        this.startLoginCount = startLoginCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Date getDataTime() {
        return dataTime;
    }

    public void setDataTime(Date dataTime) {
        this.dataTime = dataTime;
    }

    public Integer getConfirmMobileCount() {
        return confirmMobileCount;
    }

    public void setConfirmMobileCount(Integer confirmMobileCount) {
        this.confirmMobileCount = confirmMobileCount;
    }

    public Integer getLoginSuccessCount() {
        return loginSuccessCount;
    }

    public void setLoginSuccessCount(Integer loginSuccessCount) {
        this.loginSuccessCount = loginSuccessCount;
    }

    public Integer getCrawlSuccessCount() {
        return crawlSuccessCount;
    }

    public void setCrawlSuccessCount(Integer crawlSuccessCount) {
        this.crawlSuccessCount = crawlSuccessCount;
    }

    public Integer getProcessSuccessCount() {
        return processSuccessCount;
    }

    public void setProcessSuccessCount(Integer processSuccessCount) {
        this.processSuccessCount = processSuccessCount;
    }

    public BigDecimal getLoginConversionRate() {
        return loginConversionRate;
    }

    public void setLoginConversionRate(BigDecimal loginConversionRate) {
        this.loginConversionRate = loginConversionRate;
    }

    public BigDecimal getCrawlSuccessRate() {
        return crawlSuccessRate;
    }

    public void setCrawlSuccessRate(BigDecimal crawlSuccessRate) {
        this.crawlSuccessRate = crawlSuccessRate;
    }

    public BigDecimal getProcessSuccessRate() {
        return processSuccessRate;
    }

    public void setProcessSuccessRate(BigDecimal processSuccessRate) {
        this.processSuccessRate = processSuccessRate;
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

    public BigDecimal getPreviousLoginConversionRate() {
        return previousLoginConversionRate;
    }

    public void setPreviousLoginConversionRate(BigDecimal previousLoginConversionRate) {
        this.previousLoginConversionRate = previousLoginConversionRate;
    }

    public BigDecimal getPreviousCrawlSuccessRate() {
        return previousCrawlSuccessRate;
    }

    public void setPreviousCrawlSuccessRate(BigDecimal previousCrawlSuccessRate) {
        this.previousCrawlSuccessRate = previousCrawlSuccessRate;
    }

    public BigDecimal getPreviousProcessSuccessRate() {
        return previousProcessSuccessRate;
    }

    public void setPreviousProcessSuccessRate(BigDecimal previousProcessSuccessRate) {
        this.previousProcessSuccessRate = previousProcessSuccessRate;
    }
}