package com.treefinance.saas.monitor.facade.domain.request;

import com.treefinance.saas.monitor.facade.domain.base.PageRequest;

import java.io.Serializable;
import java.util.Date;

/**
 * 电商分时监控查询条件类
 *
 * @author:guoguoyun
 * @date:Created in 2018/1/15上午10:56
 */
public class EcommerceDetailAccessRequest extends PageRequest implements Serializable {

    private static final long serialVersionUID = 2524208745510448161L;
    private Date dataDate;

    private Date startDate;

    private Date endDate;

    private String groupCode;

    private String groupName;

    private Byte statType;

    private Byte sourceType;

    private String appId;

    private Byte saasEnv;


    private Integer intervalMins = 30;

    public Byte getSaasEnv() {
        return saasEnv;
    }

    public void setSaasEnv(Byte saasEnv) {
        this.saasEnv = saasEnv;
    }

    public Integer getIntervalMins() {
        return intervalMins;
    }

    public void setIntervalMins(Integer intervalMins) {
        this.intervalMins = intervalMins;
    }

    public Byte getSourceType() {
        return sourceType;
    }

    public void setSourceType(Byte sourceType) {
        this.sourceType = sourceType;
    }

    public Date getDataDate() {
        return dataDate;
    }

    public void setDataDate(Date dataDate) {
        this.dataDate = dataDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Byte getStatType() {
        return statType;
    }

    public void setStatType(Byte statType) {
        this.statType = statType;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }
}
