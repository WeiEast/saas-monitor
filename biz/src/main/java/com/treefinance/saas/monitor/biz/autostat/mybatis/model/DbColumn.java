package com.treefinance.saas.monitor.biz.autostat.mybatis.model;

/**
 * Created by yh-treefinance on 2018/1/25.
 */
public class DbColumn {
    /**
     * 列名
     */
    private String actualColumnName;
    /**
     * jdbc 类型
     */
    private int jdbcType;
    /**
     * jdbc 类型
     */
    private String jdbcTypeName;
    /**
     * 是否为空
     */
    private boolean nullable;
    /**
     * 长度
     */
    private int length;
    /**
     * decimal 精度
     */
    private int scale;
    /**
     * 默认值
     */
    private String remarks;
    /**
     * 默认值
     */
    private String defaultValue;

    /**
     * 自增列
     */
    private boolean isAutoIncrement;
    /**
     * 是否主鍵
     */
    private boolean isPrimaryKey;

    public String getActualColumnName() {
        return actualColumnName;
    }

    public void setActualColumnName(String actualColumnName) {
        this.actualColumnName = actualColumnName;
    }

    public int getJdbcType() {
        return jdbcType;
    }

    public void setJdbcType(int jdbcType) {
        this.jdbcType = jdbcType;
    }

    public String getJdbcTypeName() {
        return jdbcTypeName;
    }

    public void setJdbcTypeName(String jdbcTypeName) {
        this.jdbcTypeName = jdbcTypeName;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean isAutoIncrement() {
        return isAutoIncrement;
    }

    public void setAutoIncrement(boolean autoIncrement) {
        isAutoIncrement = autoIncrement;
    }

    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        isPrimaryKey = primaryKey;
    }
}
