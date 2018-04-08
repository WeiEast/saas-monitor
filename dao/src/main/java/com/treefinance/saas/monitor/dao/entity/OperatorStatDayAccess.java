package com.treefinance.saas.monitor.dao.entity;

import java.io.Serializable;
import java.util.Date;

public class OperatorStatDayAccess implements Serializable {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column operator_stat_day_access.id
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    private Long id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column operator_stat_day_access.appId
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    private String appId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column operator_stat_day_access.groupCode
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    private String groupCode;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column operator_stat_day_access.groupName
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    private String groupName;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column operator_stat_day_access.dataTime
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    private Date dataTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column operator_stat_day_access.dataType
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    private Byte dataType;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column operator_stat_day_access.saasEnv
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    private Byte saasEnv;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column operator_stat_day_access.userCount
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    private Integer userCount;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column operator_stat_day_access.taskCount
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    private Integer taskCount;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column operator_stat_day_access.entryCount
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    private Integer entryCount;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column operator_stat_day_access.confirmMobileCount
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    private Integer confirmMobileCount;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column operator_stat_day_access.startLoginCount
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    private Integer startLoginCount;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column operator_stat_day_access.loginSuccessCount
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    private Integer loginSuccessCount;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column operator_stat_day_access.crawlSuccessCount
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    private Integer crawlSuccessCount;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column operator_stat_day_access.processSuccessCount
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    private Integer processSuccessCount;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column operator_stat_day_access.callbackSuccessCount
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    private Integer callbackSuccessCount;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column operator_stat_day_access.createTime
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    private Date createTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column operator_stat_day_access.lastUpdateTime
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    private Date lastUpdateTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table operator_stat_day_access
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column operator_stat_day_access.id
     *
     * @return the value of operator_stat_day_access.id
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column operator_stat_day_access.id
     *
     * @param id the value for operator_stat_day_access.id
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column operator_stat_day_access.appId
     *
     * @return the value of operator_stat_day_access.appId
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    public String getAppId() {
        return appId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column operator_stat_day_access.appId
     *
     * @param appId the value for operator_stat_day_access.appId
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    public void setAppId(String appId) {
        this.appId = appId == null ? null : appId.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column operator_stat_day_access.groupCode
     *
     * @return the value of operator_stat_day_access.groupCode
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    public String getGroupCode() {
        return groupCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column operator_stat_day_access.groupCode
     *
     * @param groupCode the value for operator_stat_day_access.groupCode
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode == null ? null : groupCode.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column operator_stat_day_access.groupName
     *
     * @return the value of operator_stat_day_access.groupName
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column operator_stat_day_access.groupName
     *
     * @param groupName the value for operator_stat_day_access.groupName
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName == null ? null : groupName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column operator_stat_day_access.dataTime
     *
     * @return the value of operator_stat_day_access.dataTime
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    public Date getDataTime() {
        return dataTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column operator_stat_day_access.dataTime
     *
     * @param dataTime the value for operator_stat_day_access.dataTime
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    public void setDataTime(Date dataTime) {
        this.dataTime = dataTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column operator_stat_day_access.dataType
     *
     * @return the value of operator_stat_day_access.dataType
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    public Byte getDataType() {
        return dataType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column operator_stat_day_access.dataType
     *
     * @param dataType the value for operator_stat_day_access.dataType
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    public void setDataType(Byte dataType) {
        this.dataType = dataType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column operator_stat_day_access.saasEnv
     *
     * @return the value of operator_stat_day_access.saasEnv
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    public Byte getSaasEnv() {
        return saasEnv;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column operator_stat_day_access.saasEnv
     *
     * @param saasEnv the value for operator_stat_day_access.saasEnv
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    public void setSaasEnv(Byte saasEnv) {
        this.saasEnv = saasEnv;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column operator_stat_day_access.userCount
     *
     * @return the value of operator_stat_day_access.userCount
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    public Integer getUserCount() {
        return userCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column operator_stat_day_access.userCount
     *
     * @param userCount the value for operator_stat_day_access.userCount
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    public void setUserCount(Integer userCount) {
        this.userCount = userCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column operator_stat_day_access.taskCount
     *
     * @return the value of operator_stat_day_access.taskCount
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    public Integer getTaskCount() {
        return taskCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column operator_stat_day_access.taskCount
     *
     * @param taskCount the value for operator_stat_day_access.taskCount
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    public void setTaskCount(Integer taskCount) {
        this.taskCount = taskCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column operator_stat_day_access.entryCount
     *
     * @return the value of operator_stat_day_access.entryCount
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    public Integer getEntryCount() {
        return entryCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column operator_stat_day_access.entryCount
     *
     * @param entryCount the value for operator_stat_day_access.entryCount
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    public void setEntryCount(Integer entryCount) {
        this.entryCount = entryCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column operator_stat_day_access.confirmMobileCount
     *
     * @return the value of operator_stat_day_access.confirmMobileCount
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    public Integer getConfirmMobileCount() {
        return confirmMobileCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column operator_stat_day_access.confirmMobileCount
     *
     * @param confirmMobileCount the value for operator_stat_day_access.confirmMobileCount
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    public void setConfirmMobileCount(Integer confirmMobileCount) {
        this.confirmMobileCount = confirmMobileCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column operator_stat_day_access.startLoginCount
     *
     * @return the value of operator_stat_day_access.startLoginCount
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    public Integer getStartLoginCount() {
        return startLoginCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column operator_stat_day_access.startLoginCount
     *
     * @param startLoginCount the value for operator_stat_day_access.startLoginCount
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    public void setStartLoginCount(Integer startLoginCount) {
        this.startLoginCount = startLoginCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column operator_stat_day_access.loginSuccessCount
     *
     * @return the value of operator_stat_day_access.loginSuccessCount
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    public Integer getLoginSuccessCount() {
        return loginSuccessCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column operator_stat_day_access.loginSuccessCount
     *
     * @param loginSuccessCount the value for operator_stat_day_access.loginSuccessCount
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    public void setLoginSuccessCount(Integer loginSuccessCount) {
        this.loginSuccessCount = loginSuccessCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column operator_stat_day_access.crawlSuccessCount
     *
     * @return the value of operator_stat_day_access.crawlSuccessCount
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    public Integer getCrawlSuccessCount() {
        return crawlSuccessCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column operator_stat_day_access.crawlSuccessCount
     *
     * @param crawlSuccessCount the value for operator_stat_day_access.crawlSuccessCount
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    public void setCrawlSuccessCount(Integer crawlSuccessCount) {
        this.crawlSuccessCount = crawlSuccessCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column operator_stat_day_access.processSuccessCount
     *
     * @return the value of operator_stat_day_access.processSuccessCount
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    public Integer getProcessSuccessCount() {
        return processSuccessCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column operator_stat_day_access.processSuccessCount
     *
     * @param processSuccessCount the value for operator_stat_day_access.processSuccessCount
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    public void setProcessSuccessCount(Integer processSuccessCount) {
        this.processSuccessCount = processSuccessCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column operator_stat_day_access.callbackSuccessCount
     *
     * @return the value of operator_stat_day_access.callbackSuccessCount
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    public Integer getCallbackSuccessCount() {
        return callbackSuccessCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column operator_stat_day_access.callbackSuccessCount
     *
     * @param callbackSuccessCount the value for operator_stat_day_access.callbackSuccessCount
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    public void setCallbackSuccessCount(Integer callbackSuccessCount) {
        this.callbackSuccessCount = callbackSuccessCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column operator_stat_day_access.createTime
     *
     * @return the value of operator_stat_day_access.createTime
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column operator_stat_day_access.createTime
     *
     * @param createTime the value for operator_stat_day_access.createTime
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column operator_stat_day_access.lastUpdateTime
     *
     * @return the value of operator_stat_day_access.lastUpdateTime
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column operator_stat_day_access.lastUpdateTime
     *
     * @param lastUpdateTime the value for operator_stat_day_access.lastUpdateTime
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
     */
    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operator_stat_day_access
     *
     * @mbg.generated Sun Apr 08 19:26:46 CST 2018
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
        sb.append(", saasEnv=").append(saasEnv);
        sb.append(", userCount=").append(userCount);
        sb.append(", taskCount=").append(taskCount);
        sb.append(", entryCount=").append(entryCount);
        sb.append(", confirmMobileCount=").append(confirmMobileCount);
        sb.append(", startLoginCount=").append(startLoginCount);
        sb.append(", loginSuccessCount=").append(loginSuccessCount);
        sb.append(", crawlSuccessCount=").append(crawlSuccessCount);
        sb.append(", processSuccessCount=").append(processSuccessCount);
        sb.append(", callbackSuccessCount=").append(callbackSuccessCount);
        sb.append(", createTime=").append(createTime);
        sb.append(", lastUpdateTime=").append(lastUpdateTime);
        sb.append("]");
        return sb.toString();
    }
}