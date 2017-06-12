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
     * 批量保存商户访问数据
     *
     * @param list
     */
    void batchInsertStatAccess(List<MerchantStatAccessDTO> list);

    /**
     * 查询银行访问数据
     *
     * @param request
     * @return
     */
    List<MerchantStatBankDTO> queryBankList(MerchantStatBankRequest request);

    /**
     * 批量保存银行访问数据
     *
     * @param list
     */
    void batchInsertBankList(List<MerchantStatBankDTO> list);

    /**
     * 查询电商访问数据
     *
     * @param request
     * @return
     */
    List<MerchantStatEcommerceDTO> queryEcommerceList(MerchantStatEcommerceRequest request);

    /**
     * 批量保存电商访问数据
     *
     * @param list
     */
    void batchInsertEcommerce(List<MerchantStatEcommerceDTO> list);

    /**
     * 查询邮箱访问数据
     *
     * @param request
     * @return
     */
    List<MerchantStatMailDTO> queryMailList(MerchantStatMailRequest request);

    /**
     * 批量保存邮箱访问数据
     *
     * @param list
     */
    void batchInsertMail(List<MerchantStatMailDTO> list);

    /**
     * 查询运营商列表
     *
     * @param request
     * @return
     */
    List<MerchantStatOperatorDTO> queryOperatorList(MerchantStatOperaterRequest request);

    /**
     * 批量保存运营商访问数据
     *
     * @param list
     */
    void batchInsertOperator(List<MerchantStatOperatorDTO> list);
}
