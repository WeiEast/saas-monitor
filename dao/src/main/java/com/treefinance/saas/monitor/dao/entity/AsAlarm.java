package com.treefinance.saas.monitor.dao.entity;

import java.io.Serializable;
import java.util.Date;

public class AsAlarm implements Serializable {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column as_alarm.id
     *
     * @mbg.generated Thu Jul 19 14:02:23 CST 2018
     */
    private Long id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column as_alarm.name
     *
     * @mbg.generated Thu Jul 19 14:02:23 CST 2018
     */
    private String name;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column as_alarm.runEnv
     *
     * @mbg.generated Thu Jul 19 14:02:23 CST 2018
     */
    private Byte runEnv;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column as_alarm.alarmSwitch
     *
     * @mbg.generated Thu Jul 19 14:02:23 CST 2018
     */
    private String alarmSwitch;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column as_alarm.runInterval
     *
     * @mbg.generated Thu Jul 19 14:02:23 CST 2018
     */
    private String runInterval;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column as_alarm.timeInterval
     *
     * @mbg.generated Thu Jul 19 14:02:23 CST 2018
     */
    private Integer timeInterval;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column as_alarm.createTime
     *
     * @mbg.generated Thu Jul 19 14:02:23 CST 2018
     */
    private Date createTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column as_alarm.lastUpdateTime
     *
     * @mbg.generated Thu Jul 19 14:02:23 CST 2018
     */
    private Date lastUpdateTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table as_alarm
     *
     * @mbg.generated Thu Jul 19 14:02:23 CST 2018
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column as_alarm.id
     *
     * @return the value of as_alarm.id
     *
     * @mbg.generated Thu Jul 19 14:02:23 CST 2018
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column as_alarm.id
     *
     * @param id the value for as_alarm.id
     *
     * @mbg.generated Thu Jul 19 14:02:23 CST 2018
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column as_alarm.name
     *
     * @return the value of as_alarm.name
     *
     * @mbg.generated Thu Jul 19 14:02:23 CST 2018
     */
    public String getName() {
        return name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column as_alarm.name
     *
     * @param name the value for as_alarm.name
     *
     * @mbg.generated Thu Jul 19 14:02:23 CST 2018
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column as_alarm.runEnv
     *
     * @return the value of as_alarm.runEnv
     *
     * @mbg.generated Thu Jul 19 14:02:23 CST 2018
     */
    public Byte getRunEnv() {
        return runEnv;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column as_alarm.runEnv
     *
     * @param runEnv the value for as_alarm.runEnv
     *
     * @mbg.generated Thu Jul 19 14:02:23 CST 2018
     */
    public void setRunEnv(Byte runEnv) {
        this.runEnv = runEnv;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column as_alarm.alarmSwitch
     *
     * @return the value of as_alarm.alarmSwitch
     *
     * @mbg.generated Thu Jul 19 14:02:23 CST 2018
     */
    public String getAlarmSwitch() {
        return alarmSwitch;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column as_alarm.alarmSwitch
     *
     * @param alarmSwitch the value for as_alarm.alarmSwitch
     *
     * @mbg.generated Thu Jul 19 14:02:23 CST 2018
     */
    public void setAlarmSwitch(String alarmSwitch) {
        this.alarmSwitch = alarmSwitch == null ? null : alarmSwitch.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column as_alarm.runInterval
     *
     * @return the value of as_alarm.runInterval
     *
     * @mbg.generated Thu Jul 19 14:02:23 CST 2018
     */
    public String getRunInterval() {
        return runInterval;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column as_alarm.runInterval
     *
     * @param runInterval the value for as_alarm.runInterval
     *
     * @mbg.generated Thu Jul 19 14:02:23 CST 2018
     */
    public void setRunInterval(String runInterval) {
        this.runInterval = runInterval == null ? null : runInterval.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column as_alarm.timeInterval
     *
     * @return the value of as_alarm.timeInterval
     *
     * @mbg.generated Thu Jul 19 14:02:23 CST 2018
     */
    public Integer getTimeInterval() {
        return timeInterval;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column as_alarm.timeInterval
     *
     * @param timeInterval the value for as_alarm.timeInterval
     *
     * @mbg.generated Thu Jul 19 14:02:23 CST 2018
     */
    public void setTimeInterval(Integer timeInterval) {
        this.timeInterval = timeInterval;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column as_alarm.createTime
     *
     * @return the value of as_alarm.createTime
     *
     * @mbg.generated Thu Jul 19 14:02:23 CST 2018
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column as_alarm.createTime
     *
     * @param createTime the value for as_alarm.createTime
     *
     * @mbg.generated Thu Jul 19 14:02:23 CST 2018
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column as_alarm.lastUpdateTime
     *
     * @return the value of as_alarm.lastUpdateTime
     *
     * @mbg.generated Thu Jul 19 14:02:23 CST 2018
     */
    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column as_alarm.lastUpdateTime
     *
     * @param lastUpdateTime the value for as_alarm.lastUpdateTime
     *
     * @mbg.generated Thu Jul 19 14:02:23 CST 2018
     */
    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_alarm
     *
     * @mbg.generated Thu Jul 19 14:02:23 CST 2018
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", name=").append(name);
        sb.append(", runEnv=").append(runEnv);
        sb.append(", alarmSwitch=").append(alarmSwitch);
        sb.append(", runInterval=").append(runInterval);
        sb.append(", timeInterval=").append(timeInterval);
        sb.append(", createTime=").append(createTime);
        sb.append(", lastUpdateTime=").append(lastUpdateTime);
        sb.append("]");
        return sb.toString();
    }
}