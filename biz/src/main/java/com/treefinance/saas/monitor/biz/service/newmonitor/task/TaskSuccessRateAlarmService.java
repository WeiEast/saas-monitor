package com.treefinance.saas.monitor.biz.service.newmonitor.task;

import com.treefinance.saas.monitor.common.domain.dto.TaskSuccessRateAlarmConfigDTO;
import com.treefinance.saas.monitor.common.enumeration.EBizType;

import java.util.Date;

/**
 * Created by haojiahong on 2017/11/24.
 */
public interface TaskSuccessRateAlarmService {

    void alarm(EBizType bizType, TaskSuccessRateAlarmConfigDTO config, Date jobTime);
}
