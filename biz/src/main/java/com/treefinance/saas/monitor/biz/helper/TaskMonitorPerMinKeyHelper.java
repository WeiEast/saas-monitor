package com.treefinance.saas.monitor.biz.helper;

import com.google.common.base.Joiner;
import com.treefinance.saas.monitor.common.enumeration.EBizType;
import com.treefinance.saas.monitor.common.enumeration.EStatType;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by haojiahong on 2017/11/23.
 */
public class TaskMonitorPerMinKeyHelper {

    private static final String KEY_PREFIX = "saas-monitor-task-monitor";

    private static final String MERCHANT_INTERVAL_STAT = "merchant-interval-template";
    private static final String MERCHANT_DAY_STAT = "merchant-day-template";
    private static final String KEY_DAY_ON_MERCHANT_INTERVAL = "key-days-merchant-interval";
    private static final String KEY_USER_ON_MERCHANT_INTERVAL = "key-users-merchant-interval";
    private static final String KEY_USER_ON_MERCHANT_DAY = "key-users-merchant-day";

    private static final String SAAS_INTERVAL_STAT = "saas-interval-template";
    private static final String SAAS_DAY_STAT = "saas-day-template";
    private static final String KEY_DAY_ON_SAAS_INTERVAL = "key-days-saas-interval";
    private static final String KEY_USER_ON_SAAS_INTERVAL = "key-users-saas-interval";
    private static final String KEY_USER_ON_SAAS_DAY = "key-users-saas-day";

    private static final String MERCHANT_WITH_TYPE_INTERVAL_STAT = "merchant-type-interval-template";
    private static final String KEY_DAY_ON_MERCHANT_WITH_TYPE_INTERVAL = "key-days-merchant-type-interval";
    private static final String KEY_USER_ON_MERCHANT_WITH_TYPE_INTERVAL = "key-users-merchant-type-interval";

    private static final String SAAS_ERROR_STEP_DAY = "saas-error-step-day";


    private static final String KEY_MERCHANTS = "key-merchants";
    private static final String KEY_TASKS = "key-task-ids";
    private static final String KEY_ALARM_TIMES = "key-alarm-times";


    /**
     * redis中存入的时间段key,如设置60为:7:00,8:00,9:00
     * 若dataTime=2017-11-06 07:05:55 ,intervalMinutes = 60 则返回 2017-11-06 07:00:00
     *
     * @param dataTime
     * @return
     */
    public static Date getRedisStatDateTime(Date dataTime, Integer intervalMinutes) {
        Date intervalTime = DateUtils.truncate(dataTime, Calendar.MINUTE);
        Long currentMinute = DateUtils.getFragmentInMinutes(intervalTime, Calendar.HOUR_OF_DAY);
        if (currentMinute % intervalMinutes == 0) {
            return intervalTime;
        }
        intervalTime = DateUtils.addMinutes(intervalTime, (-currentMinute.intValue() % intervalMinutes));
        return intervalTime;
    }

    //商户每分钟统计==============

