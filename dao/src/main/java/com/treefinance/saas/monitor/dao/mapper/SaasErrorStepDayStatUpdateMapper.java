package com.treefinance.saas.monitor.dao.mapper;

import com.treefinance.saas.monitor.dao.entity.SaasErrorStepDayStat;

public interface SaasErrorStepDayStatUpdateMapper {
    /**
     * 插入更新日Total数据
     *
     * @param record
     * @return
     */
    int insertOrUpdateSelectiveDayError(SaasErrorStepDayStat record);


}