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
public class AsAlarmVariableInfoRequest extends BaseRequest {
    private static final long serialVersionUID = -8284888902787511571L;

    private String name;
    private String code;
    private String value;
    private String description;

}