    /**
     * 商户每分钟统计key
     *
     * @param redisKeyTime
     * @param appId
     * @param statType
     * @return
     */
    public static String keyOfMerchantIntervalStat(Date redisKeyTime, String appId, EStatType statType) {
        String intervalDateStr = DateFormatUtils.format(redisKeyTime, "yyyy-MM-dd HH:mm:ss");
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, MERCHANT_INTERVAL_STAT, appId, statType, intervalDateStr);
    }

    /**
     * 商户每分钟统计,刷新db时需要统计的时间
     *
     * @param redisKeyTime
     * @param statType
     * @return
     */
    public static String keyOfDayOnMerchantIntervalStat(Date redisKeyTime, String appId, EStatType statType) {
        String day = DateFormatUtils.format(redisKeyTime, "yyyy-MM-dd");
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, KEY_DAY_ON_MERCHANT_INTERVAL, appId, statType, day);
    }


    /**
     * 商户每分钟统计,此段时间内记录不同uniqueId的key
     *
     * @param redisKeyTime
     * @param appId
     * @param statType     @return
     */
    public static String keyOfUsersOnMerchantIntervalStat(Date redisKeyTime, String appId, EStatType statType) {
        String intervalDateStr = DateFormatUtils.format(redisKeyTime, "yyyy-MM-dd HH:mm:ss");
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, KEY_USER_ON_MERCHANT_INTERVAL, appId, statType, intervalDateStr);
    }


    //商户每天统计=============

    public static String keyOfMerchantDayStat(Date redisKeyTime, String appId, EStatType statType) {
        String intervalDateStr = DateFormatUtils.format(redisKeyTime, "yyyy-MM-dd");
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, MERCHANT_DAY_STAT, appId, statType, intervalDateStr);
    }

    public static String keyOfUsersOnMerchantDayStat(Date redisKeyTime, String appId, EStatType statType) {
        String intervalDateStr = DateFormatUtils.format(redisKeyTime, "yyyy-MM-dd");
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, KEY_USER_ON_MERCHANT_DAY, appId, statType, intervalDateStr);
    }

    //所有商户每分钟统计================

    public static String keyOfSaasIntervalStat(Date redisKeyTime, EStatType statType) {
        String intervalDateStr = DateFormatUtils.format(redisKeyTime, "yyyy-MM-dd HH:mm:ss");
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, SAAS_INTERVAL_STAT, statType, intervalDateStr);
    }

    public static String keyOfDayOnSaasIntervalStat(Date redisKeyTime, EStatType statType) {
        String day = DateFormatUtils.format(redisKeyTime, "yyyy-MM-dd");
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, KEY_DAY_ON_SAAS_INTERVAL, statType, day);
    }

    public static String keyOfUsersOnSaasIntervalStat(Date redisKeyTime, EStatType statType) {
        String intervalDateStr = DateFormatUtils.format(redisKeyTime, "yyyy-MM-dd HH:mm:ss");
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, KEY_USER_ON_SAAS_INTERVAL, statType, intervalDateStr);
    }

    //所有商户每天统计================

    public static String keyOfSaasDayStat(Date redisKeyTime, EStatType statType) {
        String intervalDateStr = DateFormatUtils.format(redisKeyTime, "yyyy-MM-dd");
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, SAAS_DAY_STAT, statType, intervalDateStr);
    }

    public static String keyOfUsersOnSaasDayStat(Date redisKeyTime, EStatType statType) {
        String intervalDateStr = DateFormatUtils.format(redisKeyTime, "yyyy-MM-dd");
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, KEY_USER_ON_SAAS_DAY, statType, intervalDateStr);
    }

    //商户银行访问统计表,商户邮箱访问统计表,商户邮箱访问统计表,商户邮箱访问统计表,以及后续会添加其他业务类型=============

    public static String keyOfMerchantWithTypeIntervalStat(Date redisKeyTime, String appId, EStatType statType, String account) {
        String intervalDateStr = DateFormatUtils.format(redisKeyTime, "yyyy-MM-dd HH:mm:ss");
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, MERCHANT_WITH_TYPE_INTERVAL_STAT, statType, appId, account, intervalDateStr);
    }

    public static String keyOfDayOnMerchantWithTypeIntervalStat(Date redisKeyTime, String appId, String account, EStatType statType) {
        String day = DateFormatUtils.format(redisKeyTime, "yyyy-MM-dd");
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, KEY_DAY_ON_MERCHANT_WITH_TYPE_INTERVAL, appId, account, statType, day);
    }

    public static String keyOfUsersOnMerchantWithTypeIntervalStat(Date redisKeyTime, String appId, EStatType statType, String account) {
        String intervalDateStr = DateFormatUtils.format(redisKeyTime, "yyyy-MM-dd HH:mm:ss");
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, KEY_USER_ON_MERCHANT_WITH_TYPE_INTERVAL, statType, appId, account, intervalDateStr);
    }

    //任务失败环节统计=========

    public static String keyOfSaasErrorStepDay(Date redisKeyTime, EStatType statType, String stepCode) {
        String intervalDateStr = DateFormatUtils.format(redisKeyTime, "yyyy-MM-dd");
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, SAAS_ERROR_STEP_DAY, stepCode, statType, intervalDateStr);
    }


    //通用key==============================

    /**
     * 需要统计的商户
     *
     * @return
     */
    public static String keyOfAppIds() {
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, KEY_MERCHANTS);
    }


    /**
     * 记录一段时间内的任务id
     *
     * @param redisKeyTime
     * @return
     */
    public static String keyOfTaskLog(Date redisKeyTime) {
        String intervalDateStr = DateFormatUtils.format(redisKeyTime, "yyyy-MM-dd HH:mm:ss");
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, KEY_TASKS, intervalDateStr);
    }

    /**
     * 记录任务成功率预警,今天已已经预警的时刻
     *
     * @param redisKeyTime
     * @param bizType
     * @return
     */
    public static String keyOfAlarmTimeLog(Date redisKeyTime, EBizType bizType) {
        String intervalDateStr = DateFormatUtils.format(redisKeyTime, "yyyy-MM-dd");
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, KEY_ALARM_TIMES, intervalDateStr, bizType);
    }
}
