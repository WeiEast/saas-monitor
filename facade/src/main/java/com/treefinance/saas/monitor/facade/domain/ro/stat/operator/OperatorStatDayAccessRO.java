package com.treefinance.saas.monitor.facade.domain.ro.stat.operator;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author haojiahong
 * @date 2017/11/1
 */
public class OperatorStatDayAccessRO implements Serializable {

    private static final long serialVersionUID = -4982953040353997746L;

    private Long id;

    private String appId;

    private String groupCode;

    private String groupName;

    private Integer userCount;

    private Integer taskCount;

    private Date dataTime;

    private Integer confirmMobileCount;

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

    private Date createTime;

    private Date lastUpdateTime;

    private BigDecimal dayAverage;

    /**
     * 任务人数比
     */
    private BigDecimal taskUserRatio;

    public BigDecimal getLoginSuccessRate() {
        return loginSuccessRate;
    }

    public void setLoginSuccessRate(BigDecimal loginSuccessRate) {
        this.loginSuccessRate = loginSuccessRate;
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

    public BigDecimal getTaskUserRatio() {
        return taskUserRatio;
    }

    public void setTaskUserRatio(BigDecimal taskUserRatio) {
        this.taskUserRatio = taskUserRatio;
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

    public BigDecimal getDayAverage() {
        return dayAverage;
    }

    public void setDayAverage(BigDecimal dayAverage) {
        this.dayAverage = dayAverage;
    }
}
