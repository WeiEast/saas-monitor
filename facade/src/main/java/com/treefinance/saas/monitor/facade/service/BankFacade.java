package com.treefinance.saas.monitor.facade.service;

import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;
import com.treefinance.saas.monitor.facade.domain.ro.BankRO;

import java.util.List;

/**
 * 银行查询
 * Created by yh-treefinance on 2017/6/19.
 */
public interface BankFacade {
    /**
     * 银行查询
     * @return
     */
    MonitorResult<List<BankRO>> queryAll();
}
