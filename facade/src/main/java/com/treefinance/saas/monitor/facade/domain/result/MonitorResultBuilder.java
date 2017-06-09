package com.treefinance.saas.monitor.facade.domain.result;

/**
 * Created by yh-treefinance on 2017/6/5.
 */
public class MonitorResultBuilder {

    public static <T> MonitorResult<T> build() {
        return new MonitorResult();
    }

    public static <T> MonitorResult<T> build(T data) {
        return new MonitorResult<T>(data);
    }

    public static <T> MonitorResult<T> build(String errorMsg) {
        return new MonitorResult<T>(errorMsg);
    }

    public static <T> MonitorResult<T> build(long timestamp, String errorMsg, T data) {
        return new MonitorResult<T>(timestamp, errorMsg, data);
    }
}
