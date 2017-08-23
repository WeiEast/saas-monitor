package com.treefinance.saas.monitor.common.domain.dto;

import com.treefinance.saas.monitor.common.domain.BaseDTO;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by haojiahong on 2017/8/22.
 */
public class SaasErrorStepDayStatDTO extends BaseDTO {

    private static final long serialVersionUID = -1690299803440865834L;

    /**
     * 数据ID
     */
    private Long id;

    /**
     * 数据时间
     */
    private Date dataTime;

    /**
     * 数据类型：0-合计，1:银行，2：电商，3:邮箱，4:运营商
     */
    private Byte dataType;

    /**
     * 错误编码
     */
    private String errorCode;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 总数
     */
    private Integer totalCount;

    /**
     * 失败数
     */
    private Integer failCount;

    /**
     * 取消数
     */
    private Integer cancelCount;

    /**
     * 失败率
     */
    private BigDecimal failRate;

    /**
     * 取消率
     */
    private BigDecimal cancelRate;

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
}
