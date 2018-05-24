package com.treefinance.saas.monitor.common.domain.dto.alarmconfig;


import com.datatrees.common.util.DateUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.beans.Transient;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

/**
 * @Author: chengtong
 * @Date: 18/3/9 17:01
 */

@Setter
@Getter
@ToString
public class EmailMonitorAlarmTimeConfigDTO extends BaseTimeConfig{

    /**
     * 登录转化率
     */
    private Integer loginConversionRate;

    private Integer loginSuccessRate;

    private Integer crawlSuccessRate;

    private Integer processSuccessRate;

    private Integer callbackSuccessRate;

    private Integer wholeConversionRate;

}
