package com.treefinance.saas.monitor.facade.service;

import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;
import com.treefinance.saas.monitor.facade.domain.ro.EcommerceRO;

import java.util.List;

/**
 * Created by yh-treefinance on 2017/6/12.
 */
public interface EcommerceFacade {
    /**
     * 查询所有电商列表
     *
     * @return
     */
    MonitorResult<List<EcommerceRO>> queryAll();



}
