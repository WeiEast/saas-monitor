package com.treefinance.saas.monitor.dao.mapper;

import java.util.Map;

/**
 * Created by yh-treefinance on 2018/1/29.
 */
public interface AutoStatMapper {

    /**
     * 批量更新
     *
     * @param params
     * @return
     */
    int batchInsertOrUpdate(Map<String, Object> params);
}
