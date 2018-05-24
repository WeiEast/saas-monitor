package com.treefinance.saas.monitor.common.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
/**
 * @author chengtong
 */
@Setter
@Getter
public class OperatorStatAccessDTO extends BaseStatAccessDTO {


    private Integer confirmMobileCount;

    private BigDecimal confirmMobileConversionRate;

    private String groupCode;

    private String groupName;

    /**
     * 前n天的相同时刻确认手机号数平均值
     */
    private BigDecimal previousConfirmMobileAvgCount;

    /**
     *
     */
    private BigDecimal previousConfirmMobileConversionRate;


    public BigDecimal getConfirmMobileConversionRate() {
        return confirmMobileConversionRate;
    }

    public void setConfirmMobileConversionRate(BigDecimal confirmMobileConversionRate) {
        this.confirmMobileConversionRate = confirmMobileConversionRate;
    }

}