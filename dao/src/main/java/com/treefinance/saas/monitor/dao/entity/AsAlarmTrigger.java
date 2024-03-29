package com.treefinance.saas.monitor.dao.entity;

import java.io.Serializable;
import java.util.Date;

public class AsAlarmTrigger implements Serializable {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column as_alarm_trigger.id
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    private Long id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column as_alarm_trigger.alarmId
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    private Long alarmId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column as_alarm_trigger.triggerIndex
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    private Integer triggerIndex;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column as_alarm_trigger.name
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    private String name;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column as_alarm_trigger.status
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    private Byte status;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column as_alarm_trigger.infoTrigger
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    private String infoTrigger;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column as_alarm_trigger.warningTrigger
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    private String warningTrigger;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column as_alarm_trigger.errorTrigger
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    private String errorTrigger;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column as_alarm_trigger.recoveryTrigger
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    private String recoveryTrigger;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column as_alarm_trigger.recoveryMessageTemplate
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    private String recoveryMessageTemplate;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column as_alarm_trigger.createTime
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    private Date createTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column as_alarm_trigger.lastUpdateTime
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    private Date lastUpdateTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table as_alarm_trigger
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column as_alarm_trigger.id
     *
     * @return the value of as_alarm_trigger.id
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column as_alarm_trigger.id
     *
     * @param id the value for as_alarm_trigger.id
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column as_alarm_trigger.alarmId
     *
     * @return the value of as_alarm_trigger.alarmId
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    public Long getAlarmId() {
        return alarmId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column as_alarm_trigger.alarmId
     *
     * @param alarmId the value for as_alarm_trigger.alarmId
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    public void setAlarmId(Long alarmId) {
        this.alarmId = alarmId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column as_alarm_trigger.triggerIndex
     *
     * @return the value of as_alarm_trigger.triggerIndex
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    public Integer getTriggerIndex() {
        return triggerIndex;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column as_alarm_trigger.triggerIndex
     *
     * @param triggerIndex the value for as_alarm_trigger.triggerIndex
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    public void setTriggerIndex(Integer triggerIndex) {
        this.triggerIndex = triggerIndex;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column as_alarm_trigger.name
     *
     * @return the value of as_alarm_trigger.name
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    public String getName() {
        return name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column as_alarm_trigger.name
     *
     * @param name the value for as_alarm_trigger.name
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column as_alarm_trigger.status
     *
     * @return the value of as_alarm_trigger.status
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    public Byte getStatus() {
        return status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column as_alarm_trigger.status
     *
     * @param status the value for as_alarm_trigger.status
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    public void setStatus(Byte status) {
        this.status = status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column as_alarm_trigger.infoTrigger
     *
     * @return the value of as_alarm_trigger.infoTrigger
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    public String getInfoTrigger() {
        return infoTrigger;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column as_alarm_trigger.infoTrigger
     *
     * @param infoTrigger the value for as_alarm_trigger.infoTrigger
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    public void setInfoTrigger(String infoTrigger) {
        this.infoTrigger = infoTrigger == null ? null : infoTrigger.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column as_alarm_trigger.warningTrigger
     *
     * @return the value of as_alarm_trigger.warningTrigger
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    public String getWarningTrigger() {
        return warningTrigger;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column as_alarm_trigger.warningTrigger
     *
     * @param warningTrigger the value for as_alarm_trigger.warningTrigger
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    public void setWarningTrigger(String warningTrigger) {
        this.warningTrigger = warningTrigger == null ? null : warningTrigger.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column as_alarm_trigger.errorTrigger
     *
     * @return the value of as_alarm_trigger.errorTrigger
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    public String getErrorTrigger() {
        return errorTrigger;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column as_alarm_trigger.errorTrigger
     *
     * @param errorTrigger the value for as_alarm_trigger.errorTrigger
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    public void setErrorTrigger(String errorTrigger) {
        this.errorTrigger = errorTrigger == null ? null : errorTrigger.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column as_alarm_trigger.recoveryTrigger
     *
     * @return the value of as_alarm_trigger.recoveryTrigger
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    public String getRecoveryTrigger() {
        return recoveryTrigger;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column as_alarm_trigger.recoveryTrigger
     *
     * @param recoveryTrigger the value for as_alarm_trigger.recoveryTrigger
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    public void setRecoveryTrigger(String recoveryTrigger) {
        this.recoveryTrigger = recoveryTrigger == null ? null : recoveryTrigger.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column as_alarm_trigger.recoveryMessageTemplate
     *
     * @return the value of as_alarm_trigger.recoveryMessageTemplate
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    public String getRecoveryMessageTemplate() {
        return recoveryMessageTemplate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column as_alarm_trigger.recoveryMessageTemplate
     *
     * @param recoveryMessageTemplate the value for as_alarm_trigger.recoveryMessageTemplate
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    public void setRecoveryMessageTemplate(String recoveryMessageTemplate) {
        this.recoveryMessageTemplate = recoveryMessageTemplate == null ? null : recoveryMessageTemplate.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column as_alarm_trigger.createTime
     *
     * @return the value of as_alarm_trigger.createTime
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column as_alarm_trigger.createTime
     *
     * @param createTime the value for as_alarm_trigger.createTime
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column as_alarm_trigger.lastUpdateTime
     *
     * @return the value of as_alarm_trigger.lastUpdateTime
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column as_alarm_trigger.lastUpdateTime
     *
     * @param lastUpdateTime the value for as_alarm_trigger.lastUpdateTime
     *
     * @mbg.generated Tue Jul 24 09:53:08 CST 2018
     */
    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_alarm_trigger
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
        sb.append(", triggerIndex=").append(triggerIndex);
        sb.append(", name=").append(name);
        sb.append(", status=").append(status);
        sb.append(", infoTrigger=").append(infoTrigger);
        sb.append(", warningTrigger=").append(warningTrigger);
        sb.append(", errorTrigger=").append(errorTrigger);
        sb.append(", recoveryTrigger=").append(recoveryTrigger);
        sb.append(", recoveryMessageTemplate=").append(recoveryMessageTemplate);
        sb.append(", createTime=").append(createTime);
        sb.append(", lastUpdateTime=").append(lastUpdateTime);
        sb.append("]");
        return sb.toString();
    }
}