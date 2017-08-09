package com.treefinance.saas.monitor.biz.service;

import com.treefinance.saas.monitor.common.enumeration.EStatType;

import java.util.Date;
import java.util.List;

/**
 * Created by yh-treefinance on 2017/6/30.
 */
public interface AllAlarmService {

    /**
     * 告警
     *
     * @param type
     * @param alarmTimes
     */
    void alarm(EStatType type, List<Date> alarmTimes);
}
