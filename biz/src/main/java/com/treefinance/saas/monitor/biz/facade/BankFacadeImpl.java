package com.treefinance.saas.monitor.biz.facade;

import com.treefinance.saas.monitor.biz.service.BankService;
import com.treefinance.saas.monitor.common.domain.dto.BankDTO;
import com.treefinance.saas.monitor.context.component.AbstractFacade;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResultBuilder;
import com.treefinance.saas.monitor.facade.domain.ro.BankRO;
import com.treefinance.saas.monitor.facade.service.BankFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by yh-treefinance on 2017/6/19.
 */
@Service("bankFacade")
public class BankFacadeImpl extends AbstractFacade implements BankFacade {
    @Autowired
    private BankService bankService;

    @Override
    public MonitorResult<List<BankRO>> queryAll() {
        List<BankDTO> bankDTOS = bankService.queryAll();
        List<BankRO> bankROS = convert(bankDTOS, BankRO.class);
        return MonitorResultBuilder.build(bankROS);
    }
}
