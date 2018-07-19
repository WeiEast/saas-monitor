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
public class AsAlarmQueryInfoRequest extends BaseRequest {
    private static final long serialVersionUID = -4734757118395905193L;

    private String resultCode;
    private String querySql;
    private String description;

}
