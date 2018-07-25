package com.treefinance.saas.monitor.biz.alarm.service.handler;

import com.treefinance.saas.monitor.biz.alarm.model.AlarmConfig;
import com.treefinance.saas.monitor.biz.alarm.model.AlarmContext;

/**
 * Created by yh-treefinance on 2018/7/23.
 */
public interface AlarmHandler extends Comparable<AlarmHandler> {
    /**
     * 预警组件构建
     *
     * @param config
     * @param context
     * @return
     */
    void handle(AlarmConfig config, AlarmContext context);

    /**
     * 默认排序方法
     *
     * @param handler
     * @return
     */
    @Override
    default int compareTo(AlarmHandler handler) {
        return this.getClass().getAnnotation(Order.class).value() - handler.getClass().getAnnotation(Order.class).value();
    }
}
