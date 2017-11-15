package com.treefinance.saas.monitor.biz.service;

import com.treefinance.saas.monitor.common.enumeration.ETaskOperatorStatType;

import java.util.Date;

/**
 * Created by haojiahong on 2017/11/13.
 */
public interface OperatorMonitorAllAlarmService {

    /**
     * 所有运营商任务监控预警
     *
     * @param jobTime
     * @param dataTime
     * @param statType
     */
    void alarm(Date jobTime, Date dataTime, ETaskOperatorStatType statType);
}
