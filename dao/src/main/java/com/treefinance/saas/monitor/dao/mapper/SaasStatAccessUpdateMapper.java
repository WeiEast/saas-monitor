package com.treefinance.saas.monitor.dao.mapper;

import com.treefinance.saas.monitor.dao.entity.SaasStatAccess;
import com.treefinance.saas.monitor.dao.entity.SaasStatDayAccess;

public interface SaasStatAccessUpdateMapper {
    /**
     * 插入更新日Total数据
     *
     * @param record
     * @return
     */
    int insertOrUpdateSelectiveDayTotal(SaasStatDayAccess record);

    /**
     * 插入更新Total
     *
     * @param record
     * @return
     */
    int insertOrUpdateSelectiveTotal(SaasStatAccess record);


}