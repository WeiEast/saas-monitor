package com.treefinance.saas.monitor.facade.domain.ro.stat;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * Good Luck Bro , No Bug !
 *
 * @author haojiahong
 * @date 2018/6/21
 */
public class RealTimeStatAccessRO implements Serializable {

    private static final long serialVersionUID = 329332359549868608L;

    private String appId;

    private String groupCode;

    private String groupName;

    private Date dataTime;

    private Byte dataType;

    private Byte bizType;

    private Byte saasEnv;

    private Map<String, Integer> statDataMap;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
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

    public Date getDataTime() {
        return dataTime;
    }

    public void setDataTime(Date dataTime) {
        this.dataTime = dataTime;
    }

    public Byte getDataType() {
        return dataType;
    }

    public void setDataType(Byte dataType) {
        this.dataType = dataType;
    }

    public Byte getBizType() {
        return bizType;
    }

    public void setBizType(Byte bizType) {
        this.bizType = bizType;
    }

    public Byte getSaasEnv() {
        return saasEnv;
    }

    public void setSaasEnv(Byte saasEnv) {
        this.saasEnv = saasEnv;
    }

    public Map<String, Integer> getStatDataMap() {
        return statDataMap;
    }

    public void setStatDataMap(Map<String, Integer> statDataMap) {
        this.statDataMap = statDataMap;
    }
}
