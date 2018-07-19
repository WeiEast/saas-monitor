package com.treefinance.saas.monitor.facade.domain.request.autoalarm;

import com.treefinance.saas.monitor.facade.domain.base.BaseRequest;
import lombok.Getter;
import lombok.Setter;

/**
 * @author haojiahong
 * @date 2018/7/19
 */
@Getter
@Setter
public class AsAlarmInfoRequest extends BaseRequest {
    private static final long serialVersionUID = 8045164453271422350L;

    private String name;
    private Byte runEnv;
    private String alarmSwitch;
    private String runInterval;
    private Integer timeInterval;

}
