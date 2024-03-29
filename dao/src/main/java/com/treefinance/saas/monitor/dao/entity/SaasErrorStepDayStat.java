package com.treefinance.saas.monitor.dao.entity;

import java.io.Serializable;
import java.util.Date;

public class SaasErrorStepDayStat implements Serializable {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column saas_error_step_day_stat.id
     *
     * @mbg.generated Wed Aug 23 13:49:15 CST 2017
     */
    private Long id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column saas_error_step_day_stat.dataTime
     *
     * @mbg.generated Wed Aug 23 13:49:15 CST 2017
     */
    private Date dataTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column saas_error_step_day_stat.dataType
     *
     * @mbg.generated Wed Aug 23 13:49:15 CST 2017
     */
    private Byte dataType;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column saas_error_step_day_stat.errorStepCode
     *
     * @mbg.generated Wed Aug 23 13:49:15 CST 2017
     */
    private String errorStepCode;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column saas_error_step_day_stat.failCount
     *
     * @mbg.generated Wed Aug 23 13:49:15 CST 2017
     */
    private Integer failCount;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column saas_error_step_day_stat.createTime
     *
     * @mbg.generated Wed Aug 23 13:49:15 CST 2017
     */
    private Date createTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column saas_error_step_day_stat.lastUpdateTime
     *
     * @mbg.generated Wed Aug 23 13:49:15 CST 2017
     */
    private Date lastUpdateTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table saas_error_step_day_stat
     *
     * @mbg.generated Wed Aug 23 13:49:15 CST 2017
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column saas_error_step_day_stat.id
     *
     * @return the value of saas_error_step_day_stat.id
     *
     * @mbg.generated Wed Aug 23 13:49:15 CST 2017
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column saas_error_step_day_stat.id
     *
     * @param id the value for saas_error_step_day_stat.id
     *
     * @mbg.generated Wed Aug 23 13:49:15 CST 2017
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column saas_error_step_day_stat.dataTime
     *
     * @return the value of saas_error_step_day_stat.dataTime
     *
     * @mbg.generated Wed Aug 23 13:49:15 CST 2017
     */
    public Date getDataTime() {
        return dataTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column saas_error_step_day_stat.dataTime
     *
     * @param dataTime the value for saas_error_step_day_stat.dataTime
     *
     * @mbg.generated Wed Aug 23 13:49:15 CST 2017
     */
    public void setDataTime(Date dataTime) {
        this.dataTime = dataTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column saas_error_step_day_stat.dataType
     *
     * @return the value of saas_error_step_day_stat.dataType
     *
     * @mbg.generated Wed Aug 23 13:49:15 CST 2017
     */
    public Byte getDataType() {
        return dataType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column saas_error_step_day_stat.dataType
     *
     * @param dataType the value for saas_error_step_day_stat.dataType
     *
     * @mbg.generated Wed Aug 23 13:49:15 CST 2017
     */
    public void setDataType(Byte dataType) {
        this.dataType = dataType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column saas_error_step_day_stat.errorStepCode
     *
     * @return the value of saas_error_step_day_stat.errorStepCode
     *
     * @mbg.generated Wed Aug 23 13:49:15 CST 2017
     */
    public String getErrorStepCode() {
        return errorStepCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column saas_error_step_day_stat.errorStepCode
     *
     * @param errorStepCode the value for saas_error_step_day_stat.errorStepCode
     *
     * @mbg.generated Wed Aug 23 13:49:15 CST 2017
     */
    public void setErrorStepCode(String errorStepCode) {
        this.errorStepCode = errorStepCode == null ? null : errorStepCode.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column saas_error_step_day_stat.failCount
     *
     * @return the value of saas_error_step_day_stat.failCount
     *
     * @mbg.generated Wed Aug 23 13:49:15 CST 2017
     */
    public Integer getFailCount() {
        return failCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column saas_error_step_day_stat.failCount
     *
     * @param failCount the value for saas_error_step_day_stat.failCount
     *
     * @mbg.generated Wed Aug 23 13:49:15 CST 2017
     */
    public void setFailCount(Integer failCount) {
        this.failCount = failCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column saas_error_step_day_stat.createTime
     *
     * @return the value of saas_error_step_day_stat.createTime
     *
     * @mbg.generated Wed Aug 23 13:49:15 CST 2017
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column saas_error_step_day_stat.createTime
     *
     * @param createTime the value for saas_error_step_day_stat.createTime
     *
     * @mbg.generated Wed Aug 23 13:49:15 CST 2017
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column saas_error_step_day_stat.lastUpdateTime
     *
     * @return the value of saas_error_step_day_stat.lastUpdateTime
     *
     * @mbg.generated Wed Aug 23 13:49:15 CST 2017
     */
    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column saas_error_step_day_stat.lastUpdateTime
     *
     * @param lastUpdateTime the value for saas_error_step_day_stat.lastUpdateTime
     *
     * @mbg.generated Wed Aug 23 13:49:15 CST 2017
     */
    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table saas_error_step_day_stat
     *
     * @mbg.generated Wed Aug 23 13:49:15 CST 2017
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
        sb.append(", errorStepCode=").append(errorStepCode);
        sb.append(", failCount=").append(failCount);
        sb.append(", createTime=").append(createTime);
        sb.append(", lastUpdateTime=").append(lastUpdateTime);
        sb.append("]");
        return sb.toString();
    }
}