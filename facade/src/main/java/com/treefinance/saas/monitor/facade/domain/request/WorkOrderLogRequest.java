package com.treefinance.saas.monitor.facade.domain.request;

import com.treefinance.saas.monitor.facade.domain.base.BaseRequest;
import lombok.Getter;
import lombok.Setter;

/**
 * @author chengtong
 * @date 18/5/30 11:30
 */
@Setter
@Getter
public class WorkOrderLogRequest extends BaseRequest {

    private Long orderId;

}
