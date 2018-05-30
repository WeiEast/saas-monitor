package com.treefinance.saas.monitor.facade.domain.request;

import com.treefinance.saas.monitor.facade.domain.base.BaseRequest;
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
public class UpdateWorkOrderRequest extends BaseRequest {

    private String dutyName;

    private String processorName;

    private Integer status;

    private Long id;

}
