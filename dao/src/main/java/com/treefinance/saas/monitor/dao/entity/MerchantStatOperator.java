package com.treefinance.saas.monitor.dao.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class MerchantStatOperator implements Serializable {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column merchant_stat_operator.id
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    private Long id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column merchant_stat_operator.dataTime
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    private Date dataTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column merchant_stat_operator.appId
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    private String appId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column merchant_stat_operator.operaterId
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    private String operaterId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column merchant_stat_operator.userCount
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    private Integer userCount;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column merchant_stat_operator.totalCount
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    private Integer totalCount;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column merchant_stat_operator.successCount
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    private Integer successCount;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column merchant_stat_operator.failCount
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    private Integer failCount;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column merchant_stat_operator.cancelCount
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    private Integer cancelCount;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column merchant_stat_operator.successRate
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    private BigDecimal successRate;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column merchant_stat_operator.failRate
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    private BigDecimal failRate;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column merchant_stat_operator.createTime
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    private Date createTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column merchant_stat_operator.lastUpdateTime
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    private Date lastUpdateTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table merchant_stat_operator
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column merchant_stat_operator.id
     *
     * @return the value of merchant_stat_operator.id
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column merchant_stat_operator.id
     *
     * @param id the value for merchant_stat_operator.id
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column merchant_stat_operator.dataTime
     *
     * @return the value of merchant_stat_operator.dataTime
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    public Date getDataTime() {
        return dataTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column merchant_stat_operator.dataTime
     *
     * @param dataTime the value for merchant_stat_operator.dataTime
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    public void setDataTime(Date dataTime) {
        this.dataTime = dataTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column merchant_stat_operator.appId
     *
     * @return the value of merchant_stat_operator.appId
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    public String getAppId() {
        return appId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column merchant_stat_operator.appId
     *
     * @param appId the value for merchant_stat_operator.appId
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    public void setAppId(String appId) {
        this.appId = appId == null ? null : appId.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column merchant_stat_operator.operaterId
     *
     * @return the value of merchant_stat_operator.operaterId
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    public String getOperaterId() {
        return operaterId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column merchant_stat_operator.operaterId
     *
     * @param operaterId the value for merchant_stat_operator.operaterId
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    public void setOperaterId(String operaterId) {
        this.operaterId = operaterId == null ? null : operaterId.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column merchant_stat_operator.userCount
     *
     * @return the value of merchant_stat_operator.userCount
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    public Integer getUserCount() {
        return userCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column merchant_stat_operator.userCount
     *
     * @param userCount the value for merchant_stat_operator.userCount
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    public void setUserCount(Integer userCount) {
        this.userCount = userCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column merchant_stat_operator.totalCount
     *
     * @return the value of merchant_stat_operator.totalCount
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    public Integer getTotalCount() {
        return totalCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column merchant_stat_operator.totalCount
     *
     * @param totalCount the value for merchant_stat_operator.totalCount
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column merchant_stat_operator.successCount
     *
     * @return the value of merchant_stat_operator.successCount
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    public Integer getSuccessCount() {
        return successCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column merchant_stat_operator.successCount
     *
     * @param successCount the value for merchant_stat_operator.successCount
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    public void setSuccessCount(Integer successCount) {
        this.successCount = successCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column merchant_stat_operator.failCount
     *
     * @return the value of merchant_stat_operator.failCount
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    public Integer getFailCount() {
        return failCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column merchant_stat_operator.failCount
     *
     * @param failCount the value for merchant_stat_operator.failCount
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    public void setFailCount(Integer failCount) {
        this.failCount = failCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column merchant_stat_operator.cancelCount
     *
     * @return the value of merchant_stat_operator.cancelCount
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    public Integer getCancelCount() {
        return cancelCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column merchant_stat_operator.cancelCount
     *
     * @param cancelCount the value for merchant_stat_operator.cancelCount
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    public void setCancelCount(Integer cancelCount) {
        this.cancelCount = cancelCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column merchant_stat_operator.successRate
     *
     * @return the value of merchant_stat_operator.successRate
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    public BigDecimal getSuccessRate() {
        return successRate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column merchant_stat_operator.successRate
     *
     * @param successRate the value for merchant_stat_operator.successRate
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    public void setSuccessRate(BigDecimal successRate) {
        this.successRate = successRate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column merchant_stat_operator.failRate
     *
     * @return the value of merchant_stat_operator.failRate
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    public BigDecimal getFailRate() {
        return failRate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column merchant_stat_operator.failRate
     *
     * @param failRate the value for merchant_stat_operator.failRate
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    public void setFailRate(BigDecimal failRate) {
        this.failRate = failRate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column merchant_stat_operator.createTime
     *
     * @return the value of merchant_stat_operator.createTime
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column merchant_stat_operator.createTime
     *
     * @param createTime the value for merchant_stat_operator.createTime
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column merchant_stat_operator.lastUpdateTime
     *
     * @return the value of merchant_stat_operator.lastUpdateTime
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column merchant_stat_operator.lastUpdateTime
     *
     * @param lastUpdateTime the value for merchant_stat_operator.lastUpdateTime
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table merchant_stat_operator
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", dataTime=").append(dataTime);
        sb.append(", appId=").append(appId);
        sb.append(", operaterId=").append(operaterId);
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