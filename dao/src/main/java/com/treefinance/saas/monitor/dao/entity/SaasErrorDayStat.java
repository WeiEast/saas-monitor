package com.treefinance.saas.monitor.dao.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class SaasErrorDayStat implements Serializable {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column saas_error_day_stat.id
     *
     * @mbg.generated Tue Aug 22 16:40:50 CST 2017
     */
    private Long id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column saas_error_day_stat.dataTime
     *
     * @mbg.generated Tue Aug 22 16:40:50 CST 2017
     */
    private Date dataTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column saas_error_day_stat.dataType
     *
     * @mbg.generated Tue Aug 22 16:40:50 CST 2017
     */
    private Byte dataType;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column saas_error_day_stat.errorCode
     *
     * @mbg.generated Tue Aug 22 16:40:50 CST 2017
     */
    private String errorCode;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column saas_error_day_stat.errorMsg
     *
     * @mbg.generated Tue Aug 22 16:40:50 CST 2017
     */
    private String errorMsg;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column saas_error_day_stat.totalCount
     *
     * @mbg.generated Tue Aug 22 16:40:50 CST 2017
     */
    private Integer totalCount;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column saas_error_day_stat.failCount
     *
     * @mbg.generated Tue Aug 22 16:40:50 CST 2017
     */
    private Integer failCount;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column saas_error_day_stat.cancelCount
     *
     * @mbg.generated Tue Aug 22 16:40:50 CST 2017
     */
    private Integer cancelCount;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column saas_error_day_stat.failRate
     *
     * @mbg.generated Tue Aug 22 16:40:50 CST 2017
     */
    private BigDecimal failRate;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column saas_error_day_stat.cancelRate
     *
     * @mbg.generated Tue Aug 22 16:40:50 CST 2017
     */
    private BigDecimal cancelRate;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column saas_error_day_stat.createTime
     *
     * @mbg.generated Tue Aug 22 16:40:50 CST 2017
     */
    private Date createTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column saas_error_day_stat.lastUpdateTime
     *
     * @mbg.generated Tue Aug 22 16:40:50 CST 2017
     */
    private Date lastUpdateTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table saas_error_day_stat
     *
     * @mbg.generated Tue Aug 22 16:40:50 CST 2017
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column saas_error_day_stat.id
     *
     * @return the value of saas_error_day_stat.id
     *
     * @mbg.generated Tue Aug 22 16:40:50 CST 2017
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column saas_error_day_stat.id
     *
     * @param id the value for saas_error_day_stat.id
     *
     * @mbg.generated Tue Aug 22 16:40:50 CST 2017
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column saas_error_day_stat.dataTime
     *
     * @return the value of saas_error_day_stat.dataTime
     *
     * @mbg.generated Tue Aug 22 16:40:50 CST 2017
     */
    public Date getDataTime() {
        return dataTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column saas_error_day_stat.dataTime
     *
     * @param dataTime the value for saas_error_day_stat.dataTime
     *
     * @mbg.generated Tue Aug 22 16:40:50 CST 2017
     */
    public void setDataTime(Date dataTime) {
        this.dataTime = dataTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column saas_error_day_stat.dataType
     *
     * @return the value of saas_error_day_stat.dataType
     *
     * @mbg.generated Tue Aug 22 16:40:50 CST 2017
     */
    public Byte getDataType() {
        return dataType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column saas_error_day_stat.dataType
     *
     * @param dataType the value for saas_error_day_stat.dataType
     *
     * @mbg.generated Tue Aug 22 16:40:50 CST 2017
     */
    public void setDataType(Byte dataType) {
        this.dataType = dataType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column saas_error_day_stat.errorCode
     *
     * @return the value of saas_error_day_stat.errorCode
     *
     * @mbg.generated Tue Aug 22 16:40:50 CST 2017
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column saas_error_day_stat.errorCode
     *
     * @param errorCode the value for saas_error_day_stat.errorCode
     *
     * @mbg.generated Tue Aug 22 16:40:50 CST 2017
     */
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode == null ? null : errorCode.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column saas_error_day_stat.errorMsg
     *
     * @return the value of saas_error_day_stat.errorMsg
     *
     * @mbg.generated Tue Aug 22 16:40:50 CST 2017
     */
    public String getErrorMsg() {
        return errorMsg;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column saas_error_day_stat.errorMsg
     *
     * @param errorMsg the value for saas_error_day_stat.errorMsg
     *
     * @mbg.generated Tue Aug 22 16:40:50 CST 2017
     */
    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg == null ? null : errorMsg.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column saas_error_day_stat.totalCount
     *
     * @return the value of saas_error_day_stat.totalCount
     *
     * @mbg.generated Tue Aug 22 16:40:50 CST 2017
     */
    public Integer getTotalCount() {
        return totalCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column saas_error_day_stat.totalCount
     *
     * @param totalCount the value for saas_error_day_stat.totalCount
     *
     * @mbg.generated Tue Aug 22 16:40:50 CST 2017
     */
    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column saas_error_day_stat.failCount
     *
     * @return the value of saas_error_day_stat.failCount
     *
     * @mbg.generated Tue Aug 22 16:40:50 CST 2017
     */
    public Integer getFailCount() {
        return failCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column saas_error_day_stat.failCount
     *
     * @param failCount the value for saas_error_day_stat.failCount
     *
     * @mbg.generated Tue Aug 22 16:40:50 CST 2017
     */
    public void setFailCount(Integer failCount) {
        this.failCount = failCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column saas_error_day_stat.cancelCount
     *
     * @return the value of saas_error_day_stat.cancelCount
     *
     * @mbg.generated Tue Aug 22 16:40:50 CST 2017
     */
    public Integer getCancelCount() {
        return cancelCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column saas_error_day_stat.cancelCount
     *
     * @param cancelCount the value for saas_error_day_stat.cancelCount
     *
     * @mbg.generated Tue Aug 22 16:40:50 CST 2017
     */
    public void setCancelCount(Integer cancelCount) {
        this.cancelCount = cancelCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column saas_error_day_stat.failRate
     *
     * @return the value of saas_error_day_stat.failRate
     *
     * @mbg.generated Tue Aug 22 16:40:50 CST 2017
     */
    public BigDecimal getFailRate() {
        return failRate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column saas_error_day_stat.failRate
     *
     * @param failRate the value for saas_error_day_stat.failRate
     *
     * @mbg.generated Tue Aug 22 16:40:50 CST 2017
     */
    public void setFailRate(BigDecimal failRate) {
        this.failRate = failRate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column saas_error_day_stat.cancelRate
     *
     * @return the value of saas_error_day_stat.cancelRate
     *
     * @mbg.generated Tue Aug 22 16:40:50 CST 2017
     */
    public BigDecimal getCancelRate() {
        return cancelRate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column saas_error_day_stat.cancelRate
     *
     * @param cancelRate the value for saas_error_day_stat.cancelRate
     *
     * @mbg.generated Tue Aug 22 16:40:50 CST 2017
     */
    public void setCancelRate(BigDecimal cancelRate) {
        this.cancelRate = cancelRate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column saas_error_day_stat.createTime
     *
     * @return the value of saas_error_day_stat.createTime
     *
     * @mbg.generated Tue Aug 22 16:40:50 CST 2017
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column saas_error_day_stat.createTime
     *
     * @param createTime the value for saas_error_day_stat.createTime
     *
     * @mbg.generated Tue Aug 22 16:40:50 CST 2017
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column saas_error_day_stat.lastUpdateTime
     *
     * @return the value of saas_error_day_stat.lastUpdateTime
     *
     * @mbg.generated Tue Aug 22 16:40:50 CST 2017
     */
    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column saas_error_day_stat.lastUpdateTime
     *
     * @param lastUpdateTime the value for saas_error_day_stat.lastUpdateTime
     *
     * @mbg.generated Tue Aug 22 16:40:50 CST 2017
     */
    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table saas_error_day_stat
     *
     * @mbg.generated Tue Aug 22 16:40:50 CST 2017
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
        sb.append(", errorCode=").append(errorCode);
        sb.append(", errorMsg=").append(errorMsg);
        sb.append(", totalCount=").append(totalCount);
        sb.append(", failCount=").append(failCount);
        sb.append(", cancelCount=").append(cancelCount);
        sb.append(", failRate=").append(failRate);
        sb.append(", cancelRate=").append(cancelRate);
        sb.append(", createTime=").append(createTime);
        sb.append(", lastUpdateTime=").append(lastUpdateTime);
        sb.append("]");
        return sb.toString();
    }
}