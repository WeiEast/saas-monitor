package com.treefinance.saas.monitor.facade.domain.request;

import com.treefinance.saas.monitor.facade.domain.base.PageRequest;
import lombok.Getter;
import lombok.Setter;

/**
 * @author chengtong
 * @date 18/6/12 19:20
 */
@Setter
@Getter
public class SaasWorkerRequest extends PageRequest{

    private String name;

}
