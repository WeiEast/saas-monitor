package com.treefinance.saas.monitor.biz.facade;

import com.google.common.collect.Maps;
import com.treefinance.saas.monitor.biz.service.OperatorService;
import com.treefinance.saas.monitor.common.domain.dto.OperatorDTO;
import com.treefinance.saas.monitor.common.utils.DataConverterUtils;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResultBuilder;
import com.treefinance.saas.monitor.facade.domain.ro.OperatorRO;
import com.treefinance.saas.monitor.facade.service.OperatorFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by yh-treefinance on 2017/6/12.
 */
@Service("operatorFacade")
public class OperatorFacadeImpl implements OperatorFacade {
    @Autowired
    private OperatorService operatorService;

    @Override
    public MonitorResult<List<OperatorRO>> queryAll() {
        List<OperatorDTO> list = operatorService.getAll();
        List<OperatorRO> dataList = DataConverterUtils.convert(list, OperatorRO.class);
        return MonitorResultBuilder.build(dataList);
    }

    @Override
    public MonitorResult<Map<String, OperatorRO>> queryOperatorByWebsites(List<String> websites) {
        Map<String, OperatorRO> map = Maps.newHashMap();
        if (CollectionUtils.isEmpty(websites)) {
            return MonitorResultBuilder.build(map);
        }
        map = operatorService.getOperatorByWebsites(websites);
        return MonitorResultBuilder.build(map);
    }
}
