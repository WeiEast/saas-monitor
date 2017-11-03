package com.treefinance.saas.monitor.dao.mapper;

import com.treefinance.saas.monitor.dao.entity.AllOperatorStatDayAccess;
import com.treefinance.saas.monitor.dao.entity.OperatorStatAccess;
import com.treefinance.saas.monitor.dao.entity.OperatorStatDayAccess;

public interface OperatorStatAccessUpdateMapper {
    /**
     * 插入更新所有运营商日统计数据
     *
     * @param record
     * @return
     */
    int insertOrUpdateSelectiveAllDayData(AllOperatorStatDayAccess record);


    /**
     * 插入更新运营商日统计数据
     *
     * @param record
     * @return
     */
    int insertOrUpdateSelectiveDayData(OperatorStatDayAccess record);


    /**
     * 插入更新运营商特定时间统计数据
     *
     * @param record
     * @return
     */
    int insertOrUpdateSelectiveIntervalData(OperatorStatAccess record);


}