package com.treefinance.saas.monitor.biz.facade;

import com.treefinance.saas.monitor.biz.service.EcommerceService;
import com.treefinance.saas.monitor.common.domain.dto.EcommerceDTO;
import com.treefinance.saas.monitor.context.component.AbstractFacade;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResultBuilder;
import com.treefinance.saas.monitor.facade.domain.ro.EcommerceRO;
import com.treefinance.saas.monitor.facade.service.EcommerceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by yh-treefinance on 2017/6/12.
 */
@Service("ecommerceFacade")
public class EcommerceFacadeImpl extends AbstractFacade implements EcommerceFacade {
    @Autowired
    private EcommerceService ecommerceService;

    @Override
    public MonitorResult<List<EcommerceRO>> queryAll() {
        List<EcommerceDTO> list = ecommerceService.getAll();
        List<EcommerceRO> dataList = convert(list, EcommerceRO.class);
        return MonitorResultBuilder.build(dataList);
    }


}
