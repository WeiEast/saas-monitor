package com.treefinance.saas.monitor.biz.service.impl;

import com.treefinance.saas.monitor.biz.service.ApiStatAccessService;
import com.treefinance.saas.monitor.context.component.AbstractService;
import com.treefinance.saas.monitor.dao.entity.ApiStatAccess;
import com.treefinance.saas.monitor.dao.entity.ApiStatAccessCriteria;
import com.treefinance.saas.monitor.dao.entity.ApiStatMerchantDayAccess;
import com.treefinance.saas.monitor.dao.entity.ApiStatMerchantDayAccessCriteria;
import com.treefinance.saas.monitor.dao.entity.ApiStatTotalAccess;
import com.treefinance.saas.monitor.dao.entity.ApiStatTotalAccessCriteria;
import com.treefinance.saas.monitor.dao.mapper.ApiStatAccessMapper;
import com.treefinance.saas.monitor.dao.mapper.ApiStatMerchantDayAccessMapper;
import com.treefinance.saas.monitor.dao.mapper.ApiStatTotalAccessMapper;
import com.treefinance.saas.monitor.facade.domain.request.ApiStatBaseRequest;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResultBuilder;
import com.treefinance.saas.monitor.facade.domain.ro.stat.api.ApiBaseStatRO;
import com.treefinance.saas.monitor.facade.domain.ro.stat.api.ApiStatAccessRO;
import com.treefinance.saas.monitor.facade.domain.ro.stat.api.ApiStatDayAccessRO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by haojiahong on 2017/7/7.
 */
@Service
public class ApiStatAccessServiceImpl extends AbstractService implements ApiStatAccessService {

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
        List<ApiBaseStatRO> dataList = convert(apiStatAccessList, ApiBaseStatRO.class);
        return MonitorResultBuilder.build(dataList);
    }

    @Override
    public MonitorResult<List<ApiStatDayAccessRO>> queryDayAccessList(ApiStatBaseRequest request) {
        ApiStatMerchantDayAccessCriteria criteria = new ApiStatMerchantDayAccessCriteria();
        criteria.setOrderByClause("dataTime desc");
        criteria.createCriteria().andDataTimeBetween(request.getStartDate(), request.getEndDate());
        List<ApiStatMerchantDayAccess> apiStatMerchantDayAccessList = apiStatMerchantDayAccessMapper.selectByExample(criteria);
        List<ApiStatDayAccessRO> dataList = convert(apiStatMerchantDayAccessList, ApiStatDayAccessRO.class);
        return MonitorResultBuilder.build(dataList);
    }

    @Override
    public MonitorResult<List<ApiStatAccessRO>> queryStatAccessList(ApiStatBaseRequest request) {
        ApiStatAccessCriteria criteria = new ApiStatAccessCriteria();
        criteria.setOrderByClause("dataTime desc");
        criteria.createCriteria().andDataTimeBetween(request.getStartDate(), request.getEndDate());
        List<ApiStatAccess> apiStatAccessList = apiStatAccessMapper.selectByExample(criteria);
        List<ApiStatAccessRO> dataList = convert(apiStatAccessList, ApiStatAccessRO.class);
        return MonitorResultBuilder.build(dataList);
    }

    @Override
    public MonitorResult<Boolean> batchInsertApiStatTotalAccess(List<ApiBaseStatRO> list) {
        if (CollectionUtils.isNotEmpty(list)) {
            List<ApiStatTotalAccess> accessList = convert(list, ApiStatTotalAccess.class);
            for (ApiStatTotalAccess access : accessList) {
                apiStatTotalAccessMapper.insertOrUpdateBySelective(access);
            }
        }
        return MonitorResultBuilder.build(true);
    }

    @Override
    public MonitorResult<Boolean> batchInsertApiStatMerchantDayAccess(List<ApiStatDayAccessRO> list) {
        if (CollectionUtils.isNotEmpty(list)) {
            List<ApiStatMerchantDayAccess> accessList = convert(list, ApiStatMerchantDayAccess.class);
            for (ApiStatMerchantDayAccess access : accessList) {
                apiStatMerchantDayAccessMapper.insertOrUpdateBySelective(access);
            }
        }
        return MonitorResultBuilder.build(true);
    }

    @Override
    public MonitorResult<Boolean> batchInsertApiStatAccess(List<ApiStatAccessRO> list) {
        if (CollectionUtils.isNotEmpty(list)) {
            List<ApiStatAccess> accessList = convert(list, ApiStatAccess.class);
            for (ApiStatAccess access : accessList) {
                apiStatAccessMapper.insertOrUpdateBySelective(access);
            }
        }
        return MonitorResultBuilder.build(true);
    }
}
