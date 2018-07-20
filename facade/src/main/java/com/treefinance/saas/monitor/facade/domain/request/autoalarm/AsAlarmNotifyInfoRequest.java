package com.treefinance.saas.monitor.facade.domain.request.autoalarm;

import com.treefinance.saas.monitor.facade.domain.base.BaseRequest;
import lombok.Getter;
import lombok.Setter;

/**
 * @author haojiahong
 * @date 2018/7/19
 */
@Setter
@Getter
public class AsAlarmNotifyInfoRequest extends BaseRequest {
    private static final long serialVersionUID = 4931233743200566679L;

    private Long id;
    private String alarmLevel;
    private String wechatSwitch;
    private String smsSwitch;
    private String emailSwitch;
    private String ivrSwitch;
    private Byte receiverType;

}
