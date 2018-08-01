package com.treefinance.saas.monitor.biz.alarm.service.handler.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.treefinance.saas.monitor.biz.alarm.expression.ExpressionParser;
import com.treefinance.saas.monitor.biz.alarm.model.AlarmConfig;
import com.treefinance.saas.monitor.biz.alarm.model.AlarmContext;
import com.treefinance.saas.monitor.biz.alarm.service.handler.AlarmHandler;
import com.treefinance.saas.monitor.biz.alarm.service.handler.Order;
import com.treefinance.saas.monitor.dao.entity.AsAlarmQuery;
import com.treefinance.saas.monitor.dao.mapper.DataQueryMapper;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 数据查询组件
 * Created by yh-treefinance on 2018/7/20.
 */
@Order(2)
@Component
public class DataQueryHandler implements AlarmHandler {
    /**
     * mysql - keywords
     */
    private String[] MYSQL_KEYWORDS = new String[]{"AND", "OR", "ADD", "ALL", "ALTER", "ANALYZE", "ASC", "ASENSITIVE", "BETWEEN", "BIGINT", "BLOB", "BOTH", "BY", "CALL", "CASCADE", "CASE", "CHANGE", "CHAR", "CHARACTER", "CHECK", "COLLATE", "COLUMN", "CONDITION", "CONNECTION", "CONSTRAINT", "CONTINUE", "CONVERT", "CREATE", "CROSS", "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_USER", "CURSOR", "DATABASE", "DATABASES", "DAY_HOUR", "DAY_MICROSECOND", "DAY_MINUTE", "DAY_SECOND", "DEC", "DECIMAL", "DECLARE", "DEFAULT", "DELAYED", "DELETE", "DESC", "DESCRIBE", "DETERMINISTIC", "DISTINCT", "DISTINCTROW", "DIV", "DOUBLE", "DROP", "DUAL", "EACH", "ELSE", "ELSEIF", "ENCLOSED", "ESCAPED", "EXISTS", "EXIT", "EXPLAIN", "FALSE", "FETCH", "FLOAT", "FLOAT4", "FLOAT8", "FOR", "FORCE", "FOREIGN", "FROM", "FULLTEXT", "GOTO", "GRANT", "GROUP", "HAVING", "HIGH_PRIORITY", "HOUR_MICROSECOND", "HOUR_MINUTE", "HOUR_SECOND", "IF", "IGNORE", "IN", "INDEX", "INFILE", "INNER", "INOUT", "INSENSITIVE", "INSERT", "INT", "INT1", "INT2", "INT3", "INT4", "INT8", "INTEGER", "INTERVAL", "INTO", "IS", "ITERATE", "JOIN", "KEY", "KEYS", "KILL", "LABEL", "LEADING", "LEAVE", "LEFT", "LIKE", "LIMIT", "LINEAR", "LINES", "LOAD", "LOCALTIME", "LOCALTIMESTAMP", "LOCK", "LONG", "LONGBLOB", "LONGTEXT", "LOOP", "LOW_PRIORITY", "MATCH", "MEDIUMBLOB", "MEDIUMINT", "MEDIUMTEXT", "MIDDLEINT", "MINUTE_MICROSECOND", "MINUTE_SECOND", "MOD", "MODIFIES", "NATURAL", "NOT", "NO_WRITE_TO_BINLOG", "NULL", "NUMERIC", "ON", "OPTIMIZE", "OPTION", "OPTIONALLY", "OR", "ORDER", "OUT", "OUTER", "OUTFILE", "PRECISION", "PRIMARY", "PROCEDURE", "PURGE", "RAID0", "RANGE", "READ", "READS", "REAL", "REFERENCES", "REGEXP", "RELEASE", "RENAME", "REPEAT", "REPLACE", "REQUIRE", "RESTRICT", "RETURN", "REVOKE", "RIGHT", "RLIKE", "SCHEMA", "SCHEMAS", "SECOND_MICROSECOND", "SELECT", "SENSITIVE", "SEPARATOR", "SET", "SHOW", "SMALLINT", "SPATIAL", "SPECIFIC", "SQL", "SQLEXCEPTION", "SQLSTATE", "SQLWARNING", "SQL_BIG_RESULT", "SQL_CALC_FOUND_ROWS", "SQL_SMALL_RESULT", "SSL", "STARTING", "STRAIGHT_JOIN", "TABLE", "TERMINATED", "THEN", "TINYBLOB", "TINYINT", "TINYTEXT", "TO", "TRAILING", "TRIGGER", "TRUE", "UNDO", "UNION", "UNIQUE", "UNLOCK", "UNSIGNED", "UPDATE", "USAGE", "USE", "USING", "UTC_DATE", "UTC_TIME", "UTC_TIMESTAMP", "VALUES", "VARBINARY", "VARCHAR", "VARCHARACTER", "VARYING", "WHEN", "WHERE", "WHILE", "WITH", "WRITE", "X509", "XOR", "YEAR_MONTH", "ZEROFILL"};
    /**
     * logger
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private DataQueryMapper dataQueryMapper;
    /**
     * expression
     */
    @Resource(name = "spelExpressionParser")
    private ExpressionParser expressionParser;

