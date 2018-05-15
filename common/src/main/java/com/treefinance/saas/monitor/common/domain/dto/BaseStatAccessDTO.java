package com.treefinance.saas.monitor.common.domain.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author chengtong
 * @date 18/3/13 10:32
 */
public class BaseStatAccessDTO implements Serializable {

    private Long id;

    private String appId;

    private Date dataTime;

    private Byte dataType;

    private Integer userCount;

    private Integer taskCount;


    private Integer entryCount;

    private Integer startLoginCount;

    private Integer loginSuccessCount;


    private Integer crawlSuccessCount;


    private Integer processSuccessCount;


    private Integer callbackSuccessCount;

    private BigDecimal loginConversionRate;

    private BigDecimal loginSuccessRate;

    private BigDecimal crawlSuccessRate;

    private BigDecimal processSuccessRate;

    private BigDecimal callbackSuccessRate;

    /**
     * 前n天的相同时刻创建任务数平均值
     */
    private BigDecimal previousEntryAvgCount;

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

    /**
     * 前n天的相同时刻回调成功数平均值
     */
    private BigDecimal previousCallbackSuccessAvgCount;

    private BigDecimal previousWholeConversionAvgCount;

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

    private BigDecimal wholeConversionRate;

    private BigDecimal previousWholeConversionRate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public Date getDataTime() {
        return dataTime;
    }

    public void setDataTime(Date dataTime) {
        this.dataTime = dataTime;
    }

    public Byte getDataType() {
        return dataType;
    }

    public void setDataType(Byte dataType) {
        this.dataType = dataType;
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

    public Integer getEntryCount() {
        return entryCount;
    }

    public void setEntryCount(Integer entryCount) {
        this.entryCount = entryCount;
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

    public BigDecimal getPreviousEntryAvgCount() {
        return previousEntryAvgCount;
    }

    public void setPreviousEntryAvgCount(BigDecimal previousEntryAvgCount) {
        this.previousEntryAvgCount = previousEntryAvgCount;
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

    public BigDecimal getPreviousCallbackSuccessAvgCount() {
        return previousCallbackSuccessAvgCount;
    }

    public void setPreviousCallbackSuccessAvgCount(BigDecimal previousCallbackSuccessAvgCount) {
        this.previousCallbackSuccessAvgCount = previousCallbackSuccessAvgCount;
    }

    public BigDecimal getPreviousWholeConversionAvgCount() {
        return previousWholeConversionAvgCount;
    }

    public void setPreviousWholeConversionAvgCount(BigDecimal previousWholeConversionAvgCount) {
        this.previousWholeConversionAvgCount = previousWholeConversionAvgCount;
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

    public BigDecimal getWholeConversionRate() {
        return wholeConversionRate;
    }

    public void setWholeConversionRate(BigDecimal wholeConversionRate) {
        this.wholeConversionRate = wholeConversionRate;
    }

    public BigDecimal getPreviousWholeConversionRate() {
        return previousWholeConversionRate;
    }

    public void setPreviousWholeConversionRate(BigDecimal previousWholeConversionRate) {
        this.previousWholeConversionRate = previousWholeConversionRate;
    }

}
