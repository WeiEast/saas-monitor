package com.treefinance.saas.monitor.common.domain.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class OperatorStatAccessDTO implements Serializable {

    private Long id;

    private String appId;

    private String groupCode;

    private String groupName;

    private Date dataTime;

    private Byte dataType;

    private Integer userCount;

    private Integer taskCount;

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
     * 前n天的相同时刻确认手机号数平均值
     */
    private BigDecimal previousConfirmMobileAvgCount;

    /**
     * 前n天的相同时刻开始登陆数平均值
     */
    private BigDecimal previousStartLoginAvgCount;

    /**
     * 前n天的相同时刻登陆成功数平均值
     */
    private BigDecimal previousLoginSuccessAvgCount;

    /**
     * 前n天的相同时刻抓取成功数平均值
     */
    private BigDecimal previousCrawlSuccessAvgCount;

    /**
     * 前n天的相同时刻洗数成功数平均值
     */
    private BigDecimal previousProcessSuccessAvgCount;

    private BigDecimal previousCallbackSuccessAvgCount;


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

    private BigDecimal previousCallbackSuccessRateRate;


    private Date createTime;

    private Date lastUpdateTime;

    private static final long serialVersionUID = 1L;

    public BigDecimal getConfirmMobileConversionRate() {
        return confirmMobileConversionRate;
    }

    public void setConfirmMobileConversionRate(BigDecimal confirmMobileConversionRate) {
        this.confirmMobileConversionRate = confirmMobileConversionRate;
    }

    public Integer getEntryCount() {
        return entryCount;
    }

    public void setEntryCount(Integer entryCount) {
        this.entryCount = entryCount;
    }

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

    public Byte getDataType() {
        return dataType;
    }

    public void setDataType(Byte dataType) {
        this.dataType = dataType;
    }

    public BigDecimal getPreviousConfirmMobileAvgCount() {
        return previousConfirmMobileAvgCount;
    }

    public void setPreviousConfirmMobileAvgCount(BigDecimal previousConfirmMobileAvgCount) {
        this.previousConfirmMobileAvgCount = previousConfirmMobileAvgCount;
    }

    public BigDecimal getPreviousStartLoginAvgCount() {
        return previousStartLoginAvgCount;
    }

    public void setPreviousStartLoginAvgCount(BigDecimal previousStartLoginAvgCount) {
        this.previousStartLoginAvgCount = previousStartLoginAvgCount;
    }

    public BigDecimal getPreviousLoginSuccessAvgCount() {
        return previousLoginSuccessAvgCount;
    }

    public void setPreviousLoginSuccessAvgCount(BigDecimal previousLoginSuccessAvgCount) {
        this.previousLoginSuccessAvgCount = previousLoginSuccessAvgCount;
    }

    public BigDecimal getPreviousCrawlSuccessAvgCount() {
        return previousCrawlSuccessAvgCount;
    }

    public void setPreviousCrawlSuccessAvgCount(BigDecimal previousCrawlSuccessAvgCount) {
        this.previousCrawlSuccessAvgCount = previousCrawlSuccessAvgCount;
    }

    public BigDecimal getPreviousProcessSuccessAvgCount() {
        return previousProcessSuccessAvgCount;
    }

    public void setPreviousProcessSuccessAvgCount(BigDecimal previousProcessSuccessAvgCount) {
        this.previousProcessSuccessAvgCount = previousProcessSuccessAvgCount;
    }

    public Integer getCallbackSuccessCount() {
        return callbackSuccessCount;
    }

    public void setCallbackSuccessCount(Integer callbackSuccessCount) {
        this.callbackSuccessCount = callbackSuccessCount;
    }

    public BigDecimal getCallbackSuccessRate() {
        return callbackSuccessRate;
    }

    public void setCallbackSuccessRate(BigDecimal callbackSuccessRate) {
        this.callbackSuccessRate = callbackSuccessRate;
    }

    public BigDecimal getPreviousCallbackSuccessAvgCount() {
        return previousCallbackSuccessAvgCount;
    }

    public void setPreviousCallbackSuccessAvgCount(BigDecimal previousCallbackSuccessAvgCount) {
        this.previousCallbackSuccessAvgCount = previousCallbackSuccessAvgCount;
    }

    public BigDecimal getPreviousCallbackSuccessRateRate() {
        return previousCallbackSuccessRateRate;
    }

    public void setPreviousCallbackSuccessRateRate(BigDecimal previousCallbackSuccessRateRate) {
        this.previousCallbackSuccessRateRate = previousCallbackSuccessRateRate;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public Integer getUserCount() {
        return userCount;
    }

    public void setUserCount(Integer userCount) {
        this.userCount = userCount;
    }

    public Integer getTaskCount() {
        return taskCount;
    }

    public void setTaskCount(Integer taskCount) {
        this.taskCount = taskCount;
    }
}