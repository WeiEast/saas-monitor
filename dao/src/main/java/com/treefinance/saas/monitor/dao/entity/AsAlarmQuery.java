package com.treefinance.saas.monitor.dao.entity;

import java.io.Serializable;
import java.util.Date;

public class AsAlarmQuery implements Serializable {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column as_alarm_query.id
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    private Long id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column as_alarm_query.alarmId
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    private Long alarmId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column as_alarm_query.queryIndex
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    private Integer queryIndex;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column as_alarm_query.resultCode
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    private String resultCode;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column as_alarm_query.querySql
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    private String querySql;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column as_alarm_query.description
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    private String description;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column as_alarm_query.createTime
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    private Date createTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column as_alarm_query.lastUpdateTime
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    private Date lastUpdateTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table as_alarm_query
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column as_alarm_query.id
     *
     * @return the value of as_alarm_query.id
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column as_alarm_query.id
     *
     * @param id the value for as_alarm_query.id
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column as_alarm_query.alarmId
     *
     * @return the value of as_alarm_query.alarmId
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    public Long getAlarmId() {
        return alarmId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column as_alarm_query.alarmId
     *
     * @param alarmId the value for as_alarm_query.alarmId
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    public void setAlarmId(Long alarmId) {
        this.alarmId = alarmId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column as_alarm_query.queryIndex
     *
     * @return the value of as_alarm_query.queryIndex
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    public Integer getQueryIndex() {
        return queryIndex;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column as_alarm_query.queryIndex
     *
     * @param queryIndex the value for as_alarm_query.queryIndex
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    public void setQueryIndex(Integer queryIndex) {
        this.queryIndex = queryIndex;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column as_alarm_query.resultCode
     *
     * @return the value of as_alarm_query.resultCode
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    public String getResultCode() {
        return resultCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column as_alarm_query.resultCode
     *
     * @param resultCode the value for as_alarm_query.resultCode
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    public void setResultCode(String resultCode) {
        this.resultCode = resultCode == null ? null : resultCode.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column as_alarm_query.querySql
     *
     * @return the value of as_alarm_query.querySql
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    public String getQuerySql() {
        return querySql;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column as_alarm_query.querySql
     *
     * @param querySql the value for as_alarm_query.querySql
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    public void setQuerySql(String querySql) {
        this.querySql = querySql == null ? null : querySql.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column as_alarm_query.description
     *
     * @return the value of as_alarm_query.description
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    public String getDescription() {
        return description;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column as_alarm_query.description
     *
     * @param description the value for as_alarm_query.description
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column as_alarm_query.createTime
     *
     * @return the value of as_alarm_query.createTime
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column as_alarm_query.createTime
     *
     * @param createTime the value for as_alarm_query.createTime
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column as_alarm_query.lastUpdateTime
     *
     * @return the value of as_alarm_query.lastUpdateTime
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column as_alarm_query.lastUpdateTime
     *
     * @param lastUpdateTime the value for as_alarm_query.lastUpdateTime
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_alarm_query
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", alarmId=").append(alarmId);
        sb.append(", queryIndex=").append(queryIndex);
        sb.append(", resultCode=").append(resultCode);
        sb.append(", querySql=").append(querySql);
        sb.append(", description=").append(description);
        sb.append(", createTime=").append(createTime);
        sb.append(", lastUpdateTime=").append(lastUpdateTime);
        sb.append("]");
        return sb.toString();
    }
}