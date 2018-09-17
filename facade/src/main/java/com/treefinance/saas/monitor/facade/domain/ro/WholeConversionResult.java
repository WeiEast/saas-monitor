package com.treefinance.saas.monitor.facade.domain.ro;

import com.treefinance.saas.monitor.facade.domain.base.BaseRO;
import lombok.Data;

/**
 * @author chengtong
 * @date 18/9/11 15:34
 */
@Data
public class WholeConversionResult extends BaseRO {

    private String rateToday;

    private Integer isIncrease;

    private String rateYesterday;

    private String compareRate;

}
