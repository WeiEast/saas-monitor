package com.treefinance.saas.monitor.biz.service;

import com.treefinance.saas.monitor.common.domain.dto.EcommerceMonitorAlarmConfigDTO;
import com.treefinance.saas.monitor.common.enumeration.ETaskStatDataType;

import java.util.Date;

/**
 * Created by haojiahong on 2018/1/17.
 */
public interface EcommerceMonitorAllAlarmService {
    /**
     * 总电商监控预警
     *
     * @param jobTime
     * @param config
     * @param statType
     */
    void alarm(Date jobTime, EcommerceMonitorAlarmConfigDTO config, ETaskStatDataType statType);
}
