package com.treefinance.saas.monitor.facade.domain.ro;

import com.treefinance.saas.monitor.facade.domain.base.BaseRO;
import lombok.Data;

/**
 * @author chengtong
 * @date 18/9/11 15:34
 */
@Data
public class AppTaskStatResult extends BaseRO {

    private Integer taskNumToday;

    private Integer taskNumYesterday;

    private Integer isIncrease;

    private String compareRate;

}
