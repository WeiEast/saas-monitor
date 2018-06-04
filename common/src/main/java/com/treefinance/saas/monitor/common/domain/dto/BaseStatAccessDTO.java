package com.treefinance.saas.monitor.common.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author chengtong
 * @date 18/3/13 10:32
 */
@Setter
@Getter
public class BaseStatAccessDTO implements Serializable {

    private Long id;

    private String appId;

    private Date dataTime;

    private Byte dataType;

    private Integer userCount;

    private Integer taskCount;

    private Integer entryCount;

    private Integer startLoginCount;

    private Integer loginSuccessCount;

    private Integer crawlSuccessCount;

    private Integer processSuccessCount;

    private Integer callbackSuccessCount;

    private BigDecimal loginConversionRate;

    private BigDecimal loginSuccessRate;

    private BigDecimal crawlSuccessRate;

    private BigDecimal processSuccessRate;

    private BigDecimal callbackSuccessRate;

    /**
     * 前n天的相同时刻创建任务数平均值
     */
    private BigDecimal previousEntryAvgCount;

    /**
     * 前n天的相同时刻开始登陆数平均值
     */
    private BigDecimal previousStartLoginAvgCount;

    /**
     * 前n天的相同时刻登陆成功数平均值
     */
    private BigDecimal previousLoginSuccessAvgCount;

    /**
     * 前n天的相同时刻抓取成功数平均值
     */
    private BigDecimal previousCrawlSuccessAvgCount;

    /**
     * 前n天的相同时刻洗数成功数平均值
     */
    private BigDecimal previousProcessSuccessAvgCount;

    /**
     * 前n天的相同时刻回调成功数平均值
     */
    private BigDecimal previousCallbackSuccessAvgCount;

    private BigDecimal previousWholeConversionAvgCount;

    /**
     * 前n天的相同时刻登录转化率平均值
     */
    private BigDecimal previousLoginConversionRate;

    /**
     * 前n天的相同时刻登陆成功率平均值
     */
    private BigDecimal previousLoginSuccessRate;

    /**
     * 前n天的相同时刻抓取成功率平均值
     */
    private BigDecimal previousCrawlSuccessRate;

    /**
     * 前n天的相同时刻洗数成功率平均值
     */
    private BigDecimal previousProcessSuccessRate;

    /**
     * 前n天相同时刻回调成功率平均值
     */
    private BigDecimal previousCallbackSuccessRate;

    private Date createTime;

    private Date lastUpdateTime;

    private BigDecimal wholeConversionRate;

    private BigDecimal previousWholeConversionRate;

}
