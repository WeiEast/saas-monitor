package com.treefinance.saas.monitor.biz.helper;

import com.google.common.base.Joiner;
import com.treefinance.saas.monitor.common.enumeration.ETaskOperatorStatus;
import org.apache.commons.lang.time.DateFormatUtils;

import java.util.Date;

/**
 * Created by haojiahong on 2017/10/31.
 */
public class TaskOperatorMonitorKeyHelper {

    private static final String KEY_PREFIX = "saas-monitor";
    private static final String KEY_DAY = "task-operator-monitor-key-days";
    private static final String STAT_ACCESS_INTERVAL = "task-operator-monitor-stat-interval";
    private static final String STAT_ACCESS_DAY = "task-operator-monitor-stat-day";

    /**
     * 获取统计key
     *
     * @param intervalTime
     * @return
     */
    public static String keyOfIntervalStat(Date intervalTime, ETaskOperatorStatus status) {
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, STAT_ACCESS_INTERVAL, status, intervalTime.getTime());
    }


    /**
     * 获取日统计key
     *
     * @param intervalTime
     * @return
     */
    public static String keyOfDayStat(Date intervalTime, ETaskOperatorStatus status) {
        String day = DateFormatUtils.format(intervalTime, "yyyy-MM-dd");
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, STAT_ACCESS_DAY, status, day);
    }


    public static String keyOfDay(Date intervalTime) {
        String day = DateFormatUtils.format(intervalTime, "yyyy-MM-dd");
        return Joiner.on(":").useForNull("null").join(KEY_PREFIX, KEY_DAY, day);
    }

    public static String getDateTimeStr(Date date) {
        return DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss");
    }
}
