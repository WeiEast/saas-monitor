package com.treefinance.saas.monitor.biz.autostat.basicdata.service.impl;

import com.treefinance.saas.monitor.biz.autostat.basicdata.service.BasicDataHistoryService;
import com.treefinance.saas.monitor.context.component.AbstractService;
import com.treefinance.saas.monitor.dao.entity.AsBasicDataHistory;
import com.treefinance.saas.monitor.dao.entity.AsBasicDataHistoryCriteria;
import com.treefinance.saas.monitor.dao.mapper.AsBasicDataHistoryMapper;
import com.treefinance.saas.monitor.facade.domain.request.autostat.BasicDataHistoryRequest;
import com.treefinance.saas.monitor.facade.domain.ro.autostat.BasicDataHistoryRO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by yh-treefinance on 2018/5/17.
 */
@Component
public class BasicDataHistoryServiceImpl extends AbstractService implements BasicDataHistoryService {

    @Autowired
    private AsBasicDataHistoryMapper basicDataHistoryMapper;

    @Override
    public List<BasicDataHistoryRO> queryList(BasicDataHistoryRequest request) {
        AsBasicDataHistoryCriteria criteria = new AsBasicDataHistoryCriteria();
        criteria.setLimit(request.getPageSize());
        criteria.setOffset(request.getOffset());
        AsBasicDataHistoryCriteria.Criteria _criteria = criteria.createCriteria()
                .andBasicDataIdEqualTo(request.getBasicDataId())
                .andDataTimeBetween(request.getStartTime(), request.getEndTime());
        if (StringUtils.isNotEmpty(request.getDataId())) {
            _criteria.andDataIdLike("%" + request.getDataId().concat("%"));
        }
        List<AsBasicDataHistory> list = basicDataHistoryMapper.selectPaginationByExample(criteria);
        return convert(list, BasicDataHistoryRO.class);
    }

    @Override
    public long count(BasicDataHistoryRequest request) {
        AsBasicDataHistoryCriteria criteria = new AsBasicDataHistoryCriteria();

        AsBasicDataHistoryCriteria.Criteria _criteria = criteria.createCriteria()
                .andBasicDataIdEqualTo(request.getBasicDataId())
                .andDataTimeBetween(request.getStartTime(), request.getEndTime());
        if (StringUtils.isNotEmpty(request.getDataId())) {
            _criteria.andDataIdLike("%" + request.getDataId().concat("%"));
        }
        return basicDataHistoryMapper.countByExample(criteria);
    }
}
