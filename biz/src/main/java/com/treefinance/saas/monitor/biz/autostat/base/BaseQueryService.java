package com.treefinance.saas.monitor.biz.autostat.base;

import java.util.List;

/**
 * Created by yh-treefinance on 2018/1/23.
 */
public interface BaseQueryService<T> {
    /**
     * 获取所有
     *
     * @return
     */
    List<T> getAll();

    /**
     * 根据编码获取
     *
     * @param code
     * @return
     */
    T getByCode(String code);

    /**
     * 根据ID获取
     *
     * @param id
     * @return
     */
    T getById(Long id);
}
