package com.treefinance.saas.monitor.common.domain.dto.alarmconfig;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Author: chengtong
 * @Date: 18/3/9 17:01
 */

@Setter
@Getter
@ToString
public class EmailMonitorAlarmTimeConfigDTO extends BaseTimeConfig{

    /**
     * 登录转化率的阈值百分比
     *
     */
    private Integer loginConversionRate;
    /**
     * 登录成功率的阈值百分比
     */
    private Integer loginSuccessRate;
    /**
     * 扒数成功率的阈值百分比
     */
    private Integer crawlSuccessRate;
    /**
     * 洗数成功率的阈值百分比
     */
    private Integer processSuccessRate;
    /**
     * 回调成功率的阈值百分比
     */
    private Integer callbackSuccessRate;
    /**
     * 总转化率的阈值百分比
     */
    private Integer wholeConversionRate;

}
