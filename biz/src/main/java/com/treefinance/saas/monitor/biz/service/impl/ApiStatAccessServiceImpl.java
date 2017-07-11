package com.treefinance.saas.monitor.biz.service.impl;

import com.treefinance.saas.monitor.biz.service.ApiStatAccessService;
import com.treefinance.saas.monitor.common.utils.DataConverterUtils;
import com.treefinance.saas.monitor.dao.entity.*;
import com.treefinance.saas.monitor.dao.mapper.ApiStatAccessMapper;
import com.treefinance.saas.monitor.dao.mapper.ApiStatMerchantDayAccessMapper;
import com.treefinance.saas.monitor.dao.mapper.ApiStatTotalAccessMapper;
import com.treefinance.saas.monitor.facade.domain.request.ApiStatBaseRequest;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResultBuilder;
import com.treefinance.saas.monitor.facade.domain.ro.stat.api.ApiBaseStatRO;
import com.treefinance.saas.monitor.facade.domain.ro.stat.api.ApiStatAccessRO;
import com.treefinance.saas.monitor.facade.domain.ro.stat.api.ApiStatDayAccessRO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by haojiahong on 2017/7/7.
 */
@Service
public class ApiStatAccessServiceImpl implements ApiStatAccessService {

    @Autowired
    private ApiStatAccessMapper apiStatAccessMapper;
    @Autowired
    private ApiStatTotalAccessMapper apiStatTotalAccessMapper;
    @Autowired
    private ApiStatMerchantDayAccessMapper apiStatMerchantDayAccessMapper;

    @Override
    public MonitorResult<List<ApiBaseStatRO>> queryTotalAccessList(ApiStatBaseRequest request) {
        ApiStatTotalAccessCriteria criteria = new ApiStatTotalAccessCriteria();
        criteria.setOrderByClause("dataTime desc");
        criteria.createCriteria().andDataTimeBetween(request.getStartDate(), request.getEndDate());
        List<ApiStatTotalAccess> apiStatAccessList = apiStatTotalAccessMapper.selectByExample(criteria);
        List<ApiBaseStatRO> dataList = DataConverterUtils.convert(apiStatAccessList, ApiBaseStatRO.class);
        return MonitorResultBuilder.build(dataList);
    }

    @Override
    public MonitorResult<List<ApiStatDayAccessRO>> queryDayAccessList(ApiStatBaseRequest request) {
        ApiStatMerchantDayAccessCriteria criteria = new ApiStatMerchantDayAccessCriteria();
        criteria.setOrderByClause("dataTime desc");
        criteria.createCriteria().andDataTimeBetween(request.getStartDate(), request.getEndDate());
        List<ApiStatMerchantDayAccess> apiStatMerchantDayAccessList = apiStatMerchantDayAccessMapper.selectByExample(criteria);
        List<ApiStatDayAccessRO> dataList = DataConverterUtils.convert(apiStatMerchantDayAccessList, ApiStatDayAccessRO.class);
        return MonitorResultBuilder.build(dataList);
    }

    @Override
    public MonitorResult<List<ApiStatAccessRO>> queryStatAccessList(ApiStatBaseRequest request) {
        ApiStatAccessCriteria criteria = new ApiStatAccessCriteria();
        criteria.setOrderByClause("dataTime desc");
        criteria.createCriteria().andDataTimeBetween(request.getStartDate(), request.getEndDate());
        List<ApiStatAccess> apiStatAccessList = apiStatAccessMapper.selectByExample(criteria);
        List<ApiStatAccessRO> dataList = DataConverterUtils.convert(apiStatAccessList, ApiStatAccessRO.class);
        return MonitorResultBuilder.build(dataList);
    }


}
