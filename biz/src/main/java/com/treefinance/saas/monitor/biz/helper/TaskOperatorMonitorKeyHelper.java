package com.treefinance.saas.monitor.biz.helper;

import com.google.common.base.Joiner;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by haojiahong on 2017/10/31.
 */
public class TaskOperatorMonitorKeyHelper {

    private static final String KEY_PREFIX = "saas-monitor-task-operator-monitor";
    private static final String KEY_DAY = "key-days";
    private static final String GROUP_STAT_ACCESS_INTERVAL = "group-stat-interval";
    private static final String GROUP_STAT_ACCESS_DAY = "group-stat-day";
    private static final String ALL_STAT_ACCESS_DAY = "all-stat-day";

    /**
     * 获取特定运营商统计key
     * saas-monitor-task-operator-monitor:group-stat-interval:groupCode:yyyy-MM-dd HH:mm:ss
     *
     * @param intervalTime
     * @param groupCode
     * @return
     */
    public static String keyOfGroupCodeIntervalStat(Date intervalTime, String groupCode) {
        String intervalTimeStr = DateFormatUtils.format(intervalTime, "yyyy-MM-dd HH:mm:ss");
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, GROUP_STAT_ACCESS_INTERVAL, groupCode, intervalTimeStr);
    }


    /**
     * 获取特定运营商日统计key
     * saas-monitor-task-operator-monitor:group-stat-day:groupCode:yyyy-MM-dd
     *
     * @param intervalTime
     * @param groupCode
     * @return
     */
    public static String keyOfGroupCodeDayStat(Date intervalTime, String groupCode) {
        String day = DateFormatUtils.format(intervalTime, "yyyy-MM-dd");
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, GROUP_STAT_ACCESS_DAY, groupCode, day);
    }

    /**
     * 获取所有运营商日统计key
     * saas-monitor-task-operator-monitor:all-stat-day:yyyy-MM-dd
     *
     * @param intervalTime
     * @return
     */
    public static String keyOfAllDayStat(Date intervalTime) {
        String day = DateFormatUtils.format(intervalTime, "yyyy-MM-dd");
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, ALL_STAT_ACCESS_DAY, day);
    }


    public static String keyOfDay(Date intervalTime) {
        String day = DateFormatUtils.format(intervalTime, "yyyy-MM-dd");
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, KEY_DAY, day);
    }


    /**
     * redis中存入的时间段key,如设置60位:7:00,8:00,9:00
     *
     * @param dataTime
     * @return
     */
    public static Date getRedisStatDateTime(Date dataTime) {
        Date intervalTime = StatHelper.calculateIntervalTime(dataTime, 60);//按小时统计数据的时间点,如6:00,7:00
        return intervalTime;

    }

    public static String keyOfGroupCodes() {
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, "stat-group-codes");
    }

}
