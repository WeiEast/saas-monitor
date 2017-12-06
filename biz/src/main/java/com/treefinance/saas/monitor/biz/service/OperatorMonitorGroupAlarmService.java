package com.treefinance.saas.monitor.biz.service;

import com.treefinance.saas.monitor.common.domain.dto.OperatorMonitorAlarmConfigDTO;
import com.treefinance.saas.monitor.common.enumeration.ETaskOperatorStatType;

import java.util.Date;

/**
 * Created by haojiahong on 2017/11/13.
 */
public interface OperatorMonitorGroupAlarmService {

    /**
     * 特定运营商任务监控预警
     *
     * @param jobTime  预警任务发生时间
     * @param config
     * @param statType 数据统计维度(任务,用户)
     */
    void alarm(Date jobTime, OperatorMonitorAlarmConfigDTO config, ETaskOperatorStatType statType);

}
