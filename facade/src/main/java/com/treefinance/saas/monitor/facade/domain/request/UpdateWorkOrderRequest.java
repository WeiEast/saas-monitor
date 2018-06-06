package com.treefinance.saas.monitor.facade.domain.request;

import com.treefinance.saas.monitor.facade.domain.base.BaseRequest;
import lombok.Getter;
import lombok.Setter;

/**
 * @author chengtong
 * @date 18/5/30 11:27
 */

@Setter
@Getter
public class UpdateWorkOrderRequest extends BaseRequest {

    private String opName;

    private String processorName;

    private Integer status;

    private Long id;

    private String remark;

}
