package com.treefinance.saas.monitor.biz.service;

import com.treefinance.saas.monitor.common.enumeration.EStatType;

/**
 * Created by yh-treefinance on 2017/6/30.
 */
public interface AllAlarmService {

    /**
     * 告警
     *
     * @param type
     */
    void alarm(EStatType type);
}
