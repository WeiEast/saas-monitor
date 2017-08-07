package com.treefinance.saas.monitor.biz.service;

import com.treefinance.saas.monitor.facade.domain.request.*;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;
import com.treefinance.saas.monitor.facade.domain.ro.stat.*;

import java.util.List;

/**
 * Created by yh-treefinance on 2017/6/1.
 */
public interface StatAccessService {

    /**
     * 查询商户访问数据
     *
     * @param request
     * @return
     */
    MonitorResult<List<MerchantStatDayAccessRO>> queryDayAccessList(MerchantStatDayAccessRequest request);

    /**
     * 查询商户日访问数据,不分页
     *
     * @param request
     * @return
     */
    MonitorResult<List<MerchantStatDayAccessRO>> queryDayAccessListNoPage(MerchantStatDayAccessRequest request);

    /**
     * 查询所有商户日访问数据
     *
     * @param request
     * @return
     */
    MonitorResult<List<MerchantStatDayAccessRO>> queryAllDayAccessList(MerchantStatDayAccessRequest request);

    /**
     * 查询所有商户日访问数据,不分页
     *
     * @param request
     * @return
     */
    MonitorResult<List<MerchantStatDayAccessRO>> queryAllDayAccessListNoPage(MerchantStatDayAccessRequest request);

    /**
     * 查询商户访问数据
     *
     * @param request
     * @return
     */
    MonitorResult<List<MerchantStatAccessRO>> queryAccessList(MerchantStatAccessRequest request);

    /**
     * 查询所有商户访问数据
     *
     * @param request
     * @return
     */
    MonitorResult<List<MerchantStatAccessRO>> queryAllAccessList(MerchantStatAccessRequest request);

    /**
     * 查询银行访问数据
     *
     * @param request
     * @return
     */
    MonitorResult<List<MerchantStatBankRO>> queryBankList(MerchantStatBankRequest request);

    /**
     * 查询电商访问数据
     *
     * @param request
     * @return
     */
    MonitorResult<List<MerchantStatEcommerceRO>> queryEcommerceList(MerchantStatEcommerceRequest request);

    /**
     * 查询邮箱访问数据
     *
     * @param request
     * @return
     */
    MonitorResult<List<MerchantStatMailRO>> queryMailList(MerchantStatMailRequest request);

    /**
     * 查询运营商列表
     *
     * @param request
     * @return
     */
    MonitorResult<List<MerchantStatOperatorRO>> queryOperatorList(MerchantStatOperaterRequest request);
}
