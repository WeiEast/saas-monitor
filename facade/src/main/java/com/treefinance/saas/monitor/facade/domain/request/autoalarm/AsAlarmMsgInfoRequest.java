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
public class AsAlarmMsgInfoRequest extends BaseRequest {
    private static final long serialVersionUID = 4502816433449034991L;

    private String titleTemplate;
    private String bodyTemplate;

}
