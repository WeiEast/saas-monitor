package com.treefinance.saas.monitor.facade.domain.ro.stat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by haojiahong on 2017/8/22.
 */
public class SaasErrorStepDayStatRO implements Serializable {

    private static final long serialVersionUID = -387012743372683536L;

    private Long id;

    private Date dataTime;

    private Byte dataType;

    private String errorCode;

    private String errorMsg;

    private Integer totalCount;

    private Integer failCount;

    private Integer cancelCount;

    private BigDecimal failRate;

    private BigDecimal cancelRate;

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

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getFailCount() {
        return failCount;
    }

    public void setFailCount(Integer failCount) {
        this.failCount = failCount;
    }

    public Integer getCancelCount() {
        return cancelCount;
    }

    public void setCancelCount(Integer cancelCount) {
        this.cancelCount = cancelCount;
    }

    public BigDecimal getFailRate() {
        return failRate;
    }

    public void setFailRate(BigDecimal failRate) {
        this.failRate = failRate;
    }

    public BigDecimal getCancelRate() {
        return cancelRate;
    }

    public void setCancelRate(BigDecimal cancelRate) {
        this.cancelRate = cancelRate;
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
}
