package com.treefinance.saas.monitor.biz.helper;

import com.google.common.base.Joiner;
import com.treefinance.saas.grapserver.facade.model.enums.ETaskOperatorMonitorStatus;
import com.treefinance.saas.monitor.common.enumeration.ETaskOperatorStatType;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by haojiahong on 2017/10/31.
 */
public class TaskOperatorMonitorKeyHelper {

    private static final String KEY_PREFIX = "saas-monitor-task-operator-monitor";
    private static final String KEY_MESSAGE_LOG = "key-message-log";
    private static final String KEY_DAY_ON_GROUP = "key-days-group";
    private static final String KEY_DAY_ON_All = "key-days-all";
    private static final String GROUP_STAT_ACCESS_INTERVAL = "group-stat-interval";
    private static final String GROUP_STAT_ACCESS_DAY = "group-stat-day";
    private static final String ALL_STAT_ACCESS_INTERVAL = "all-stat-interval";
    private static final String ALL_STAT_ACCESS_DAY = "all-stat-day";

    private static final String KEY_USERS_GROUP_ON_ACTION = "key-users-group-on-action";
    private static final String KEY_USERS_ALL_ON_ACTION = "key-users-all-on-action";


    /**
     * 获取特定运营商统计key
     * saas-monitor-task-operator-monitor:group-stat-interval:statType:groupCode:yyyy-MM-dd HH:mm:ss
     *
     * @param intervalTime
     * @param groupCode
     * @param statType
     * @return
     */
    public static String keyOfGroupCodeIntervalStat(Date intervalTime, String groupCode, ETaskOperatorStatType statType) {
        String intervalTimeStr = DateFormatUtils.format(intervalTime, "yyyy-MM-dd HH:mm:ss");
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, GROUP_STAT_ACCESS_INTERVAL, statType, groupCode, intervalTimeStr);
    }


    /**
     * 获取特定运营商日统计key
     * saas-monitor-task-operator-monitor:group-stat-day:statType:groupCode:yyyy-MM-dd
     *
     * @param intervalTime
     * @param groupCode
     * @param statType
     * @return
     */
    public static String keyOfGroupCodeDayStat(Date intervalTime, String groupCode, ETaskOperatorStatType statType) {
        String day = DateFormatUtils.format(intervalTime, "yyyy-MM-dd");
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, GROUP_STAT_ACCESS_DAY, statType, groupCode, day);
    }

    /**
     * 获取所有运营商按按时间间隔统计key
     * saas-monitor-task-operator-monitor:all-stat-interval:yyyy-MM-dd HH:mm:ss
     *
     * @param intervalTime
     * @return
     */
    public static String keyOfAllIntervalStat(Date intervalTime, ETaskOperatorStatType statType) {
        String intervalTimeStr = DateFormatUtils.format(intervalTime, "yyyy-MM-dd HH:mm:ss");
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, ALL_STAT_ACCESS_INTERVAL, statType, intervalTimeStr);
    }

    /**
     * 获取所有运营商日统计key
     * saas-monitor-task-operator-monitor:all-stat-day:yyyy-MM-dd
     *
     * @param intervalTime
     * @return
     */
    public static String keyOfAllDayStat(Date intervalTime, ETaskOperatorStatType statType) {
        String day = DateFormatUtils.format(intervalTime, "yyyy-MM-dd");
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, ALL_STAT_ACCESS_DAY, statType, day);
    }

    /**
     * 记录按时间间隔统计特定运营商,定时任务刷新时,需要统计的时间段集合
     *
     * @param intervalTime
     * @return
     */
    public static String keyOfDayOnGroupStat(Date intervalTime, ETaskOperatorStatType statType) {
        String day = DateFormatUtils.format(intervalTime, "yyyy-MM-dd");
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, KEY_DAY_ON_GROUP, statType, day);
    }

    /**
     * 记录按时间间隔统计所有运营商,定时任务刷新时,需要统计的时间段集合
     *
     * @param intervalTime
     * @return
     */
    public static String keyOfDayOnAllStat(Date intervalTime, ETaskOperatorStatType statType) {
        String day = DateFormatUtils.format(intervalTime, "yyyy-MM-dd");
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, KEY_DAY_ON_All, statType, day);
    }

    /**
     * 记录按日统计,当天已统计的用户(区分运营商)
     *
     * @param intervalTime
     * @return
     */
    public static String keyOfUsersGroupOnDayStat(Date intervalTime, ETaskOperatorMonitorStatus statusType) {
        String day = DateFormatUtils.format(intervalTime, "yyyy-MM-dd");
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, KEY_USERS_GROUP_ON_ACTION, day, statusType);
    }

    /**
     * 记录按时间间隔统计,当前时间段已统计的用户(区分运营商)
     *
     * @param intervalTime
     * @param statusType
     * @return
     */
    public static String keyOfUsersGroupOnIntervalStat(Date intervalTime, ETaskOperatorMonitorStatus statusType) {
        String interval = DateFormatUtils.format(intervalTime, "yyyy-MM-dd HH:mm:ss");
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, KEY_USERS_GROUP_ON_ACTION, interval, statusType);
    }


    /**
     * 记录按日统计,当天已统计的用户(区分运营商)
     *
     * @param intervalTime
     * @return
     */
    public static String keyOfUsersAllOnDayStat(Date intervalTime, ETaskOperatorMonitorStatus statusType) {
        String day = DateFormatUtils.format(intervalTime, "yyyy-MM-dd");
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, KEY_USERS_ALL_ON_ACTION, day, statusType);
    }

    /**
     * 记录按时间间隔统计,当前时间段已统计的用户(区分运营商)
     *
     * @param intervalTime
     * @param statusType
     * @return
     */
    public static String keyOfUsersAllOnIntervalStat(Date intervalTime, ETaskOperatorMonitorStatus statusType) {
        String interval = DateFormatUtils.format(intervalTime, "yyyy-MM-dd HH:mm:ss");
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, KEY_USERS_ALL_ON_ACTION, interval, statusType);
    }

    public static String keyOfMessageLog(Date intervalTime) {
        String interval = DateFormatUtils.format(intervalTime, "yyyy-MM-dd HH:mm:ss");
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, KEY_MESSAGE_LOG, interval);
    }


    /**
     * redis中存入的时间段key,如设置60为:7:00,8:00,9:00
     * 若dataTime=2017-11-06 07:05:55 ,intervalMinutes = 60 则返回 2017-11-06 07:00:00
     *
     * @param dataTime
     * @return
     */
    public static Date getRedisStatDateTime(Date dataTime, Integer intervalMinutes) {
        if (intervalMinutes == null) {
            intervalMinutes = 60;//默认定时统计时间段为60分钟
        }
        Date intervalTime = DateUtils.truncate(dataTime, Calendar.MINUTE);
        Long currentMinute = DateUtils.getFragmentInMinutes(intervalTime, Calendar.HOUR_OF_DAY);
        if (currentMinute % intervalMinutes == 0) {
            return intervalTime;
        }
        intervalTime = DateUtils.addMinutes(intervalTime, (-currentMinute.intValue() % intervalMinutes));
        return intervalTime;
    }

    public static String keyOfGroupCodes() {
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, "stat-group-codes");
    }

}
