package com.treefinance.saas.monitor.biz.service;

import com.treefinance.saas.monitor.common.domain.dto.EmailMonitorAlarmConfigDTO;
import com.treefinance.saas.monitor.common.enumeration.ETaskStatDataType;

import java.util.Date;

/**
 * @Author: chengtong
 * @Date: 18/3/9 16:21
 */
public interface EmailMonitorAlarmService {

    /**
     * 邮箱预警
     * @param now 当前时间
     * @param configDTO 相关配置
     * @param type  数据类型
     * @param emails 邮箱类型的列表
     */
    void alarm(Date now, EmailMonitorAlarmConfigDTO configDTO, ETaskStatDataType type, String... emails);

}