    public List<Map<String, Object>> queryData(String sql, Map<String, Object> params) {
        List<Map<String, Object>> dataList = Lists.newArrayList();
        try {
            dataList = dataQueryMapper.queryData(sql, params);
        } finally {
            logger.info("queryData : sql={} , params={},data={}", sql, JSON.toJSONString(params), JSON.toJSONString(dataList));
        }
        return dataList;
    }

    @Override
    public void handle(AlarmConfig config, AlarmContext context) {
        List<AsAlarmQuery> alarmQueries = config.getAlarmQueries();
        if (CollectionUtils.isEmpty(alarmQueries)) {
            logger.info("alarm query list is empty : config={}", JSON.toJSONString(config));
            return;
        }
        List<AsAlarmQuery> sortedAlarmQueries = alarmQueries.stream()
                .sorted(Comparator.comparing(AsAlarmQuery::getQueryIndex)).collect(Collectors.toList());
        List<Map<String, Object>> groups = context.groups();
        for (AsAlarmQuery query : sortedAlarmQueries) {
            String sql = query.getQuerySql();
            String code = query.getResultCode();
            if (StringUtils.isEmpty(sql)) {
                continue;
            }
            // 利用关键字格式化sql
            sql = formatSQL(sql);
            // 提取sql 语句中表达式
            Pattern pattern = Pattern.compile("(#\\S{1,}\\(.+\\)\\s?)|(#\\S{1,}\\s?)");
            List<String> sqlparts = pattern.splitAsStream(sql).collect(Collectors.toList());
            List<String> expressions = Lists.newArrayList();
            Matcher expressionMatcher = pattern.matcher(sql);
            while (expressionMatcher.find()) {
                expressions.add(expressionMatcher.group());
            }

            // 拼接动态sql
            StringBuffer sqlBf = new StringBuffer();
            int size = sqlparts.size();
            Map<String, String> dynamicMap = Maps.newHashMap();
            for (int i = 0; i < size; i++) {
                String sqlpart = sqlparts.get(i);
                sqlBf.append(sqlpart);
                if (i < expressions.size()) {
                    String expression = expressions.get(i);
                    String key = "p" + i;
                    String dynamicKey = "param." + key;
                    dynamicMap.put(key, expression);
                    sqlBf.append("#{" + dynamicKey + "}");
                }
            }
            // 计算sql中表达式值
            String dynamicSql = sqlBf.toString();

            for (Map<String, Object> paramMap : groups) {
                dynamicMap.keySet().forEach(paramKey -> {
                    String expression = dynamicMap.get(paramKey);
                    paramMap.put(paramKey, expressionParser.parse(expression, paramMap));
                });
                List<Map<String, Object>> dataList = queryData(dynamicSql, paramMap);
                paramMap.put(code, dataList);
            }
        }

    }

    private String formatSQL(String sql) {
        // 逗号前后加空格
        sql = sql.replaceAll("\\,", " , ")
                .replaceAll("\\*", " * ")
                .replaceAll("/", " / ");
        // mysql 关键字换行
        for (String keyWord : MYSQL_KEYWORDS) {
            if (sql.toLowerCase().indexOf(keyWord.toLowerCase()) == -1) {
                continue;
            }
            Pattern keyPattern = Pattern.compile("((\\s{0,}))" + keyWord + "((\\s|\\n|\\r)+)", Pattern.CASE_INSENSITIVE);
            sql = keyPattern.matcher(sql).replaceAll("\r\n  " + keyWord + " ");
        }
        return sql;
    }


    public static void main(String[] args) {
        String sql = "select sum(total) as total,sum(success) as success" +
                " from  ecommerce_all_stat_access " +
                " where appId = #appId " +
                "        and dataType= #dataType" +
                "        and dataTime <= #sum(#alarmTime ,  #ss, #a(#d, #c)) " +
                "and a=#s(dd)" +
                "        and dataTime >= DATE_SUB(#alarmTime,INTERVAL #dataPointNum * #intervalTime MINUTE)  ";

//        System.out.println(sql);
//        System.out.println(SQLUtils.formatMySql(sql.replaceAll("#", "_")));

        // 提取sql 语句中表达式
        sql = new DataQueryHandler().formatSQL(sql);
        System.out.println("\n ========\n" + sql);

//        Pattern pattern = Pattern.compile("(#\\S{1,}\\(.+\\)\\s?)|(#\\S{1,}\\s?)");
//        List<String> sqlparts = pattern.splitAsStream(sql).collect(Collectors.toList());
//        List<String> expressions = Lists.newArrayList();
//        Matcher expressionMatcher = pattern.matcher(sql);
//        while (expressionMatcher.find()) {
//            expressions.add(expressionMatcher.group());
//        }
//
//        StringBuffer sqlBf = new StringBuffer();
//        int size = sqlparts.size();
//
//        Map<String, String> dynamicMap = Maps.newHashMap();
//        for (int i = 0; i < size; i++) {
//            String sqlpart = sqlparts.get(i);
//            sqlBf.append(sqlpart);
//            if (i < expressions.size()) {
//                String expression = expressions.get(i);
//                String dynamicKey = "data.p" + i;
//                dynamicMap.put(dynamicKey, expression);
//                sqlBf.append("${" + dynamicKey + "}");
//            }
//        }
//        System.out.println("sqlparts=" + JSON.toJSONString(sqlparts));
//        System.out.println("sql=" + sqlBf);
//        System.out.println("dynamic=" + JSON.toJSONString(dynamicMap));
    }
}
