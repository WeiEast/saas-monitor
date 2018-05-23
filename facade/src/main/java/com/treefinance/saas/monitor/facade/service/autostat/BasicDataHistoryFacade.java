package com.treefinance.saas.monitor.facade.service.autostat;

import com.treefinance.saas.monitor.facade.domain.request.autostat.BasicDataHistoryRequest;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;
import com.treefinance.saas.monitor.facade.domain.ro.autostat.BasicDataHistoryRO;

import java.util.List;

/**
 * Created by yh-treefinance on 2018/5/2.
 */
public interface BasicDataHistoryFacade {

    /**
     * 查询历史数据信息
     *
     * @param request
     * @return
     */
    MonitorResult<List<BasicDataHistoryRO>> queryList(BasicDataHistoryRequest request);
}
