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
public class AsAlarmTriggerInfoRequest extends BaseRequest {
    private static final long serialVersionUID = 7719000650401292325L;

    private Long id;
    private String name;
    private Byte status;
    private String infoTrigger;
    private String warningTrigger;
    private String errorTrigger;
    private String recoveryTrigger;
    private String recoveryMessageTemplate;
    private Byte toDelete;


}
