package com.treefinance.saas.monitor.biz.service;

import com.treefinance.saas.monitor.common.enumeration.ESaasEnv;

import java.util.List;

/**
 * @author chengtong
 * @date 18/9/12 16:53
 */
public interface OperatorStatAccessService {

    List<String> queryDecreasedOperator(ESaasEnv saasEnv);

}
