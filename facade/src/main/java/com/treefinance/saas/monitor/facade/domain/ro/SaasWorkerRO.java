package com.treefinance.saas.monitor.facade.domain.ro;

import com.treefinance.saas.monitor.facade.domain.base.BaseRO;
import lombok.Getter;
import lombok.Setter;

/**
 * @author chengtong
 * @date 18/5/30 11:31
 */
@Setter
@Getter
public class SaasWorkerRO extends BaseRO{

    private Long id;

    private String name;

    private String mobile;

    private String email;

    private String dutyCorn;


}
