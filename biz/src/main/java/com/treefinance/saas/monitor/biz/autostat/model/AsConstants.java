package com.treefinance.saas.monitor.biz.autostat.model;

/**
 * Created by yh-treefinance on 2018/2/5.
 */
public class AsConstants {
    /**
     * redis key前缀
     */
    public static final String REDIS_PREFIX = "saas-monitor:stat";

    /**
     *
     */
    public static final String REDIS_LOCK_PREFIX = "saas-monitor:stat:lock";

    /**
     * 模板缓存key
     */
    public static final String REDIS_AUTO_STAT_TEMPLATE_KEY = "saas-monitor:auto:stat:templates";

    /**
     * redis
     */
    public static final String REDIS = "redis";

    /**
     * 数据时间
     */
    public static final String DATA = "data";
    /**
     * 数据时间
     */
    public static final String DATA_TIME = "dataTime";
    /**
     * 表达式
     */
    public static final String EXPRESSION = "expression";
    /**
     * 表达式ID
     */
    public static final String EXPRESSION_ID = "expressionId";
    /**
     * 计算模板
     */
    public static final String STAT_TEMPLATE = "statTemplate";
    /**
     * group Index
     */
    public static final String GROUP = "group";

    /**
     * 设置effectiveTime后人数统计group过滤
     */
    public static final String DISTINCT_USER_GROUP = "distinctUserGroup";

}
