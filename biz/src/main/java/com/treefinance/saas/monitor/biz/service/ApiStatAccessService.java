package com.treefinance.saas.monitor.biz.service;

import com.treefinance.saas.monitor.facade.domain.request.ApiStatBaseRequest;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;
import com.treefinance.saas.monitor.facade.domain.ro.stat.api.ApiBaseStatRO;
import com.treefinance.saas.monitor.facade.domain.ro.stat.api.ApiStatAccessRO;
import com.treefinance.saas.monitor.facade.domain.ro.stat.api.ApiStatDayAccessRO;

import java.util.List;

/**
 * Created by haojiahong on 2017/7/7.
 */
public interface ApiStatAccessService {

    MonitorResult<List<ApiBaseStatRO>> queryTotalAccessList(ApiStatBaseRequest request);

    MonitorResult<List<ApiStatDayAccessRO>> queryDayAccessList(ApiStatBaseRequest request);

    MonitorResult<List<ApiStatAccessRO>> queryStatAccessList(ApiStatBaseRequest request);
}
