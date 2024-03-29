package com.treefinance.saas.monitor.common.domain.dto.alarmconfig;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * @author chengtong
 * @date 18/5/11 15:20
 */
@Getter
@Setter
@ToString
public class TaskSuccRateAlarmTimeListDTO extends BaseTimeConfig{

    /**转化率的阈值*/
    private BigDecimal thresholdError;
    /**转化率的阈值*/
    private BigDecimal thresholdWarning;
    /**转化率的阈值*/
    private BigDecimal thresholdInfo;
    /**当前时间的interval*/
    private Integer intervals;

}
