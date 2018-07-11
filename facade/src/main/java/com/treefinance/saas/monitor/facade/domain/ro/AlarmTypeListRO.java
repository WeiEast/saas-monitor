package com.treefinance.saas.monitor.facade.domain.ro;

import com.treefinance.saas.monitor.facade.domain.base.BaseRO;
import lombok.Getter;
import lombok.Setter;

/**
 * @author chengtong
 * @date 18/7/9 20:01
 */
@Setter
@Getter
public class AlarmTypeListRO extends BaseRO{

    private String name;

    private String value;

}
