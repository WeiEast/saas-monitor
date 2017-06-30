package com.treefinance.saas.monitor.biz.helper;

import com.google.common.base.Joiner;
import com.treefinance.saas.monitor.common.enumeration.EStatType;
import org.apache.commons.lang.time.DateFormatUtils;

import java.util.Date;

/**
 * Created by yh-treefinance on 2017/6/7.
 */
public class RedisKeyHelper {

    public static final String KEY_PREFIX = "saas-monitor";
    public static final String STAT_ACCESS = "stat-access";
    public static final String STAT_ACCESS_DAY = "stat-access-day";
    public static final String STAT_MAIL = "stat-mail";
    public static final String STAT_ECOMMERCE = "stat-ecommerce";
    public static final String STAT_OPERATOR = "stat-operator";
    public static final String ALARM_ACCESS_DAY = "alarm-flag";


    /**
     * appId列表
     */
    public static String keyOfAppIds() {
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, "stat-appids").toString();
    }

    /**
     * 日期时间
     *
     * @param date
     * @return
     */
    public static String keyOfDay(Date date) {
        String day = DateFormatUtils.format(date, "yyyy-MM-dd");
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, "stat-day", day).toString();
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
     * 获取统计key
     *
     * @param intervalTime
     * @return
     */
    public static String keyOfTotal(String appId, Date intervalTime, EStatType statType) {
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, STAT_ACCESS, appId, statType, intervalTime.getTime()).toString();
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
}
