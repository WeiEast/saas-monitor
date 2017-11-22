package com.treefinance.saas.monitor.biz.service;

import com.treefinance.saas.monitor.common.enumeration.EBizType;

import java.util.Date;

/**
 * Created by haojiahong on 2017/11/17.
 */
public interface TaskExistMonitorAlarmService {

    void alarmNoTask(Date startTime, Date endTime);

    void alarmNoSuccessTask(Date startTime, Date endTime);

    void alarmNoSuccessTaskWithType(Date startTime, Date endTime, EBizType bizType);
}
