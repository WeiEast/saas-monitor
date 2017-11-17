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
     *
     * @param date
     * @return
     */
    public static Date calculateDayTime(Date date) {
        return DateUtils.truncate(date, Calendar.DAY_OF_MONTH);
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
            intervalMinutes = 5;
        }
        Date intervalTime = DateUtils.truncate(dataTime, Calendar.MINUTE);
        Long currentMinute = DateUtils.getFragmentInMinutes(intervalTime, Calendar.HOUR_OF_DAY);
        if (currentMinute % intervalMinutes == 0) {
            return intervalTime;
        }
        intervalTime = DateUtils.addMinutes(intervalTime, (-currentMinute.intValue() % intervalMinutes));
        return intervalTime;
    }



    public static void main(String[] args) {
        System.out.println(StatHelper.calculateIntervalTime(new Date(), 60));
    }
}
