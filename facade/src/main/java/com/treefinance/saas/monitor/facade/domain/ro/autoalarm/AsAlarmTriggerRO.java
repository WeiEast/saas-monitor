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
public class AsAlarmTriggerRO extends BaseRO {

    private Long id;
    private Integer triggerIndex;
    private Long alarmId;
    private String name;
    private Byte status;
    private String infoTrigger;
    private String warningTrigger;
    private String errorTrigger;
    private String recoveryTrigger;
    private String recoveryMessageTemplate;


}
