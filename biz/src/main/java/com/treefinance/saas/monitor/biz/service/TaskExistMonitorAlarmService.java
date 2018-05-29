package com.treefinance.saas.monitor.biz.service;

import com.treefinance.saas.monitor.common.domain.dto.TaskExistAlarmNoSuccessTaskConfigDTO;
import com.treefinance.saas.monitor.common.domain.dto.TaskExistAlarmNoTaskConfigDTO;

import java.util.Date;

/**
 * Created by haojiahong on 2017/11/17.
 */
public interface TaskExistMonitorAlarmService {

    void alarmNoSuccessTaskWithConfig(Date startTime, Date endTime, TaskExistAlarmNoSuccessTaskConfigDTO config);

    void alarmNoTaskWithConfig(Date startTime, Date endTime, TaskExistAlarmNoTaskConfigDTO config);
}
