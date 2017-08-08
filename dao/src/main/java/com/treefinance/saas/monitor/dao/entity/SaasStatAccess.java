package com.treefinance.saas.monitor.dao.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class SaasStatAccess implements Serializable {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column saas_stat_access.id
     *
     * @mbg.generated Tue Aug 08 17:39:18 CST 2017
     */
    private Long id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column saas_stat_access.dataTime
     *
     * @mbg.generated Tue Aug 08 17:39:18 CST 2017
     */
    private Date dataTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column saas_stat_access.dataType
     *
     * @mbg.generated Tue Aug 08 17:39:18 CST 2017
     */
    private Byte dataType;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column saas_stat_access.userCount
     *
     * @mbg.generated Tue Aug 08 17:39:18 CST 2017
     */
    private Integer userCount;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column saas_stat_access.totalCount
     *
     * @mbg.generated Tue Aug 08 17:39:18 CST 2017
     */
    private Integer totalCount;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column saas_stat_access.successCount
     *
     * @mbg.generated Tue Aug 08 17:39:18 CST 2017
     */
    private Integer successCount;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column saas_stat_access.failCount
     *
     * @mbg.generated Tue Aug 08 17:39:18 CST 2017
     */
    private Integer failCount;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column saas_stat_access.cancelCount
     *
     * @mbg.generated Tue Aug 08 17:39:18 CST 2017
     */
    private Integer cancelCount;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column saas_stat_access.successRate
     *
     * @mbg.generated Tue Aug 08 17:39:18 CST 2017
     */
    private BigDecimal successRate;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column saas_stat_access.failRate
     *
     * @mbg.generated Tue Aug 08 17:39:18 CST 2017
     */
    private BigDecimal failRate;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column saas_stat_access.createTime
     *
     * @mbg.generated Tue Aug 08 17:39:18 CST 2017
     */
    private Date createTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column saas_stat_access.lastUpdateTime
     *
     * @mbg.generated Tue Aug 08 17:39:18 CST 2017
     */
    private Date lastUpdateTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table saas_stat_access
     *
     * @mbg.generated Tue Aug 08 17:39:18 CST 2017
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column saas_stat_access.id
     *
     * @return the value of saas_stat_access.id
     *
     * @mbg.generated Tue Aug 08 17:39:18 CST 2017
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column saas_stat_access.id
     *
     * @param id the value for saas_stat_access.id
     *
     * @mbg.generated Tue Aug 08 17:39:18 CST 2017
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column saas_stat_access.dataTime
     *
     * @return the value of saas_stat_access.dataTime
     *
     * @mbg.generated Tue Aug 08 17:39:18 CST 2017
     */
    public Date getDataTime() {
        return dataTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column saas_stat_access.dataTime
     *
     * @param dataTime the value for saas_stat_access.dataTime
     *
     * @mbg.generated Tue Aug 08 17:39:18 CST 2017
     */
    public void setDataTime(Date dataTime) {
        this.dataTime = dataTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column saas_stat_access.dataType
     *
     * @return the value of saas_stat_access.dataType
     *
     * @mbg.generated Tue Aug 08 17:39:18 CST 2017
     */
    public Byte getDataType() {
        return dataType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column saas_stat_access.dataType
     *
     * @param dataType the value for saas_stat_access.dataType
     *
     * @mbg.generated Tue Aug 08 17:39:18 CST 2017
     */
    public void setDataType(Byte dataType) {
        this.dataType = dataType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column saas_stat_access.userCount
     *
     * @return the value of saas_stat_access.userCount
     *
     * @mbg.generated Tue Aug 08 17:39:18 CST 2017
     */
    public Integer getUserCount() {
        return userCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column saas_stat_access.userCount
     *
     * @param userCount the value for saas_stat_access.userCount
     *
     * @mbg.generated Tue Aug 08 17:39:18 CST 2017
     */
    public void setUserCount(Integer userCount) {
        this.userCount = userCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column saas_stat_access.totalCount
     *
     * @return the value of saas_stat_access.totalCount
     *
     * @mbg.generated Tue Aug 08 17:39:18 CST 2017
     */
    public Integer getTotalCount() {
        return totalCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column saas_stat_access.totalCount
     *
     * @param totalCount the value for saas_stat_access.totalCount
     *
     * @mbg.generated Tue Aug 08 17:39:18 CST 2017
     */
    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column saas_stat_access.successCount
     *
     * @return the value of saas_stat_access.successCount
     *
     * @mbg.generated Tue Aug 08 17:39:18 CST 2017
     */
    public Integer getSuccessCount() {
        return successCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column saas_stat_access.successCount
     *
     * @param successCount the value for saas_stat_access.successCount
     *
     * @mbg.generated Tue Aug 08 17:39:18 CST 2017
     */
    public void setSuccessCount(Integer successCount) {
        this.successCount = successCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column saas_stat_access.failCount
     *
     * @return the value of saas_stat_access.failCount
     *
     * @mbg.generated Tue Aug 08 17:39:18 CST 2017
     */
    public Integer getFailCount() {
        return failCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column saas_stat_access.failCount
     *
     * @param failCount the value for saas_stat_access.failCount
     *
     * @mbg.generated Tue Aug 08 17:39:18 CST 2017
     */
    public void setFailCount(Integer failCount) {
        this.failCount = failCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column saas_stat_access.cancelCount
     *
     * @return the value of saas_stat_access.cancelCount
     *
     * @mbg.generated Tue Aug 08 17:39:18 CST 2017
     */
    public Integer getCancelCount() {
        return cancelCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column saas_stat_access.cancelCount
     *
     * @param cancelCount the value for saas_stat_access.cancelCount
     *
     * @mbg.generated Tue Aug 08 17:39:18 CST 2017
     */
    public void setCancelCount(Integer cancelCount) {
        this.cancelCount = cancelCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column saas_stat_access.successRate
     *
     * @return the value of saas_stat_access.successRate
     *
     * @mbg.generated Tue Aug 08 17:39:18 CST 2017
     */
    public BigDecimal getSuccessRate() {
        return successRate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column saas_stat_access.successRate
     *
     * @param successRate the value for saas_stat_access.successRate
     *
     * @mbg.generated Tue Aug 08 17:39:18 CST 2017
     */
    public void setSuccessRate(BigDecimal successRate) {
        this.successRate = successRate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column saas_stat_access.failRate
     *
     * @return the value of saas_stat_access.failRate
     *
     * @mbg.generated Tue Aug 08 17:39:18 CST 2017
     */
    public BigDecimal getFailRate() {
        return failRate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column saas_stat_access.failRate
     *
     * @param failRate the value for saas_stat_access.failRate
     *
     * @mbg.generated Tue Aug 08 17:39:18 CST 2017
     */
    public void setFailRate(BigDecimal failRate) {
        this.failRate = failRate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column saas_stat_access.createTime
     *
     * @return the value of saas_stat_access.createTime
     *
     * @mbg.generated Tue Aug 08 17:39:18 CST 2017
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column saas_stat_access.createTime
     *
     * @param createTime the value for saas_stat_access.createTime
     *
     * @mbg.generated Tue Aug 08 17:39:18 CST 2017
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column saas_stat_access.lastUpdateTime
     *
     * @return the value of saas_stat_access.lastUpdateTime
     *
     * @mbg.generated Tue Aug 08 17:39:18 CST 2017
     */
    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column saas_stat_access.lastUpdateTime
     *
     * @param lastUpdateTime the value for saas_stat_access.lastUpdateTime
     *
     * @mbg.generated Tue Aug 08 17:39:18 CST 2017
     */
    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table saas_stat_access
     *
     * @mbg.generated Tue Aug 08 17:39:18 CST 2017
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", dataTime=").append(dataTime);
        sb.append(", dataType=").append(dataType);
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