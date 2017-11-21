package com.treefinance.saas.monitor.common.domain.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class OperatorAllStatAccessDTO implements Serializable {

    private static final long serialVersionUID = -5390308997672442434L;

    private Long id;


    private Date dataTime;


    private Byte dataType;


    private Integer entryCount;


    private Integer confirmMobileCount;


    private Integer startLoginCount;


    private Integer loginSuccessCount;


    private Integer crawlSuccessCount;


    private Integer processSuccessCount;


    private Integer callbackSuccessCount;


    private BigDecimal confirmMobileConversionRate;


    private BigDecimal loginConversionRate;


    private BigDecimal loginSuccessRate;


    private BigDecimal crawlSuccessRate;


    private BigDecimal processSuccessRate;


    private BigDecimal callbackSuccessRate;

    /**
     * 前n天的相同时刻创建任务数平均值
     */
    private Integer previousEntryAvgCount;

    /**
     * 前n天的相同时刻确认手机号数平均值
     */
    private Integer previousConfirmMobileAvgCount;

    /**
     * 前n天的相同时刻开始登陆数平均值
     */
    private Integer previousStartLoginAvgCount;

    /**
     * 前n天的相同时刻登陆成功数平均值
     */
    private Integer previousLoginSuccessAvgCount;

    /**
     * 前n天的相同时刻抓取成功数平均值
     */
    private Integer previousCrawlSuccessAvgCount;

    /**
     * 前n天的相同时刻洗数成功数平均值
     */
    private Integer previousProcessSuccessAvgCount;

    /**
     * 前n天的相同时刻回调成功数平均值
     */
    private Integer previousCallbackSuccessAvgCount;

    /**
     * 前n天的相同时刻确认手机转化率平均值
     */
    private BigDecimal previousConfirmMobileConversionRate;

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

    /**
     * 前n天相同时刻回调成功率平均值
     */
    private BigDecimal previousCallbackSuccessRate;


    private Date createTime;


    private Date lastUpdateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataTime() {
        return dataTime;
    }

    public void setDataTime(Date dataTime) {
        this.dataTime = dataTime;
    }

    public Integer getEntryCount() {
        return entryCount;
    }

    public void setEntryCount(Integer entryCount) {
        this.entryCount = entryCount;
    }

    public Integer getConfirmMobileCount() {
        return confirmMobileCount;
    }

    public void setConfirmMobileCount(Integer confirmMobileCount) {
        this.confirmMobileCount = confirmMobileCount;
    }

    public Integer getStartLoginCount() {
        return startLoginCount;
    }

    public void setStartLoginCount(Integer startLoginCount) {
        this.startLoginCount = startLoginCount;
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

    public Integer getCallbackSuccessCount() {
        return callbackSuccessCount;
    }

    public void setCallbackSuccessCount(Integer callbackSuccessCount) {
        this.callbackSuccessCount = callbackSuccessCount;
    }

    public BigDecimal getConfirmMobileConversionRate() {
        return confirmMobileConversionRate;
    }

    public void setConfirmMobileConversionRate(BigDecimal confirmMobileConversionRate) {
        this.confirmMobileConversionRate = confirmMobileConversionRate;
    }

    public BigDecimal getLoginConversionRate() {
        return loginConversionRate;
    }

    public void setLoginConversionRate(BigDecimal loginConversionRate) {
        this.loginConversionRate = loginConversionRate;
    }

    public BigDecimal getLoginSuccessRate() {
        return loginSuccessRate;
    }

    public void setLoginSuccessRate(BigDecimal loginSuccessRate) {
        this.loginSuccessRate = loginSuccessRate;
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

    public BigDecimal getCallbackSuccessRate() {
        return callbackSuccessRate;
    }

    public void setCallbackSuccessRate(BigDecimal callbackSuccessRate) {
        this.callbackSuccessRate = callbackSuccessRate;
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

    public BigDecimal getPreviousConfirmMobileConversionRate() {
        return previousConfirmMobileConversionRate;
    }

    public void setPreviousConfirmMobileConversionRate(BigDecimal previousConfirmMobileConversionRate) {
        this.previousConfirmMobileConversionRate = previousConfirmMobileConversionRate;
    }

    public BigDecimal getPreviousLoginConversionRate() {
        return previousLoginConversionRate;
    }

    public void setPreviousLoginConversionRate(BigDecimal previousLoginConversionRate) {
        this.previousLoginConversionRate = previousLoginConversionRate;
    }

    public BigDecimal getPreviousLoginSuccessRate() {
        return previousLoginSuccessRate;
    }

    public void setPreviousLoginSuccessRate(BigDecimal previousLoginSuccessRate) {
        this.previousLoginSuccessRate = previousLoginSuccessRate;
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

    public BigDecimal getPreviousCallbackSuccessRate() {
        return previousCallbackSuccessRate;
    }

    public void setPreviousCallbackSuccessRate(BigDecimal previousCallbackSuccessRate) {
        this.previousCallbackSuccessRate = previousCallbackSuccessRate;
    }

    public Byte getDataType() {
        return dataType;
    }

    public void setDataType(Byte dataType) {
        this.dataType = dataType;
    }

    public Integer getPreviousEntryAvgCount() {
        return previousEntryAvgCount;
    }

    public void setPreviousEntryAvgCount(Integer previousEntryAvgCount) {
        this.previousEntryAvgCount = previousEntryAvgCount;
    }

    public Integer getPreviousConfirmMobileAvgCount() {
        return previousConfirmMobileAvgCount;
    }

    public void setPreviousConfirmMobileAvgCount(Integer previousConfirmMobileAvgCount) {
        this.previousConfirmMobileAvgCount = previousConfirmMobileAvgCount;
    }

    public Integer getPreviousStartLoginAvgCount() {
        return previousStartLoginAvgCount;
    }

    public void setPreviousStartLoginAvgCount(Integer previousStartLoginAvgCount) {
        this.previousStartLoginAvgCount = previousStartLoginAvgCount;
    }

    public Integer getPreviousLoginSuccessAvgCount() {
        return previousLoginSuccessAvgCount;
    }

    public void setPreviousLoginSuccessAvgCount(Integer previousLoginSuccessAvgCount) {
        this.previousLoginSuccessAvgCount = previousLoginSuccessAvgCount;
    }

    public Integer getPreviousCrawlSuccessAvgCount() {
        return previousCrawlSuccessAvgCount;
    }

    public void setPreviousCrawlSuccessAvgCount(Integer previousCrawlSuccessAvgCount) {
        this.previousCrawlSuccessAvgCount = previousCrawlSuccessAvgCount;
    }

    public Integer getPreviousProcessSuccessAvgCount() {
        return previousProcessSuccessAvgCount;
    }

    public void setPreviousProcessSuccessAvgCount(Integer previousProcessSuccessAvgCount) {
        this.previousProcessSuccessAvgCount = previousProcessSuccessAvgCount;
    }

    public Integer getPreviousCallbackSuccessAvgCount() {
        return previousCallbackSuccessAvgCount;
    }

    public void setPreviousCallbackSuccessAvgCount(Integer previousCallbackSuccessAvgCount) {
        this.previousCallbackSuccessAvgCount = previousCallbackSuccessAvgCount;
    }
}