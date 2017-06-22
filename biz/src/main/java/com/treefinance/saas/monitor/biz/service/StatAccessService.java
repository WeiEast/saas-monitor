package com.treefinance.saas.monitor.biz.service;

import com.treefinance.saas.monitor.common.domain.dto.*;
import com.treefinance.saas.monitor.facade.domain.request.*;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;
import com.treefinance.saas.monitor.facade.domain.result.PageResult;
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
    PageResult<MerchantStatDayAccessRO> queryDayAccessList(MerchantStatDayAccessRequest request);

    /**
     * 查询商户访问数据
     *
     * @param request
     * @return
     */
    MonitorResult<List<MerchantStatAccessRO>> queryAccessList(MerchantStatAccessRequest request);

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
