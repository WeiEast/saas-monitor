package com.treefinance.saas.monitor.biz.alarm.service.handler;

import com.treefinance.saas.monitor.biz.alarm.model.AlarmConfig;
import com.treefinance.saas.monitor.biz.alarm.model.AlarmContext;

/**
 * Created by yh-treefinance on 2018/7/24.
 */
public interface AlarmHandlerChain {

    /**
     * 处理函数
     *
     * @param config
     */
    AlarmContext handle(AlarmConfig config);
}
