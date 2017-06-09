package com.treefinance.saas.monitor.biz.facade;

import com.treefinance.saas.monitor.biz.service.MerchantStatAccessService;
import com.treefinance.saas.monitor.common.domain.dto.*;
import com.treefinance.saas.monitor.common.utils.DataConverterUtils;
import com.treefinance.saas.monitor.facade.checker.MerchantStatChecker;
import com.treefinance.saas.monitor.facade.domain.request.*;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResultBuilder;
import com.treefinance.saas.monitor.facade.domain.ro.stat.*;
import com.treefinance.saas.monitor.facade.service.stat.MerchantStatAccessFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by yh-treefinance on 2017/5/31.
 */
@Service("merchantStatAccessFacade")
public class MerchantStatAccessFacadeImpl implements MerchantStatAccessFacade {
    @Autowired
    private MerchantStatAccessService merchantStatAccessService;

    @Override
    public MonitorResult<List<MerchantStatAccessRO>> queryAccessList(MerchantStatAccessRequest request) {
        MerchantStatChecker.checkAccessRequest(request);
        List<MerchantStatAccessDTO> list = merchantStatAccessService.queryAccessList(request);
        List<MerchantStatAccessRO> data = DataConverterUtils.convert(list, MerchantStatAccessRO.class);
        return MonitorResultBuilder.build(data);
    }

    @Override
    public MonitorResult<List<MerchantStatBankRO>> queryBankList(MerchantStatBankRequest request) {
        MerchantStatChecker.checkBankRequest(request);
        List<MerchantStatBankDTO> list = merchantStatAccessService.queryBankList(request);
        List<MerchantStatBankRO> data = DataConverterUtils.convert(list, MerchantStatBankRO.class);
        return MonitorResultBuilder.build(data);
    }

    @Override
    public MonitorResult<List<MerchantStatEcommerceRO>> queryEcommerceList(MerchantStatEcommerceRequest request) {
        MerchantStatChecker.checkEcommerceRequest(request);
        List<MerchantStatEcommerceDTO> list = merchantStatAccessService.queryEcommerceList(request);
        List<MerchantStatEcommerceRO> data = DataConverterUtils.convert(list, MerchantStatEcommerceRO.class);
        return MonitorResultBuilder.build(data);
    }

    @Override
    public MonitorResult<List<MerchantStatMailRO>> queryMailList(MerchantStatMailRequest request) {
        MerchantStatChecker.checkMailRequest(request);
        List<MerchantStatMailDTO> list = merchantStatAccessService.queryMailList(request);
        List<MerchantStatMailRO> data = DataConverterUtils.convert(list, MerchantStatMailRO.class);
        return MonitorResultBuilder.build(data);
    }

    @Override
    public MonitorResult<List<MerchantStatOperatorRO>> queryOperatorList(MerchantStatOperaterRequest request) {
        MerchantStatChecker.checkOperaterRequest(request);
        List<MerchantStatOperatorDTO> list = merchantStatAccessService.queryOperatorList(request);
        List<MerchantStatOperatorRO> data = DataConverterUtils.convert(list, MerchantStatOperatorRO.class);
        return MonitorResultBuilder.build(data);
    }
}
