package com.treefinance.saas.monitor.biz.helper;

import com.google.common.base.Joiner;
import com.treefinance.saas.monitor.common.enumeration.EBizType;
import com.treefinance.saas.monitor.common.enumeration.EStatType;
import org.apache.commons.lang.time.DateFormatUtils;

import java.util.Date;

/**
 * Created by yh-treefinance on 2017/6/7.
 */
public class RedisKeyHelper {

    public static final String KEY_PREFIX = "saas-monitor";
    public static final String STAT_ACCESS = "template-access";
    public static final String STAT_ACCESS_ALL = "template-access-all";
    public static final String STAT_ACCESS_DAY = "template-access-day";
    public static final String STAT_ACCESS_DAY_ALL = "template-access-day-all";
    public static final String STAT_ACCESS_DAY_ERROR = "template-access-day-error";
    public static final String STAT_MAIL = "template-mail";
    public static final String STAT_ECOMMERCE = "template-ecommerce";
    public static final String STAT_OPERATOR = "template-operator";
    public static final String ALARM_ACCESS_DAY = "alarm-flag";
    public static final String ALARM_ACCESS_DAY_ALL = "alarm-flag-all";
    public static final String ALARM_ACCESS_DAY_TIMES_ALL = "alarm-flag-times-all";
    public static final String HTTP_STAT = "template-http";
    public static final String TASK_EXIST = "saas-monitor-task-exist-monitor";

    /**
     * 任务存在数
     *
     * @param date
     * @return
     */
    public static String keyOfTaskExist(Date date) {
        String timeStr = DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss");
        return Joiner.on(":").useForNull("null").join(TASK_EXIST, timeStr);
    }

    /**
     * 任务存在数,区分业务类型
     *
     * @param date
     * @return
     */
    public static String keyOfTaskExistWithType(Date date, EBizType bizType) {
        String timeStr = DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss");
        return Joiner.on(":").useForNull("null").join(TASK_EXIST, bizType, timeStr);
    }

    /**
     * 任务存在数,区分环境
     *
     * @param date
     * @return
     */
    public static String keyOfTaskExistWithEnv(Date date, String saasEnv) {
        String timeStr = DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss");
        return Joiner.on(":").useForNull("null").join(TASK_EXIST, saasEnv, timeStr);
    }

