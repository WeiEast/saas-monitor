package com.treefinance.saas.monitor.biz.service;

import com.treefinance.saas.monitor.common.domain.dto.MerchantStatEcommerceDTO;
import com.treefinance.saas.monitor.common.domain.dto.MerchantStatMailDTO;
import com.treefinance.saas.monitor.common.domain.dto.MerchantStatOperatorDTO;

import java.util.List;

/**
 * 统计数据更新Service
 * Created by yh-treefinance on 2017/6/1.
 */
public interface StatAccessUpdateService {

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
