package com.treefinance.saas.monitor.biz.helper;

import com.google.common.base.Joiner;
import com.treefinance.saas.grapserver.facade.model.enums.ETaskOperatorMonitorStatus;
import com.treefinance.saas.monitor.common.domain.dto.alarmconfig.OperatorMonitorAlarmConfigDTO;
import com.treefinance.saas.monitor.common.enumeration.ETaskStatDataType;
import com.treefinance.saas.monitor.common.utils.MonitorDateUtils;
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
    private static final String KEY_DAY_USER_MOBILE_LOG = "key-day-user-mobile-log";
    private static final String KEY_INTERVAL_USER_MOBILE_LOG = "key-interval-user-mobile-log";
    private static final String KEY_DAY_ON_GROUP = "key-days-group";
    private static final String KEY_DAY_ON_All = "key-days-all";
    private static final String GROUP_STAT_ACCESS_INTERVAL = "group-template-interval";
    private static final String GROUP_STAT_ACCESS_DAY = "group-template-day";
    private static final String ALL_STAT_ACCESS_INTERVAL = "all-template-interval";
    private static final String ALL_STAT_ACCESS_DAY = "all-template-day";

    private static final String KEY_USERS_GROUP_ON_ACTION = "key-users-group-on-action";
    private static final String KEY_USERS_ALL_ON_ACTION = "key-users-all-on-action";
    private static final String KEY_ALARM_TIMES = "key-str-alarm-times";
    private static final String KEY_ALARM_MSG_TIMES = "key-str-alarm-msg-times";

    private static final String KEY_ALL_INTERVAL_TASK_USER_COUNT = "key-all-interval-task-user-count";
    private static final String KEY_ALL_DAY_TASK_USER_COUNT = "key-all-day-task-user-count";
    private static final String KEY_ALL_INTERVAL_TASK_USER_COUNT_UNIQUE = "key-all-interval-task-user-count-unique";
    private static final String KEY_ALL_INTERVAL_TASK_USER_COUNT_UNIQUE_MOBILE = "key-all-interval-task-user-count-unique-mobile";
    private static final String KEY_ALL_DAY_TASK_USER_COUNT_UNIQUE = "key-all-day-task-user-count-unique";
    private static final String KEY_ALL_DAY_TASK_USER_COUNT_UNIQUE_MOBILE = "key-all-day-task-user-count-unique-mobile";

    /**
     * 获取特定运营商统计key
     * saas-monitor-task-operator-monitor:group-template-interval:statType:groupCode:yyyy-MM-dd HH:mm:ss
     *
     * @param intervalTime
     * @param groupCode
     * @param statType
     * @return
     */
    public static String keyOfGroupCodeIntervalStat(Date intervalTime, String groupCode, String appId, ETaskStatDataType statType) {
        String intervalTimeStr = DateFormatUtils.format(intervalTime, "yyyy-MM-dd HH:mm:ss");
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, GROUP_STAT_ACCESS_INTERVAL, statType, groupCode, appId, intervalTimeStr);
    }


    /**
     * 获取特定运营商日统计key
     * saas-monitor-task-operator-monitor:group-template-day:statType:groupCode:yyyy-MM-dd
     *
     * @param intervalTime
     * @param groupCode
     * @param statType
     * @return
     */
    public static String keyOfGroupCodeDayStat(Date intervalTime, String groupCode, String appId, ETaskStatDataType statType) {
        String day = DateFormatUtils.format(intervalTime, "yyyy-MM-dd");
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, GROUP_STAT_ACCESS_DAY, statType, groupCode, appId, day);
    }

    /**
     * 获取所有运营商按按时间间隔统计key
     * saas-monitor-task-operator-monitor:all-template-interval:yyyy-MM-dd HH:mm:ss
     *
     * @param intervalTime
     * @return
     */
    public static String keyOfAllIntervalStat(Date intervalTime, String appId, ETaskStatDataType statType) {
        String intervalTimeStr = DateFormatUtils.format(intervalTime, "yyyy-MM-dd HH:mm:ss");
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, ALL_STAT_ACCESS_INTERVAL, statType, appId, intervalTimeStr);
    }

    /**
     * 获取所有运营商日统计key
     * saas-monitor-task-operator-monitor:all-template-day:yyyy-MM-dd
     *
     * @param intervalTime
     * @return
     */
    public static String keyOfAllDayStat(Date intervalTime, String appId, ETaskStatDataType statType) {
        String day = DateFormatUtils.format(intervalTime, "yyyy-MM-dd");
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, ALL_STAT_ACCESS_DAY, statType, appId, day);
    }

    /**
     * 记录按时间间隔统计特定运营商,定时任务刷新时,需要统计的时间段集合
     *
     * @param intervalTime
     * @return
     */
    public static String keyOfDayOnGroupStat(Date intervalTime, String appId, ETaskStatDataType statType) {
        String day = DateFormatUtils.format(intervalTime, "yyyy-MM-dd");
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, KEY_DAY_ON_GROUP, statType, day, appId);
    }

    /**
     * 记录按时间间隔统计所有运营商,定时任务刷新时,需要统计的时间段集合
     *
     * @param intervalTime
     * @return
     */
    public static String keyOfDayOnAllStat(Date intervalTime, String appId, ETaskStatDataType statType) {
        String day = DateFormatUtils.format(intervalTime, "yyyy-MM-dd");
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, KEY_DAY_ON_All, statType, day, appId);
    }

    /**
     * 记录按日统计,当天已统计的用户(区分运营商)
     *
     * @param intervalTime
     * @return
     */
    public static String keyOfUsersGroupOnDayStat(Date intervalTime, String groupCode, ETaskOperatorMonitorStatus statusType, String appId) {
        String day = DateFormatUtils.format(intervalTime, "yyyy-MM-dd");
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, KEY_USERS_GROUP_ON_ACTION, day, groupCode, appId, statusType);
    }

    /**
     * 记录按时间间隔统计,当前时间段已统计的用户(区分运营商)
     *
     * @param intervalTime
     * @param statusType
     * @return
     */
    public static String keyOfUsersGroupOnIntervalStat(Date intervalTime, String groupCode, ETaskOperatorMonitorStatus statusType, String appId) {
        String interval = DateFormatUtils.format(intervalTime, "yyyy-MM-dd HH:mm:ss");
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, KEY_USERS_GROUP_ON_ACTION, interval, groupCode, appId, statusType);
    }


    /**
     * 记录按日统计,当天已统计的用户(区分运营商)
     *
     * @param intervalTime
     * @return
     */
    public static String keyOfUsersAllOnDayStat(Date intervalTime, ETaskOperatorMonitorStatus statusType, String appId) {
        String day = DateFormatUtils.format(intervalTime, "yyyy-MM-dd");
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, KEY_USERS_ALL_ON_ACTION, day, statusType, appId);
    }

    /**
     * 记录按时间间隔统计,当前时间段已统计的用户(区分运营商)
     *
     * @param intervalTime
     * @param statusType
     * @param appId
     * @return
     */
    public static String keyOfUsersAllOnIntervalStat(Date intervalTime, ETaskOperatorMonitorStatus statusType, String appId) {
        String interval = DateFormatUtils.format(intervalTime, "yyyy-MM-dd HH:mm:ss");
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, KEY_USERS_ALL_ON_ACTION, interval, appId, statusType);
    }

    /**
     * 任务消息记录
     *
     * @param intervalTime
     * @return
     */
    public static String keyOfMessageLog(Date intervalTime) {
        String interval = DateFormatUtils.format(intervalTime, "yyyy-MM-dd HH:mm:ss");
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, KEY_MESSAGE_LOG, interval);
    }

    /**
     * 用户一天内对应的手机号记录
     *
     * @param intervalTime
     * @return
     */
    public static String keyOfDayUsersMobileLog(Date intervalTime, String appId) {
        String interval = DateFormatUtils.format(intervalTime, "yyyy-MM-dd");
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, KEY_DAY_USER_MOBILE_LOG, interval, appId);
    }

    /**
     * 用户统计时段内对应的手机号记录
     *
     * @param intervalTime
     * @return
     */
    public static String keyOfIntervalUsersMobileLog(Date intervalTime, String appId) {
        String interval = DateFormatUtils.format(intervalTime, "yyyy-MM-dd HH:mm:ss");
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, KEY_INTERVAL_USER_MOBILE_LOG, interval, appId);
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
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, "template-group-codes");
    }

    public static String keyOfAppIds() {
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, "template-appIds");
    }

    public static String keyOfAlarmTimeLog(Date baseTime, OperatorMonitorAlarmConfigDTO config) {
        String intervalDateStr = DateFormatUtils.format(baseTime, "yyyy-MM-dd");
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, KEY_ALARM_TIMES, intervalDateStr, config.getAlarmType(), config.getDataType(), config.getSaasEnv());
    }

    public static String strKeyOfAlarmTimeLog(Date baseTime, OperatorMonitorAlarmConfigDTO config) {
        String intervalDateStr = DateFormatUtils.format(baseTime, "yyyy-MM-dd");
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, KEY_ALARM_TIMES, intervalDateStr, config.getAlarmType(), config.getDataType(), config.getSaasEnv(), MonitorDateUtils.format2Hms(baseTime));
    }

    public static String keyOfAlarmMsgTimeLog(Date baseTime, OperatorMonitorAlarmConfigDTO config) {
        String interval = DateFormatUtils.format(baseTime, "yyyy-MM-dd HH:mm:ss");
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, KEY_ALARM_MSG_TIMES, interval, config.getAlarmType(), config.getDataType(), config.getSaasEnv());
    }

    public static String keyOfTaskUserCountAllIntervalStat(Date intervalTime, String appId, ETaskStatDataType statType) {
        String interval = DateFormatUtils.format(intervalTime, "yyyy-MM-dd HH:mm:ss");
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, KEY_ALL_INTERVAL_TASK_USER_COUNT, interval, appId, statType);
    }

    public static String keyOfTaskUserCountAllDayStat(Date intervalTime, String appId, ETaskStatDataType statType) {
        String interval = DateFormatUtils.format(intervalTime, "yyyy-MM-dd");
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, KEY_ALL_DAY_TASK_USER_COUNT, interval, appId, statType);
    }

    public static String keyOfUsersCountAllIntervalStat(Date intervalTime, String appId, ETaskStatDataType statType) {
        String interval = DateFormatUtils.format(intervalTime, "yyyy-MM-dd HH:mm:ss");
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, KEY_ALL_INTERVAL_TASK_USER_COUNT_UNIQUE, interval, appId, statType);
    }

    public static String keyOfUserCountAllIntervalUsersMobileLog(Date intervalTime, String appId, ETaskStatDataType statType) {
        String interval = DateFormatUtils.format(intervalTime, "yyyy-MM-dd HH:mm:ss");
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, KEY_ALL_INTERVAL_TASK_USER_COUNT_UNIQUE_MOBILE, interval, appId, statType);
    }

    public static String keyOfUsersCountAllDayStat(Date intervalTime, String appId, ETaskStatDataType statType) {
        String interval = DateFormatUtils.format(intervalTime, "yyyy-MM-dd");
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, KEY_ALL_DAY_TASK_USER_COUNT_UNIQUE, interval, appId, statType);
    }

    public static String keyOfUserCountAllDayUsersMobileLog(Date intervalTime, String appId, ETaskStatDataType statType) {
        String interval = DateFormatUtils.format(intervalTime, "yyyy-MM-dd");
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, KEY_ALL_DAY_TASK_USER_COUNT_UNIQUE_MOBILE, interval, appId, statType);
    }

}
