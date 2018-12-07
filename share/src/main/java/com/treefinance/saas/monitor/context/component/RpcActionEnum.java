package com.treefinance.saas.monitor.context.component;

/**
 * RPC action list.
 *
 * @author Jerry
 * @date 2018/11/29 17:02
 */
public enum RpcActionEnum {
    /**
     * 查询全部app licenses
     */
    QUERY_APP_LICENSES,
    /**
     * 根据bizType查询app licenses
     */
    QUERY_APP_LICENSES_BY_BIZ_TYPE,
    /**
     * 根据appId查询可用app licenses
     */
    QUERY_VALID_APP_LICENSES_BY_APP_ID,
    /**
     * 查询全部biz-type
     */
    QUERY_BIZ_TYPE_LIST,
    /**
     * 查询全部biz-type，发挥值包含主键ID
     */
    QUERY_BIZ_TYPE_LIST_SIMPLE,
    /**
     * 根据指定的biz_type值查询列表
     */
    QUERY_BIZ_TYPE_LIST_ASSIGNED,
    /**
     * 根据指定的taskId列表查询任务日志
     */
    QUERY_TASK_LOG_ASSIGNED_TASK_IDS,

    /**
     * 查询商户每天的访问统计记录的集合
     */
    STATISTICS_MERCHANT_DAILY_ACCESS_RESULT_SET,
    /**
     * 查询商户每天的访问统计记录
     */
    STATISTICS_MERCHANT_DAILY_ACCESS_RECORDS,
    /**
     * 查询商户的访问统计记录
     */
    STATISTICS_MERCHANT_ACCESS_RECORDS,
    /**
     * 查询商户的成功访问统计记录
     */
    STATISTICS_MERCHANT_ACCESS_SUCCESS_RECORDS,
    /**
     * 查询saas每天的ErrorStep统计记录
     */
    STATISTICS_ERROR_STEP_DAILY_RECORDS,

}
