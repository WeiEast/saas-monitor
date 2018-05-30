package com.treefinance.saas.monitor.facade.domain.ro;

import com.treefinance.saas.monitor.facade.domain.base.BaseRO;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author chengtong
 * @date 18/5/30 11:17
 */
@Setter
@Getter
public class AlarmWorkOrderRO extends BaseRO {

    private Long id;

    private Long recordId;

    private Integer status;

    private String dutyName;

    private String processorName;

    private String remark;

}
