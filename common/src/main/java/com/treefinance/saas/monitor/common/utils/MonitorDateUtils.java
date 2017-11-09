package com.treefinance.saas.monitor.common.utils;

import com.google.common.collect.Lists;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by haojiahong on 2017/11/3.
 */
public class MonitorDateUtils {

    public static String format(Date date) {
        return DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss");
    }

    public static String format2Ymd(Date date) {
        return DateFormatUtils.format(date, "yyyy-MM-dd");
    }

    public static Date parse(String dateStr) {
        Date date = null;
        try {
            date = DateUtils.parseDate(dateStr, "yyyy-MM-dd HH:mm:ss");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 获取当前日期开始时间
     *
     * @param date
     * @return
     */
    public static Date getDayStartTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取当期日期结束时间
     *
     * @param date
     * @return
     */
    public static Date getDayEndTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取当前日期整点时间
     *
     * @param date
     * @return
     */
    public static Date getOClockTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取当前日期每隔intervalMinutes时间的整点时间
     * 例如:2017-11-07 15:22:30 ,intervalMinutes = 10,则结果为:2017-11-07 15:20:00
     *
     * @param date
     * @return
     */
    public static Date getIntervalTime(Date date, Integer intervalMinutes) {
        Date intervalTime = DateUtils.truncate(date, Calendar.MINUTE);
        Long currentMinute = DateUtils.getFragmentInMinutes(intervalTime, Calendar.HOUR_OF_DAY);
        if (currentMinute % intervalMinutes == 0) {
            return intervalTime;
        }
        intervalTime = DateUtils.addMinutes(intervalTime, (-currentMinute.intValue() % intervalMinutes));
        return intervalTime;
    }

    /**
     * 获取前n天的此此时刻date的时间列表
     * 如:date = 2017-11-06 08:00:00 ; n=3
     * 返回[2017-11-05 08:00:00,2017-11-04 08:00:00,2017-11-03 08:00:00]
     *
     * @param date
     * @param n
     * @return
     */
    public static List<Date> getPreviousOClockTime(Date date, Integer n) {
        List<Date> result = Lists.newArrayList();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        for (int i = 0; i < n; i++) {
            calendar.add(Calendar.DATE, -1);
            result.add(calendar.getTime());
        }
        return result;
    }


    /**
     * 获取当前日期零点时间字符串
     *
     * @param date
     * @return
     */
    public static String getDayStartTimeStr(Date date) {
        return format(getDayStartTime(date));
    }

    public static void main(String[] args) {
        System.out.println(format(getIntervalTime(new Date(), 5)));
    }


}
