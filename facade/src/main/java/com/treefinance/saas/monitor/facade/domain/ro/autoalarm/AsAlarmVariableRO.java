package com.treefinance.saas.monitor.facade.domain.ro.autoalarm;

import com.treefinance.saas.monitor.facade.domain.base.BaseRO;
import lombok.Getter;
import lombok.Setter;

/**
 * @author haojiahong
 * @date 2018/7/19
 */
@Getter
@Setter
public class AsAlarmVariableRO extends BaseRO {
    private Long id;
    private Long alarmId;
    private String name;
    private String code;
    private String value;
    private String description;

}
