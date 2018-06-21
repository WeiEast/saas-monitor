package com.treefinance.saas.monitor.facade.service.stat;

import com.treefinance.saas.monitor.facade.domain.request.BaseStatAccessRequest;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;
import com.treefinance.saas.monitor.facade.domain.ro.stat.RealTimeStatAccessRO;

import java.util.List;

/**
 * Good Luck Bro , No Bug !
 * 任务实时监控
 *
 * @author haojiahong
 * @date 2018/6/21
 */
public interface RealTimeStatAccessFacade {

    MonitorResult<List<RealTimeStatAccessRO>> queryRealTimeStatAccess(BaseStatAccessRequest request);

}
