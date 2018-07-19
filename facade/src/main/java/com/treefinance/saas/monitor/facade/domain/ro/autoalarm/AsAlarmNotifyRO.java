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
public class AsAlarmNotifyRO extends BaseRO {

    private Long id;
    private Long alarmId;
    private String alarmLevel;
    private String wechatSwitch;
    private String smsSwitch;
    private String emailSwitch;
    private String ivrSwitch;
    private Byte receiverType;

}
