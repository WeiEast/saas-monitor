package com.treefinance.saas.monitor.biz.service;

import com.treefinance.saas.monitor.common.enumeration.EStatType;

/**
 * Created by yh-treefinance on 2017/6/30.
 */
public interface AlarmService {

    /**
     * 告警
     *
     * @param appId
     */
    void alarm(String appId, EStatType type);
}
