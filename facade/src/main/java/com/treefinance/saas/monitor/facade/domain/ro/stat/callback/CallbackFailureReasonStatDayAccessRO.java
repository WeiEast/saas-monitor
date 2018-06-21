package com.treefinance.saas.monitor.facade.domain.ro.stat.callback;

import java.io.Serializable;
import java.util.Date;

/**
 * Good Luck Bro , No Bug !
 *
 * @author haojiahong
 * @date 2018/6/12
 */
public class CallbackFailureReasonStatDayAccessRO implements Serializable {
    private static final long serialVersionUID = -602263402260433412L;

    private Long id;

    private String appId;

    private String groupCode;

    private String groupName;

    private Date dataTime;

    private Byte dataType;

    private Byte bizType;

    private Byte saasEnv;

    private Integer totalCount;

    private Integer unKnownReasonCount;

    private Integer personalReasonCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getUnKnownReasonCount() {
        return unKnownReasonCount;
    }

    public void setUnKnownReasonCount(Integer unKnownReasonCount) {
        this.unKnownReasonCount = unKnownReasonCount;
    }

    public Integer getPersonalReasonCount() {
        return personalReasonCount;
    }

    public void setPersonalReasonCount(Integer personalReasonCount) {
        this.personalReasonCount = personalReasonCount;
    }
}
