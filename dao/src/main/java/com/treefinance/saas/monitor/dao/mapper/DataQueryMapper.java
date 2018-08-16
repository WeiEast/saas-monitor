package com.treefinance.saas.monitor.dao.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by yh-treefinance on 2018/7/23.
 */
public interface DataQueryMapper {
    /**
     * 动态执行查询语句
     *
     * @param sql
     * @return
     */
    List<Map<String, Object>> queryData(@Param("sql") String sql, @Param("param") Map<String, Object> paramMap);
}
