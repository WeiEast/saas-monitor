package com.treefinance.saas.monitor.biz.helper;

import org.apache.commons.lang.time.DateUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by yh-treefinance on 2017/6/9.
 */
public abstract class StatHelper {

    /**
     * 计算最近间隔时间
     *
     * @param date
     * @param intervalMinutes
     * @return
     */
    public static Date calculateIntervalTime(Date date, int intervalMinutes) {
        Date intervalTime = DateUtils.truncate(date, Calendar.MINUTE);
        // 12:00:32 -> 12:01:00
        if (date.after(intervalTime)) {
            intervalTime = DateUtils.addMinutes(intervalTime, 1);
        }
        Long currentMinute = DateUtils.getFragmentInMinutes(intervalTime, Calendar.HOUR_OF_DAY);
        if (currentMinute % intervalMinutes == 0) {
            return intervalTime;
        }
        // 切换至下一时间段
        intervalTime = DateUtils.addMinutes(intervalTime, (intervalMinutes - currentMinute.intValue() % intervalMinutes));
        return intervalTime;
    }

    /**
     * 计算日期
     * @param date
     * @return
     */
    public static Date calculateDayTime(Date date) {
        return DateUtils.truncate(date, Calendar.DAY_OF_MONTH);
    }
}
