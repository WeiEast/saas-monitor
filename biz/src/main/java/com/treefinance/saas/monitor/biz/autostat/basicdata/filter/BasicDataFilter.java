package com.treefinance.saas.monitor.biz.autostat.basicdata.filter;

import java.util.List;

/**
 * Created by yh-treefinance on 2018/1/23.
 */
public interface BasicDataFilter<T> {
    /**
     * 数据过滤
     *
     * @param data
     * @return
     */
    void doFilter(List<T> data);
}
