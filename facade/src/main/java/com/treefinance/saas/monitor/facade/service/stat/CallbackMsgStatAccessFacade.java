package com.treefinance.saas.monitor.facade.service.stat;

import com.treefinance.saas.monitor.facade.domain.request.CallbackMsgStatAccessRequest;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;
import com.treefinance.saas.monitor.facade.domain.ro.stat.callback.AsStatCallbackRO;
import com.treefinance.saas.monitor.facade.domain.ro.stat.callback.AsStatDayCallbackRO;

import java.util.List;

/**
 * Buddha Bless , No Bug !
 *
 * @author haojiahong
 * @date 2018/3/15
 */
public interface CallbackMsgStatAccessFacade {

    /**
     * 日回调数据统计查询
     *
     * @param request
     * @return
     */
    MonitorResult<List<AsStatDayCallbackRO>> queryCallbackMsgStatDayAccessList(CallbackMsgStatAccessRequest request);

    /**
     * 分时回调统计数据查询
     *
     * @param request
     * @return
     */
    MonitorResult<List<AsStatCallbackRO>> queryCallbackMsgStatAccessList(CallbackMsgStatAccessRequest request);

}
