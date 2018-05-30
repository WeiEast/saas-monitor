package com.treefinance.saas.monitor.facade.domain.request;

import com.treefinance.saas.monitor.facade.domain.base.PageRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author chengtong
 * @date 18/5/30 11:27
 */

@Setter
@Getter
public class WorkOrderRequest extends PageRequest {

    private String dutyName;

    private String processorName;

    private Date startTime;

    private Date endTime;

    private Long id;

}