    /**
     * http 请求总
     *
     * @param date
     * @return
     */
    public static String keyOfHttpTotal(Date date) {
        String day = DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss");
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, HTTP_STAT, "total", day).toString();
    }

    /**
     * http 请求商户
     *
     * @param date
     * @param appId
     * @return
     */
    public static String keyOfHttpMerchant(Date date, String appId) {
        String day = DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss");
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, HTTP_STAT, "merchant", appId, day).toString();
    }

    /**
     * http 请求api
     *
     * @param date
     * @return
     */
    public static String keyOfHttpApi(Date date, String api) {
        String day = DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss");
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, HTTP_STAT, "api", api, day).toString();
    }


    public static String keyOfHttpMerchantList(Date date) {
        String day = DateFormatUtils.format(date, "yyyy-MM-dd");
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, HTTP_STAT, "merchant-list", day).toString();
    }


    public static String keyOfHttpApiList(Date date) {
        String day = DateFormatUtils.format(date, "yyyy-MM-dd");
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, HTTP_STAT, "api-list", day).toString();
    }

    /**
     * http 请求日
     *
     * @param date
     * @return
     */
    public static String keyOfHttpDay(Date date) {
        String day = DateFormatUtils.format(date, "yyyy-MM-dd");
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, HTTP_STAT, "day", day).toString();
    }

    /**
     * appId列表
     */
    public static String keyOfAppIds() {
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, "template-appids").toString();
    }

    /**
     * 日期时间
     *
     * @param date
     * @return
     */
    public static String keyOfDay(Date date) {
        String day = DateFormatUtils.format(date, "yyyy-MM-dd");
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, "template-day", day).toString();
    }


    /**
     * 获取日统计key
     *
     * @param intervalTime
     * @return
     */
    public static String keyOfTotalDay(String appId, Date intervalTime, EStatType statType) {
        String day = DateFormatUtils.format(intervalTime, "yyyy-MM-dd");
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, STAT_ACCESS_DAY, appId, statType, day).toString();
    }

    /**
     * 获取日统计key(合计所有的商户)
     *
     * @param intervalTime
     * @return
     */
    public static String keyOfAllTotalDay(Date intervalTime, EStatType statType) {
        String day = DateFormatUtils.format(intervalTime, "yyyy-MM-dd");
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, STAT_ACCESS_DAY_ALL, statType, day).toString();
    }

    /**
     * 获取统计key
     *
     * @param intervalTime
     * @return
     */
    public static String keyOfTotal(String appId, Date intervalTime, EStatType statType) {
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, STAT_ACCESS, appId, statType, intervalTime.getTime()).toString();
    }


    /**
     * 获取统计key(合计所有的商户)
     *
     * @param intervalTime
     * @return
     */
    public static String keyOfAllTotal(Date intervalTime, EStatType statType) {
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, STAT_ACCESS_ALL, statType, intervalTime.getTime()).toString();
    }


    /**
     * 预警标记key
     *
     * @return
     */
    public static String keyOfAlarm(String appId, EStatType statType) {
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, ALARM_ACCESS_DAY, appId, statType).toString();
    }

    /**
     * 预警标记key(针对所有商户)
     *
     * @return
     */
    public static String keyOfAllAlarm(EStatType statType) {
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, ALARM_ACCESS_DAY_ALL, statType).toString();
    }

    /**
     * 预警标记key所标记的时间区段(针对所有商户)
     *
     * @param statType
     * @return
     */
    public static String keyOfAllAlarmTimes(EStatType statType) {
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, ALARM_ACCESS_DAY_TIMES_ALL, statType).toString();
    }

    /**
     * 获取邮箱统计key
     *
     * @param intervalTime
     * @return
     */
    public static String keyOfMail(String appId, Date intervalTime, String mailCode) {
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, STAT_MAIL, appId, mailCode, intervalTime.getTime()).toString();
    }

    /**
     * 获取银行统计key
     *
     * @param appId
     * @param intervalTime
     * @param bankId
     * @return
     */
    public static String keyOfBank(String appId, Date intervalTime, Byte bankId) {
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, STAT_MAIL, appId, bankId, intervalTime.getTime()).toString();
    }

    /**
     * 获取电商统计key
     *
     * @param appId
     * @param intervalTime
     * @param ecommerceId
     * @return
     */
    public static String keyOfEcommerce(String appId, Date intervalTime, Short ecommerceId) {
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, STAT_ECOMMERCE, appId, ecommerceId, intervalTime.getTime()).toString();
    }

    /**
     * 运营商
     *
     * @param appId
     * @param intervalTime
     * @param operatorId
     * @return
     */
    public static String keyOfOperator(String appId, Date intervalTime, String operatorId) {
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, STAT_OPERATOR, appId, operatorId, intervalTime.getTime()).toString();
    }

    /**
     * 用户ID
     *
     * @param key
     * @param uniqueId
     * @return
     */
    public static String keyOfUniqueId(String key, String uniqueId) {
        return Joiner.on(":").useForNull("null").join(key, uniqueId).toString();
    }


    /**
     * 任务失败取消环节日统计key(针对所有商户)
     *
     * @param intervalTime
     * @param statType
     * @return
     */
    public static String keyOfAllErrorDay(Date intervalTime, EStatType statType, String errorCode) {
        String day = DateFormatUtils.format(intervalTime, "yyyy-MM-dd");
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, STAT_ACCESS_DAY_ERROR, statType, day, errorCode).toString();
    }
}
