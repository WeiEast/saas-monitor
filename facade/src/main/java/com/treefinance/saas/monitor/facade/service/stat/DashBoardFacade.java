package com.treefinance.saas.monitor.facade.service.stat;

import com.treefinance.saas.monitor.facade.domain.request.DashboardStatRequest;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;
import com.treefinance.saas.monitor.facade.domain.ro.DashBoardResult;

/**
 * @author chengtong
 * @date 18/9/11 14:52
 */
public interface DashBoardFacade {

    MonitorResult<DashBoardResult> queryDashboardResult(DashboardStatRequest request);

}
