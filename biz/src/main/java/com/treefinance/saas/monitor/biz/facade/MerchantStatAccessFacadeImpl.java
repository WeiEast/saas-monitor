package com.treefinance.saas.monitor.biz.facade;

import com.treefinance.saas.monitor.biz.service.StatAccessService;
import com.treefinance.saas.monitor.facade.checker.MerchantStatChecker;
import com.treefinance.saas.monitor.facade.domain.request.*;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;
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
    private StatAccessService statAccessService;

    @Override
    public MonitorResult<List<MerchantStatDayAccessRO>> queryDayAccessList(MerchantStatDayAccessRequest request) {
        MerchantStatChecker.checkDayAccessRequest(request);
        return statAccessService.queryDayAccessList(request);
    }

    @Override
    public MonitorResult<List<MerchantStatDayAccessRO>> queryDayAccessListNoPage(MerchantStatDayAccessRequest request) {
        MerchantStatChecker.checkDayAccessRequest(request);
        return statAccessService.queryDayAccessListNoPage(request);
    }

    @Override
    public MonitorResult<List<MerchantStatDayAccessRO>> queryAllDayAccessList(MerchantStatDayAccessRequest request) {
        MerchantStatChecker.checkAllDayAccessRequest(request);
        return statAccessService.queryAllDayAccessList(request);
    }

    @Override
    public MonitorResult<List<MerchantStatDayAccessRO>> queryAllDayAccessListNoPage(MerchantStatDayAccessRequest request) {
        MerchantStatChecker.checkAllDayAccessRequest(request);
        return statAccessService.queryAllDayAccessListNoPage(request);
    }

    @Override
    public MonitorResult<List<MerchantStatAccessRO>> queryAccessList(MerchantStatAccessRequest request) {
        MerchantStatChecker.checkAccessRequest(request);
        return statAccessService.queryAccessList(request);
    }

    @Override
    public MonitorResult<List<MerchantStatAccessRO>> queryAllAccessList(MerchantStatAccessRequest request) {
        MerchantStatChecker.checkAllAccessRequest(request);
        return statAccessService.queryAllAccessList(request);
    }

    @Override
    public MonitorResult<List<MerchantStatAccessRO>> queryAllSuccessAccessList(MerchantStatAccessRequest request) {
        MerchantStatChecker.checkAllAccessRequest(request);
        return statAccessService.queryAllSuccessAccessList(request);
    }

    @Override
    public MonitorResult<List<MerchantStatBankRO>> queryBankList(MerchantStatBankRequest request) {
        MerchantStatChecker.checkBankRequest(request);
        return statAccessService.queryBankList(request);
    }

    @Override
    public MonitorResult<List<MerchantStatEcommerceRO>> queryEcommerceList(MerchantStatEcommerceRequest request) {
        MerchantStatChecker.checkEcommerceRequest(request);
        return statAccessService.queryEcommerceList(request);
    }

    @Override
    public MonitorResult<List<MerchantStatMailRO>> queryMailList(MerchantStatMailRequest request) {
        MerchantStatChecker.checkMailRequest(request);
        return statAccessService.queryMailList(request);
    }

    @Override
    public MonitorResult<List<MerchantStatOperatorRO>> queryOperatorList(MerchantStatOperaterRequest request) {
        MerchantStatChecker.checkOperaterRequest(request);
        return statAccessService.queryOperatorList(request);
    }

    @Override
    public MonitorResult<List<SaasErrorStepDayStatRO>> querySaasErrorStepDayStatListNoPage(SaasErrorStepDayStatRequest request) {
        MerchantStatChecker.checkErrorDayStatRequest(request);
        return statAccessService.querySaasErrorStepDayStatListNoPage(request);
    }
}
