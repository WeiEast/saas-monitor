package com.treefinance.saas.monitor.biz.service;

import com.treefinance.saas.monitor.common.domain.dto.OperatorMonitorAlarmConfigDTO;
import com.treefinance.saas.monitor.common.enumeration.ETaskStatDataType;

import java.util.Date;

/**
 * Created by haojiahong on 2017/11/13.
 */
public interface OperatorMonitorAllAlarmService {

    /**
     * 所有运营商任务监控预警
     *
     * @param jobTime
     * @param config
     * @param statType
     */
    void alarm(Date jobTime, OperatorMonitorAlarmConfigDTO config, ETaskStatDataType statType);
}
