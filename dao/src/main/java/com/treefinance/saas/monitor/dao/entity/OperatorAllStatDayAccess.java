package com.treefinance.saas.monitor.dao.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class OperatorAllStatDayAccess implements Serializable {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column operator_all_stat_day_access.id
     *
     * @mbg.generated Thu Nov 09 15:42:43 CST 2017
     */
    private Long id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column operator_all_stat_day_access.dataTime
     *
     * @mbg.generated Thu Nov 09 15:42:43 CST 2017
     */
    private Date dataTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column operator_all_stat_day_access.entryCount
     *
     * @mbg.generated Thu Nov 09 15:42:43 CST 2017
     */
    private Integer entryCount;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column operator_all_stat_day_access.confirmMobileCount
     *
     * @mbg.generated Thu Nov 09 15:42:43 CST 2017
     */
    private Integer confirmMobileCount;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column operator_all_stat_day_access.startLoginCount
     *
     * @mbg.generated Thu Nov 09 15:42:43 CST 2017
     */
    private Integer startLoginCount;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column operator_all_stat_day_access.loginSuccessCount
     *
     * @mbg.generated Thu Nov 09 15:42:43 CST 2017
     */
    private Integer loginSuccessCount;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column operator_all_stat_day_access.crawlSuccessCount
     *
     * @mbg.generated Thu Nov 09 15:42:43 CST 2017
     */
    private Integer crawlSuccessCount;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column operator_all_stat_day_access.processSuccessCount
     *
     * @mbg.generated Thu Nov 09 15:42:43 CST 2017
     */
    private Integer processSuccessCount;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column operator_all_stat_day_access.callbackSuccessCount
     *
     * @mbg.generated Thu Nov 09 15:42:43 CST 2017
     */
    private Integer callbackSuccessCount;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column operator_all_stat_day_access.confirmMobileConversionRate
     *
     * @mbg.generated Thu Nov 09 15:42:43 CST 2017
     */
    private BigDecimal confirmMobileConversionRate;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column operator_all_stat_day_access.loginConversionRate
     *
     * @mbg.generated Thu Nov 09 15:42:43 CST 2017
     */
    private BigDecimal loginConversionRate;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column operator_all_stat_day_access.loginSuccessRate
     *
     * @mbg.generated Thu Nov 09 15:42:43 CST 2017
     */
    private BigDecimal loginSuccessRate;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column operator_all_stat_day_access.crawlSuccessRate
     *
     * @mbg.generated Thu Nov 09 15:42:43 CST 2017
     */
    private BigDecimal crawlSuccessRate;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column operator_all_stat_day_access.processSuccessRate
     *
     * @mbg.generated Thu Nov 09 15:42:43 CST 2017
     */
    private BigDecimal processSuccessRate;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column operator_all_stat_day_access.callbackSuccessRate
     *
     * @mbg.generated Thu Nov 09 15:42:43 CST 2017
     */
    private BigDecimal callbackSuccessRate;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column operator_all_stat_day_access.createTime
     *
     * @mbg.generated Thu Nov 09 15:42:43 CST 2017
     */
    private Date createTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column operator_all_stat_day_access.lastUpdateTime
     *
     * @mbg.generated Thu Nov 09 15:42:43 CST 2017
     */
    private Date lastUpdateTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table operator_all_stat_day_access
     *
     * @mbg.generated Thu Nov 09 15:42:43 CST 2017
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column operator_all_stat_day_access.id
     *
     * @return the value of operator_all_stat_day_access.id
     *
     * @mbg.generated Thu Nov 09 15:42:43 CST 2017
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column operator_all_stat_day_access.id
     *
     * @param id the value for operator_all_stat_day_access.id
     *
     * @mbg.generated Thu Nov 09 15:42:43 CST 2017
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column operator_all_stat_day_access.dataTime
     *
     * @return the value of operator_all_stat_day_access.dataTime
     *
     * @mbg.generated Thu Nov 09 15:42:43 CST 2017
     */
    public Date getDataTime() {
        return dataTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column operator_all_stat_day_access.dataTime
     *
     * @param dataTime the value for operator_all_stat_day_access.dataTime
     *
     * @mbg.generated Thu Nov 09 15:42:43 CST 2017
     */
    public void setDataTime(Date dataTime) {
        this.dataTime = dataTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column operator_all_stat_day_access.entryCount
     *
     * @return the value of operator_all_stat_day_access.entryCount
     *
     * @mbg.generated Thu Nov 09 15:42:43 CST 2017
     */
    public Integer getEntryCount() {
        return entryCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column operator_all_stat_day_access.entryCount
     *
     * @param entryCount the value for operator_all_stat_day_access.entryCount
     *
     * @mbg.generated Thu Nov 09 15:42:43 CST 2017
     */
    public void setEntryCount(Integer entryCount) {
        this.entryCount = entryCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column operator_all_stat_day_access.confirmMobileCount
     *
     * @return the value of operator_all_stat_day_access.confirmMobileCount
     *
     * @mbg.generated Thu Nov 09 15:42:43 CST 2017
     */
    public Integer getConfirmMobileCount() {
        return confirmMobileCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column operator_all_stat_day_access.confirmMobileCount
     *
     * @param confirmMobileCount the value for operator_all_stat_day_access.confirmMobileCount
     *
     * @mbg.generated Thu Nov 09 15:42:43 CST 2017
     */
    public void setConfirmMobileCount(Integer confirmMobileCount) {
        this.confirmMobileCount = confirmMobileCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column operator_all_stat_day_access.startLoginCount
     *
     * @return the value of operator_all_stat_day_access.startLoginCount
     *
     * @mbg.generated Thu Nov 09 15:42:43 CST 2017
     */
    public Integer getStartLoginCount() {
        return startLoginCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column operator_all_stat_day_access.startLoginCount
     *
     * @param startLoginCount the value for operator_all_stat_day_access.startLoginCount
     *
     * @mbg.generated Thu Nov 09 15:42:43 CST 2017
     */
    public void setStartLoginCount(Integer startLoginCount) {
        this.startLoginCount = startLoginCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column operator_all_stat_day_access.loginSuccessCount
     *
     * @return the value of operator_all_stat_day_access.loginSuccessCount
     *
     * @mbg.generated Thu Nov 09 15:42:43 CST 2017
     */
    public Integer getLoginSuccessCount() {
        return loginSuccessCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column operator_all_stat_day_access.loginSuccessCount
     *
     * @param loginSuccessCount the value for operator_all_stat_day_access.loginSuccessCount
     *
     * @mbg.generated Thu Nov 09 15:42:43 CST 2017
     */
    public void setLoginSuccessCount(Integer loginSuccessCount) {
        this.loginSuccessCount = loginSuccessCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column operator_all_stat_day_access.crawlSuccessCount
     *
     * @return the value of operator_all_stat_day_access.crawlSuccessCount
     *
     * @mbg.generated Thu Nov 09 15:42:43 CST 2017
     */
    public Integer getCrawlSuccessCount() {
        return crawlSuccessCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column operator_all_stat_day_access.crawlSuccessCount
     *
     * @param crawlSuccessCount the value for operator_all_stat_day_access.crawlSuccessCount
     *
     * @mbg.generated Thu Nov 09 15:42:43 CST 2017
     */
    public void setCrawlSuccessCount(Integer crawlSuccessCount) {
        this.crawlSuccessCount = crawlSuccessCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column operator_all_stat_day_access.processSuccessCount
     *
     * @return the value of operator_all_stat_day_access.processSuccessCount
     *
     * @mbg.generated Thu Nov 09 15:42:43 CST 2017
     */
    public Integer getProcessSuccessCount() {
        return processSuccessCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column operator_all_stat_day_access.processSuccessCount
     *
     * @param processSuccessCount the value for operator_all_stat_day_access.processSuccessCount
     *
     * @mbg.generated Thu Nov 09 15:42:43 CST 2017
     */
    public void setProcessSuccessCount(Integer processSuccessCount) {
        this.processSuccessCount = processSuccessCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column operator_all_stat_day_access.callbackSuccessCount
     *
     * @return the value of operator_all_stat_day_access.callbackSuccessCount
     *
     * @mbg.generated Thu Nov 09 15:42:43 CST 2017
     */
    public Integer getCallbackSuccessCount() {
        return callbackSuccessCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column operator_all_stat_day_access.callbackSuccessCount
     *
     * @param callbackSuccessCount the value for operator_all_stat_day_access.callbackSuccessCount
     *
     * @mbg.generated Thu Nov 09 15:42:43 CST 2017
     */
    public void setCallbackSuccessCount(Integer callbackSuccessCount) {
        this.callbackSuccessCount = callbackSuccessCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column operator_all_stat_day_access.confirmMobileConversionRate
     *
     * @return the value of operator_all_stat_day_access.confirmMobileConversionRate
     *
     * @mbg.generated Thu Nov 09 15:42:43 CST 2017
     */
    public BigDecimal getConfirmMobileConversionRate() {
        return confirmMobileConversionRate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column operator_all_stat_day_access.confirmMobileConversionRate
     *
     * @param confirmMobileConversionRate the value for operator_all_stat_day_access.confirmMobileConversionRate
     *
     * @mbg.generated Thu Nov 09 15:42:43 CST 2017
     */
    public void setConfirmMobileConversionRate(BigDecimal confirmMobileConversionRate) {
        this.confirmMobileConversionRate = confirmMobileConversionRate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column operator_all_stat_day_access.loginConversionRate
     *
     * @return the value of operator_all_stat_day_access.loginConversionRate
     *
     * @mbg.generated Thu Nov 09 15:42:43 CST 2017
     */
    public BigDecimal getLoginConversionRate() {
        return loginConversionRate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column operator_all_stat_day_access.loginConversionRate
     *
     * @param loginConversionRate the value for operator_all_stat_day_access.loginConversionRate
     *
     * @mbg.generated Thu Nov 09 15:42:43 CST 2017
     */
    public void setLoginConversionRate(BigDecimal loginConversionRate) {
        this.loginConversionRate = loginConversionRate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column operator_all_stat_day_access.loginSuccessRate
     *
     * @return the value of operator_all_stat_day_access.loginSuccessRate
     *
     * @mbg.generated Thu Nov 09 15:42:43 CST 2017
     */
    public BigDecimal getLoginSuccessRate() {
        return loginSuccessRate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column operator_all_stat_day_access.loginSuccessRate
     *
     * @param loginSuccessRate the value for operator_all_stat_day_access.loginSuccessRate
     *
     * @mbg.generated Thu Nov 09 15:42:43 CST 2017
     */
    public void setLoginSuccessRate(BigDecimal loginSuccessRate) {
        this.loginSuccessRate = loginSuccessRate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column operator_all_stat_day_access.crawlSuccessRate
     *
     * @return the value of operator_all_stat_day_access.crawlSuccessRate
     *
     * @mbg.generated Thu Nov 09 15:42:43 CST 2017
     */
    public BigDecimal getCrawlSuccessRate() {
        return crawlSuccessRate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column operator_all_stat_day_access.crawlSuccessRate
     *
     * @param crawlSuccessRate the value for operator_all_stat_day_access.crawlSuccessRate
     *
     * @mbg.generated Thu Nov 09 15:42:43 CST 2017
     */
    public void setCrawlSuccessRate(BigDecimal crawlSuccessRate) {
        this.crawlSuccessRate = crawlSuccessRate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column operator_all_stat_day_access.processSuccessRate
     *
     * @return the value of operator_all_stat_day_access.processSuccessRate
     *
     * @mbg.generated Thu Nov 09 15:42:43 CST 2017
     */
    public BigDecimal getProcessSuccessRate() {
        return processSuccessRate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column operator_all_stat_day_access.processSuccessRate
     *
     * @param processSuccessRate the value for operator_all_stat_day_access.processSuccessRate
     *
     * @mbg.generated Thu Nov 09 15:42:43 CST 2017
     */
    public void setProcessSuccessRate(BigDecimal processSuccessRate) {
        this.processSuccessRate = processSuccessRate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column operator_all_stat_day_access.callbackSuccessRate
     *
     * @return the value of operator_all_stat_day_access.callbackSuccessRate
     *
     * @mbg.generated Thu Nov 09 15:42:43 CST 2017
     */
    public BigDecimal getCallbackSuccessRate() {
        return callbackSuccessRate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column operator_all_stat_day_access.callbackSuccessRate
     *
     * @param callbackSuccessRate the value for operator_all_stat_day_access.callbackSuccessRate
     *
     * @mbg.generated Thu Nov 09 15:42:43 CST 2017
     */
    public void setCallbackSuccessRate(BigDecimal callbackSuccessRate) {
        this.callbackSuccessRate = callbackSuccessRate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column operator_all_stat_day_access.createTime
     *
     * @return the value of operator_all_stat_day_access.createTime
     *
     * @mbg.generated Thu Nov 09 15:42:43 CST 2017
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column operator_all_stat_day_access.createTime
     *
     * @param createTime the value for operator_all_stat_day_access.createTime
     *
     * @mbg.generated Thu Nov 09 15:42:43 CST 2017
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column operator_all_stat_day_access.lastUpdateTime
     *
     * @return the value of operator_all_stat_day_access.lastUpdateTime
     *
     * @mbg.generated Thu Nov 09 15:42:43 CST 2017
     */
    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column operator_all_stat_day_access.lastUpdateTime
     *
     * @param lastUpdateTime the value for operator_all_stat_day_access.lastUpdateTime
     *
     * @mbg.generated Thu Nov 09 15:42:43 CST 2017
     */
    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_all_stat_day_access
     *
     * @mbg.generated Thu Nov 09 15:42:43 CST 2017
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", dataTime=").append(dataTime);
        sb.append(", entryCount=").append(entryCount);
        sb.append(", confirmMobileCount=").append(confirmMobileCount);
        sb.append(", startLoginCount=").append(startLoginCount);
        sb.append(", loginSuccessCount=").append(loginSuccessCount);
        sb.append(", crawlSuccessCount=").append(crawlSuccessCount);
        sb.append(", processSuccessCount=").append(processSuccessCount);
        sb.append(", callbackSuccessCount=").append(callbackSuccessCount);
        sb.append(", confirmMobileConversionRate=").append(confirmMobileConversionRate);
        sb.append(", loginConversionRate=").append(loginConversionRate);
        sb.append(", loginSuccessRate=").append(loginSuccessRate);
        sb.append(", crawlSuccessRate=").append(crawlSuccessRate);
        sb.append(", processSuccessRate=").append(processSuccessRate);
        sb.append(", callbackSuccessRate=").append(callbackSuccessRate);
        sb.append(", createTime=").append(createTime);
        sb.append(", lastUpdateTime=").append(lastUpdateTime);
        sb.append("]");
        return sb.toString();
    }
}