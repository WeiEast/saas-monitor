package com.treefinance.saas.monitor.biz.autostat.mybatis;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.treefinance.saas.monitor.biz.autostat.mybatis.model.DbColumn;
import com.treefinance.saas.monitor.dao.mapper.AutoStatMapper;
import org.apache.commons.io.IOUtils;
import org.apache.ibatis.jdbc.SQL;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by yh-treefinance on 2018/1/25.
 */
@Component
public class MybatisService {
    private static final Logger logger = LoggerFactory.getLogger(MybatisService.class);

    @Autowired
    private SqlSessionFactoryBean sqlSessionFactoryBean;

    @Autowired
    private AutoStatMapper autoStatMapper;

    /**
     * 获取表名
     *
     * @param tableName
     * @return
     */
    public List<DbColumn> getTableColumns(String tableName) {
        List<DbColumn> list = Lists.newArrayList();
        SqlSession sqlSession = null;
        ResultSet rs = null;
        ResultSet primaryKeyRs = null;
        try {
            sqlSession = sqlSessionFactoryBean.getObject().openSession();
            Connection connection = sqlSession.getConnection();
            String catalog = connection.getCatalog();
            DatabaseMetaData databaseMetaData = connection.getMetaData();

            List<String> primaryKeys = Lists.newArrayList();
            primaryKeyRs = databaseMetaData.getPrimaryKeys(catalog, "%", tableName);
            while (primaryKeyRs.next()) {
                primaryKeys.add(primaryKeyRs.getString("COLUMN_NAME"));
            }

            rs = databaseMetaData.getColumns(catalog, "%", tableName, "%");
            while (rs.next()) {
                DbColumn dbColumn = new DbColumn();
                dbColumn.setJdbcType(rs.getInt("DATA_TYPE"));
                dbColumn.setLength(rs.getInt("COLUMN_SIZE"));
                dbColumn.setActualColumnName(rs.getString("COLUMN_NAME"));
                dbColumn.setNullable(rs.getInt("NULLABLE") == DatabaseMetaData.columnNullable);
                dbColumn.setScale(rs.getInt("DECIMAL_DIGITS"));
                dbColumn.setRemarks(rs.getString("REMARKS"));
                dbColumn.setDefaultValue(rs.getString("COLUMN_DEF"));
                dbColumn.setAutoIncrement("YES".equals(rs.getString("IS_AUTOINCREMENT")));
                dbColumn.setPrimaryKey(primaryKeys.contains(dbColumn.getActualColumnName()));
                list.add(dbColumn);
            }
        } catch (Exception e) {
            throw new RuntimeException("get table column error:table=" + tableName, e);
        } finally {
            closeQuietly(rs);
            closeQuietly(primaryKeyRs);
            IOUtils.closeQuietly(sqlSession);
        }
        return list;
    }


    /**
     * 更新数据
     *
     * @param tableName
     * @param dataList
     * @return
     */
    public int batchInsertOrUpdate(String tableName, List<Map<String, Object>> dataList) {
        List<DbColumn> dbColumns = getTableColumns(tableName);
        List<String> columnNames = dbColumns.stream().map(DbColumn::getActualColumnName).collect(Collectors.toList());

        Set<String> dataKeys = Sets.newHashSet();
        dataList.stream().forEach(map -> dataKeys.addAll(map.keySet()));
        // 取交集：仅更新需要字段
        columnNames.retainAll(dataKeys);

        List<Object> rows = Lists.newArrayList();
        Map<String, Object> paramsMap = Maps.newHashMap();

        int result = -1;
        try {
            paramsMap.put("tableName",tableName);
            paramsMap.put("columns", columnNames);
            paramsMap.put("rows", rows);

            for (Map<String, Object> data : dataList) {
                List<Object> row = Lists.newArrayList();
                for (String column : columnNames) {
                    row.add(data.get(column));
                }
                rows.add(row);
            }
            result = autoStatMapper.batchInsertOrUpdate(paramsMap);
        } finally {
            logger.info("batchInsertOrUpdate : result={}, tableName={}, columnNames={},paramsMap={}, dataList={}",
                    result, tableName, JSON.toJSONString(columnNames),
                    JSON.toJSONString(paramsMap), JSON.toJSONString(dataList));
        }
        return result;
    }


    private void closeQuietly(AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
            }
        }
    }
}
