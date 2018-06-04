package com.treefinance.saas.monitor.biz.service;

import com.treefinance.saas.monitor.common.domain.dto.*;

import java.util.List;

/**
 * 统计数据更新Service
 * Created by yh-treefinance on 2017/6/1.
 */
public interface StatAccessUpdateService {

    /**
     * 批量保存商户访问数据
     *
     * @param list
     */
    void batchInsertStatAccess(List<MerchantStatAccessDTO> list);

    /**
     * 批量保存商户日访问数据
     *
     * @param list
     */
    void batchInsertStaDayAccess(List<MerchantStatDayAccessDTO> list);

    /**
     * 批量保存银行访问数据
     *
     * @param list
     */
    void batchInsertBankList(List<MerchantStatBankDTO> list);

    /**
     * 批量保存电商访问数据
     *
     * @param list
     */
    void batchInsertEcommerce(List<MerchantStatEcommerceDTO> list);

    /**
     * 批量保存邮箱访问数据
     *
     * @param list
     */
    void batchInsertMail(List<MerchantStatMailDTO> list);
    /**
     * 批量保存运营商访问数据
     *
     * @param list
     */
    void batchInsertOperator(List<MerchantStatOperatorDTO> list);
}
