package com.treefinance.saas.monitor.dao.entity;

import java.io.Serializable;
import java.util.Date;

public class CallbackFailureReasonStatDayAccess implements Serializable {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column callback_failure_reason_stat_day_access.id
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    private Long id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column callback_failure_reason_stat_day_access.appId
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    private String appId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column callback_failure_reason_stat_day_access.groupCode
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    private String groupCode;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column callback_failure_reason_stat_day_access.groupName
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    private String groupName;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column callback_failure_reason_stat_day_access.dataTime
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    private Date dataTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column callback_failure_reason_stat_day_access.dataType
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    private Byte dataType;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column callback_failure_reason_stat_day_access.bizType
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    private Byte bizType;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column callback_failure_reason_stat_day_access.saasEnv
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    private Byte saasEnv;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column callback_failure_reason_stat_day_access.totalCount
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    private Integer totalCount;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column callback_failure_reason_stat_day_access.unKnownReasonCount
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    private Integer unKnownReasonCount;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column callback_failure_reason_stat_day_access.personalReasonCount
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    private Integer personalReasonCount;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column callback_failure_reason_stat_day_access.createTime
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    private Date createTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column callback_failure_reason_stat_day_access.lastUpdateTime
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    private Date lastUpdateTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table callback_failure_reason_stat_day_access
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column callback_failure_reason_stat_day_access.id
     *
     * @return the value of callback_failure_reason_stat_day_access.id
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column callback_failure_reason_stat_day_access.id
     *
     * @param id the value for callback_failure_reason_stat_day_access.id
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column callback_failure_reason_stat_day_access.appId
     *
     * @return the value of callback_failure_reason_stat_day_access.appId
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    public String getAppId() {
        return appId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column callback_failure_reason_stat_day_access.appId
     *
     * @param appId the value for callback_failure_reason_stat_day_access.appId
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    public void setAppId(String appId) {
        this.appId = appId == null ? null : appId.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column callback_failure_reason_stat_day_access.groupCode
     *
     * @return the value of callback_failure_reason_stat_day_access.groupCode
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    public String getGroupCode() {
        return groupCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column callback_failure_reason_stat_day_access.groupCode
     *
     * @param groupCode the value for callback_failure_reason_stat_day_access.groupCode
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode == null ? null : groupCode.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column callback_failure_reason_stat_day_access.groupName
     *
     * @return the value of callback_failure_reason_stat_day_access.groupName
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column callback_failure_reason_stat_day_access.groupName
     *
     * @param groupName the value for callback_failure_reason_stat_day_access.groupName
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName == null ? null : groupName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column callback_failure_reason_stat_day_access.dataTime
     *
     * @return the value of callback_failure_reason_stat_day_access.dataTime
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    public Date getDataTime() {
        return dataTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column callback_failure_reason_stat_day_access.dataTime
     *
     * @param dataTime the value for callback_failure_reason_stat_day_access.dataTime
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    public void setDataTime(Date dataTime) {
        this.dataTime = dataTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column callback_failure_reason_stat_day_access.dataType
     *
     * @return the value of callback_failure_reason_stat_day_access.dataType
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    public Byte getDataType() {
        return dataType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column callback_failure_reason_stat_day_access.dataType
     *
     * @param dataType the value for callback_failure_reason_stat_day_access.dataType
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    public void setDataType(Byte dataType) {
        this.dataType = dataType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column callback_failure_reason_stat_day_access.bizType
     *
     * @return the value of callback_failure_reason_stat_day_access.bizType
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    public Byte getBizType() {
        return bizType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column callback_failure_reason_stat_day_access.bizType
     *
     * @param bizType the value for callback_failure_reason_stat_day_access.bizType
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    public void setBizType(Byte bizType) {
        this.bizType = bizType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column callback_failure_reason_stat_day_access.saasEnv
     *
     * @return the value of callback_failure_reason_stat_day_access.saasEnv
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    public Byte getSaasEnv() {
        return saasEnv;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column callback_failure_reason_stat_day_access.saasEnv
     *
     * @param saasEnv the value for callback_failure_reason_stat_day_access.saasEnv
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    public void setSaasEnv(Byte saasEnv) {
        this.saasEnv = saasEnv;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column callback_failure_reason_stat_day_access.totalCount
     *
     * @return the value of callback_failure_reason_stat_day_access.totalCount
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    public Integer getTotalCount() {
        return totalCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column callback_failure_reason_stat_day_access.totalCount
     *
     * @param totalCount the value for callback_failure_reason_stat_day_access.totalCount
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column callback_failure_reason_stat_day_access.unKnownReasonCount
     *
     * @return the value of callback_failure_reason_stat_day_access.unKnownReasonCount
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    public Integer getUnKnownReasonCount() {
        return unKnownReasonCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column callback_failure_reason_stat_day_access.unKnownReasonCount
     *
     * @param unKnownReasonCount the value for callback_failure_reason_stat_day_access.unKnownReasonCount
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    public void setUnKnownReasonCount(Integer unKnownReasonCount) {
        this.unKnownReasonCount = unKnownReasonCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column callback_failure_reason_stat_day_access.personalReasonCount
     *
     * @return the value of callback_failure_reason_stat_day_access.personalReasonCount
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    public Integer getPersonalReasonCount() {
        return personalReasonCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column callback_failure_reason_stat_day_access.personalReasonCount
     *
     * @param personalReasonCount the value for callback_failure_reason_stat_day_access.personalReasonCount
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    public void setPersonalReasonCount(Integer personalReasonCount) {
        this.personalReasonCount = personalReasonCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column callback_failure_reason_stat_day_access.createTime
     *
     * @return the value of callback_failure_reason_stat_day_access.createTime
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column callback_failure_reason_stat_day_access.createTime
     *
     * @param createTime the value for callback_failure_reason_stat_day_access.createTime
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column callback_failure_reason_stat_day_access.lastUpdateTime
     *
     * @return the value of callback_failure_reason_stat_day_access.lastUpdateTime
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column callback_failure_reason_stat_day_access.lastUpdateTime
     *
     * @param lastUpdateTime the value for callback_failure_reason_stat_day_access.lastUpdateTime
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table callback_failure_reason_stat_day_access
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", appId=").append(appId);
        sb.append(", groupCode=").append(groupCode);
        sb.append(", groupName=").append(groupName);
        sb.append(", dataTime=").append(dataTime);
        sb.append(", dataType=").append(dataType);
        sb.append(", bizType=").append(bizType);
        sb.append(", saasEnv=").append(saasEnv);
        sb.append(", totalCount=").append(totalCount);
        sb.append(", unKnownReasonCount=").append(unKnownReasonCount);
        sb.append(", personalReasonCount=").append(personalReasonCount);
        sb.append(", createTime=").append(createTime);
        sb.append(", lastUpdateTime=").append(lastUpdateTime);
        sb.append("]");
        return sb.toString();
    }
}