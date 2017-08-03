package com.treefinance.saas.monitor.facade.service.stat;

import com.treefinance.saas.monitor.facade.domain.request.ApiStatBaseRequest;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;
import com.treefinance.saas.monitor.facade.domain.ro.stat.api.ApiBaseStatRO;
import com.treefinance.saas.monitor.facade.domain.ro.stat.api.ApiStatAccessRO;
import com.treefinance.saas.monitor.facade.domain.ro.stat.api.ApiStatDayAccessRO;

import java.util.List;

/**
 * api访问统计facade
 * Created by haojiahong on 2017/7/7.
 */
public interface ApiStatAccessFacade {

    /**
     * api接口总访问情况统计
     *
     * @param request
     * @return
     */
    MonitorResult<List<ApiBaseStatRO>> queryTotalAccessList(ApiStatBaseRequest request);

    /**
     * api商户日访问情况
     *
     * @param request
     * @return
     */
    MonitorResult<List<ApiStatDayAccessRO>> queryDayAccessList(ApiStatBaseRequest request);

    /**
     *
     * @param request
     * @return
     */
    MonitorResult<List<ApiStatAccessRO>> queryStatAccessList(ApiStatBaseRequest request);

}
