package com.treefinance.saas.monitor.common.domain.dto.alarmconfig;

import lombok.Getter;
import lombok.Setter;

/**
 * @author chengtong
 * @date 18/5/25 11:36
 */
@Setter
@Getter
public class OperatorAlarmTimeConfigDTO extends BaseTimeConfig {

    /**
     * 确认手机转化率的阈值百分比
     * */
    private Integer confirmMobileConversionRate;
    /**
     * 登录转化率的阈值百分比
     */
    private Integer loginConversionRate;
    /**
     * 登录成功转化率的阈值百分比
     */
    private Integer loginSuccessRate;
    /**
     * 爬树转化率的阈值百分比
     */
    private Integer crawlSuccessRate;
    /**
     * 洗数转化率的阈值百分比
     */
    private Integer processSuccessRate;
    /**
     * 回调转化率的阈值百分比
     */
    private Integer callbackSuccessRate;
    /**
     * 总转化率的阈值百分比
     */
    private Integer wholeConversionRate;

}
