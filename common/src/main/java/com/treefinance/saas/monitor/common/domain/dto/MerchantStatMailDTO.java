package com.treefinance.saas.monitor.common.domain.dto;

import com.treefinance.saas.monitor.common.domain.BaseDTO;

import java.math.BigDecimal;
import java.util.Date;

public class MerchantStatMailDTO extends BaseDTO {
    /**
     * 数据ID
     */
    private Long id;

    /**
     * 数据时间
     */
    private Date dataTime;

    /**
     * APPID
     */
    private String appId;

    /**
     * 邮箱编码
     */
    private String mailCode;

    /**
     * 访问用户数
     */
    private Integer userCount;

    /**
     * 访问总数
     */
    private Integer totalCount;

    /**
     * 访问成功数
     */
    private Integer successCount;

    /**
     * 访问失败数
     */
    private Integer failCount;

    /**
     * 访问取消数
     */
    private Integer cancelCount;

    /**
     * 访问成功率
     */
    private BigDecimal successRate;

    /**
     * 访问失败率
     */
    private BigDecimal failRate;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 上次更新时间
     */
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

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getMailCode() {
        return mailCode;
    }

    public void setMailCode(String mailCode) {
        this.mailCode = mailCode;
    }

    public Integer getUserCount() {
        return userCount;
    }

    public void setUserCount(Integer userCount) {
        this.userCount = userCount;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(Integer successCount) {
        this.successCount = successCount;
    }

    public Integer getFailCount() {
        return failCount;
    }

    public void setFailCount(Integer failCount) {
        this.failCount = failCount;
    }

    public Integer getCancelCount() {
        return cancelCount;
    }

    public void setCancelCount(Integer cancelCount) {
        this.cancelCount = cancelCount;
    }

    public BigDecimal getSuccessRate() {
        return successRate;
    }

    public void setSuccessRate(BigDecimal successRate) {
        this.successRate = successRate;
    }

    public BigDecimal getFailRate() {
        return failRate;
    }

    public void setFailRate(BigDecimal failRate) {
        this.failRate = failRate;
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
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", dataTime=").append(dataTime);
        sb.append(", appId=").append(appId);
        sb.append(", mailCode=").append(mailCode);
        sb.append(", userCount=").append(userCount);
        sb.append(", totalCount=").append(totalCount);
        sb.append(", successCount=").append(successCount);
        sb.append(", failCount=").append(failCount);
        sb.append(", cancelCount=").append(cancelCount);
        sb.append(", successRate=").append(successRate);
        sb.append(", failRate=").append(failRate);
        sb.append(", createTime=").append(createTime);
        sb.append(", lastUpdateTime=").append(lastUpdateTime);
        sb.append("]");
        return sb.toString();
    }
}