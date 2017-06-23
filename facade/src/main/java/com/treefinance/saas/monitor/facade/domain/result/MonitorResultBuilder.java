package com.treefinance.saas.monitor.facade.domain.result;

import com.treefinance.saas.monitor.facade.domain.base.PageRequest;

import java.util.List;

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

    public static <T> MonitorResult<T> pageResult(PageRequest request, T data, long totalCount){
        return new MonitorResult<T>(request,data,totalCount);
    }
}
