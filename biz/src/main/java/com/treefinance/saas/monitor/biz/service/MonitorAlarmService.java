package com.treefinance.saas.monitor.biz.service;

import com.treefinance.saas.monitor.common.domain.dto.alarmconfig.BaseAlarmConfigDTO;
import com.treefinance.saas.monitor.common.domain.dto.alarmconfig.EmailMonitorAlarmConfigDTO;
import com.treefinance.saas.monitor.common.enumeration.ETaskStatDataType;

import java.util.Date;

/**
 * @Author: chengtong
 * @Date: 18/3/9 16:21
 */
public interface MonitorAlarmService {

    /**
     * 邮箱预警
     * @param now 当前时间
     * @param configDTO 相关配置
     * @param type  数据类型
     */
    void alarm(Date now, BaseAlarmConfigDTO configDTO, ETaskStatDataType type);

}
