package com.treefinance.saas.monitor.biz.service;

import java.util.Date;

/**
 * Created by haojiahong on 2017/11/13.
 */
public interface OperatorTaskGroupAlarmService {

    /**
     * 特定运营商任务监控预警
     *
     * @param jobTime  预警任务发生时间
     * @param dataTime 预警任务发生时,需查询的数据时刻
     */
    void alarm(Date jobTime, Date dataTime);

}
