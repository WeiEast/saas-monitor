package com.treefinance.saas.monitor.facade.service.stat;

import com.treefinance.saas.monitor.facade.domain.request.CallbackFailureReasonStatAccessRequest;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;
import com.treefinance.saas.monitor.facade.domain.ro.stat.callback.CallbackFailureReasonStatAccessRO;
import com.treefinance.saas.monitor.facade.domain.ro.stat.callback.CallbackFailureReasonStatDayAccessRO;

import java.util.List;

/**
 * Good Luck Bro , No Bug !
 *
 * @author haojiahong
 * @date 2018/6/12
 */
public interface CallbackFailureReasonStatAccessFacade {
    /**
     * 日回调数据统计查询
     *
     * @param request
     * @return
     */
    MonitorResult<List<CallbackFailureReasonStatDayAccessRO>> queryCallbackFailureReasonStatDayAccessList(CallbackFailureReasonStatAccessRequest request);

    /**
     * 分时回调统计数据查询
     *
     * @param request
     * @return
     */
    MonitorResult<List<CallbackFailureReasonStatAccessRO>> queryCallbackFailureReasonStatAccessList(CallbackFailureReasonStatAccessRequest request);


}
