package com.treefinance.saas.monitor.biz.service;

import java.util.Date;

/**
 * Created by haojiahong on 2017/11/17.
 */
public interface TaskExistMonitorAlarmService {

    void alarmNoTask(Date startTime, Date endTime);

    void alarmNoSuccessTask(Date startTime, Date endTime);

}
