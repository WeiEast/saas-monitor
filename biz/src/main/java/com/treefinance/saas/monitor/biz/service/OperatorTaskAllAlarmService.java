package com.treefinance.saas.monitor.biz.service;

import java.util.Date;

/**
 * Created by haojiahong on 2017/11/13.
 */
public interface OperatorTaskAllAlarmService {

    /**
     * 所有运营商任务监控预警
     * @param jobTime
     * @param dataTime
     */
    void alarm(Date jobTime, Date dataTime);
}
