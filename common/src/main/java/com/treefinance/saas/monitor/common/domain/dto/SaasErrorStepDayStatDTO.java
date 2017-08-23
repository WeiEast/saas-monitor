package com.treefinance.saas.monitor.common.domain.dto;

import com.treefinance.saas.monitor.common.domain.BaseDTO;

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
    private String errorStepCode;
    /**
     * 失败数
     */
    private Integer failCount;


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

    public String getErrorStepCode() {
        return errorStepCode;
    }

    public void setErrorStepCode(String errorStepCode) {
        this.errorStepCode = errorStepCode;
    }
}
