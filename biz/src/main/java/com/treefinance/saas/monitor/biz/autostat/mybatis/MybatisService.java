package com.treefinance.saas.monitor.biz.autostat.mybatis;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.treefinance.saas.monitor.biz.autostat.mybatis.model.DbColumn;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by yh-treefinance on 2018/1/25.
 */
@Component
public class MybatisService implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(MybatisService.class);

    @Autowired
    private SqlSessionFactoryBean sqlSessionFactoryBean;

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
    public int insertOrUpdate(String tableName, List<Map<String, Object>> dataList) {
        List<DbColumn> dbColumns = getTableColumns(tableName);
        StringBuilder sql = generateSQL(tableName, dataList, dbColumns);

        String sqlString = sql.toString();
        SqlSession sqlSession = sqlSessionFactoryBean.getObject().openSession();
        sqlSession.
        return 0;
    }

    /**
     * 生成更新sql
     *
     * @param tableName
     * @param dataList
     * @param dbColumns
     * @return
     */
    private StringBuilder generateSQL(String tableName, List<Map<String, Object>> dataList, List<DbColumn> dbColumns) {
        List<String> columnNames = dbColumns.stream()
                .map(DbColumn::getActualColumnName)
                .collect(Collectors.toList());

        // sql 语句
        StringBuilder sql = new StringBuilder();
        // insert into table ();
        sql.append("insert into ").append(tableName);
        sql.append("( ");
        Joiner.on(",").appendTo(sql, columnNames);
        sql.append(" )");
        sql.append("VALUES ");


        String[] valuesArr = new String[columnNames.size()];
        Arrays.fill(valuesArr, "?");
        List<String> params = Arrays.asList(valuesArr);
        List<String> valueSql = Lists.newArrayList();
        for (Map<String, Object> data : dataList) {
            valueSql.add("(" + Joiner.on(",").appendTo(sql, params) + ")");
        }
        Joiner.on(",").appendTo(sql, valueSql);


        // ON DUPLICATE KEY UPDATE
        sql.append(" ON DUPLICATE KEY UPDATE ");
        List<String> updateSql = Lists.newArrayList();
        for (String columnName : columnNames) {
            updateSql.add(columnName + "=VALUES(" + columnName + ")");
        }
        Joiner.on(",").appendTo(sql, updateSql);
        return sql;
    }


    private void closeQuietly(AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("get table columns : {}", JSON.toJSONString(getTableColumns("api_stat_access"), true));
    }
}
