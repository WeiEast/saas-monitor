package com.treefinance.saas.monitor.facade.service;

import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;
import com.treefinance.saas.monitor.facade.domain.ro.OperatorRO;

import java.util.List;
import java.util.Map;

/**
 * Created by yh-treefinance on 2017/6/12.
 */
public interface OperatorFacade {

    /**
     * 查询所有运营商
     *
     * @return
     */
    MonitorResult<List<OperatorRO>> queryAll();

    /**
     * 根据website批量查询operator
     *
     * @param websites
     * @return
     */
    MonitorResult<Map<String, OperatorRO>> queryOperatorByWebsites(List<String> websites);
}
