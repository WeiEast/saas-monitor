package com.treefinance.saas.monitor.dao.mapper;

import com.treefinance.saas.monitor.dao.entity.*;

public interface MerchantStatAccessUpdateMapper {
    /**
     * 插入更新日Total数据
     *
     * @param record
     * @return
     */
    int insertOrUpdateSelectiveDayTotal(MerchantStatDayAccess record);

    /**
     * 插入更新Total
     *
     * @param record
     * @return
     */
    int insertOrUpdateSelectiveTotal(MerchantStatAccess record);

    /**
     * 插入更新bank
     *
     * @param record
     * @return
     */
    int insertOrUpdateSelectiveBank(MerchantStatBank record);

    /**
     * 插入更新电商
     *
     * @param record
     * @return
     */
    int insertOrUpdateSelectiveEcommerce(MerchantStatEcommerce record);

    /**
     * 插入更新邮箱
     *
     * @param record
     * @return
     */
    int insertOrUpdateSelectiveMail(MerchantStatMail record);

    /**
     * 插入更新运营商
     * @param record
     * @return
     */
    int insertOrUpdateSelectiveOperator(MerchantStatOperator record);
}