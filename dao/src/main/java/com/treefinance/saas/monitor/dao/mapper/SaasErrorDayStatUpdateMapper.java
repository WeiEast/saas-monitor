package com.treefinance.saas.monitor.dao.mapper;

import com.treefinance.saas.monitor.dao.entity.SaasErrorDayStat;

public interface SaasErrorDayStatUpdateMapper {
    /**
     * 插入更新日Total数据
     *
     * @param record
     * @return
     */
    int insertOrUpdateSelectiveDayError(SaasErrorDayStat record);


}