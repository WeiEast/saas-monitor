package com.treefinance.saas.monitor.biz.service;

import com.treefinance.saas.monitor.common.domain.dto.*;
import com.treefinance.saas.monitor.facade.domain.request.*;

import java.util.List;

/**
 * Created by yh-treefinance on 2017/6/1.
 */
public interface MerchantStatAccessService {

    /**
     * 查询商户访问数据
     *
     * @param request
     * @return
     */
    List<MerchantStatAccessDTO> queryAccessList(MerchantStatAccessRequest request);

    /**
     * 查询银行访问数据
     *
     * @param request
     * @return
     */
    List<MerchantStatBankDTO> queryBankList(MerchantStatBankRequest request);

    /**
     * 查询电商访问数据
     *
     * @param request
     * @return
     */
    List<MerchantStatEcommerceDTO> queryEcommerceList(MerchantStatEcommerceRequest request);

    /**
     * 查询邮箱访问数据
     *
     * @param request
     * @return
     */
    List<MerchantStatMailDTO> queryMailList(MerchantStatMailRequest request);

    /**
     * 查询运营商列表
     *
     * @param request
     * @return
     */
    List<MerchantStatOperatorDTO> queryOperatorList(MerchantStatOperaterRequest request);

}
