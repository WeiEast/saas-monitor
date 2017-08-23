package com.treefinance.saas.monitor.facade.domain.ro.stat;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by haojiahong on 2017/8/22.
 */
public class SaasErrorStepDayStatRO implements Serializable {

    private static final long serialVersionUID = -387012743372683536L;

    private Long id;

    private Date dataTime;

    private Byte dataType;

    private String errorStepCode;

    private Integer failCount;

    private Date createTime;

    private Date lastUpdateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getFailCount() {
        return failCount;
    }

    public void setFailCount(Integer failCount) {
        this.failCount = failCount;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getErrorStepCode() {
        return errorStepCode;
    }

    public void setErrorStepCode(String errorStepCode) {
        this.errorStepCode = errorStepCode;
    }
}
