package com.treefinance.saas.monitor.facade.domain.ro;

import com.treefinance.saas.monitor.facade.domain.base.BaseRO;
import lombok.Getter;
import lombok.Setter;

/**
 * @author chengtong
 * @date 18/5/30 14:09
 */
@Getter
@Setter
public class WorkOrderLogRO extends BaseRO {

    private Long id;

    private Long orderId;

    private Long recordId;

    private String opName;

    private String opDesc;


}
