package com.treefinance.saas.monitor.biz.facade;

import com.treefinance.saas.monitor.biz.service.ApiStatAccessService;
import com.treefinance.saas.monitor.facade.checker.ApiStatChecker;
import com.treefinance.saas.monitor.facade.domain.request.ApiStatBaseRequest;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;
import com.treefinance.saas.monitor.facade.domain.ro.stat.api.ApiBaseStatRO;
import com.treefinance.saas.monitor.facade.domain.ro.stat.api.ApiStatAccessRO;
import com.treefinance.saas.monitor.facade.domain.ro.stat.api.ApiStatDayAccessRO;
import com.treefinance.saas.monitor.facade.service.stat.ApiStatAccessFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by haojiahong on 2017/7/7.
 */
@Service("apiStatAccessFacade")
public class ApiStatAccessFacadeImpl implements ApiStatAccessFacade {

    @Autowired
    private ApiStatAccessService apiStatAccessService;

    @Override
    public MonitorResult<List<ApiBaseStatRO>> queryTotalAccessList(ApiStatBaseRequest request) {
        ApiStatChecker.checkBase(request);
        return apiStatAccessService.queryTotalAccessList(request);
    }

    @Override
    public MonitorResult<List<ApiStatDayAccessRO>> queryDayAccessList(ApiStatBaseRequest request) {
        ApiStatChecker.checkBase(request);
        return apiStatAccessService.queryDayAccessList(request);
    }

    @Override
    public MonitorResult<List<ApiStatAccessRO>> queryStatAccessList(ApiStatBaseRequest request) {
        ApiStatChecker.checkBase(request);
        return apiStatAccessService.queryStatAccessList(request);
    }

}
