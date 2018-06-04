package com.treefinance.saas.monitor.biz.facade;

import com.google.common.collect.Lists;
import com.treefinance.saas.monitor.biz.autostat.basicdata.service.BasicDataHistoryService;
import com.treefinance.saas.monitor.exception.BizException;
import com.treefinance.saas.monitor.facade.domain.request.autostat.BasicDataHistoryRequest;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResultBuilder;
import com.treefinance.saas.monitor.facade.domain.ro.autostat.BasicDataHistoryRO;
import com.treefinance.saas.monitor.facade.service.autostat.BasicDataHistoryFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by yh-treefinance on 2018/5/17.
 */
@Component("basicDataHistoryFacade")
public class BasicDataHistoryFacadeImpl implements BasicDataHistoryFacade {

    @Autowired
    private BasicDataHistoryService basicDataHistoryService;

    @Override
    public MonitorResult<List<BasicDataHistoryRO>> queryList(BasicDataHistoryRequest request) {
        if (request == null || request.getBasicDataId() == null) {
            throw new BizException("基础数据ID不能为空");
        }
        if (request.getStartTime() == null || request.getEndTime() == null) {
            throw new BizException("开始时间结束时间不能为空");
        }
        long totalCount = basicDataHistoryService.count(request);
        List<BasicDataHistoryRO> list = Lists.newArrayList();
        if (totalCount > 0) {
            list = basicDataHistoryService.queryList(request);
        }
        return MonitorResultBuilder.pageResult(request, list, totalCount);
    }
}
