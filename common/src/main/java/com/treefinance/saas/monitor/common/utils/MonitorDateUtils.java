package com.treefinance.saas.monitor.common.utils;

import com.google.common.collect.Lists;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by haojiahong on 2017/11/3.
 */
public class MonitorDateUtils {

    /**
     * 获取dataTime在间隔时间内的开始时间,如dataTime=19:41,intervalMinutes=5,则19:40
     *
     * @param dataTime
     * @return
     */
    public static Date getIntervalDateTime(Date dataTime, Integer intervalMinutes) {
        Date intervalTime = DateUtils.truncate(dataTime, Calendar.MINUTE);
        Long currentMinute = DateUtils.getFragmentInMinutes(intervalTime, Calendar.HOUR_OF_DAY);
        if (currentMinute % intervalMinutes == 0) {
            return intervalTime;
        }
        intervalTime = DateUtils.addMinutes(intervalTime, (-currentMinute.intValue() % intervalMinutes));
        return intervalTime;
    }

    /**
     * 获取dataTime在间隔时间内的结束时间,如dataTime=19:41,intervalMinutes=5,则19:45
     *
     * @param dataTime
     * @return
     */
    public static Date getLaterIntervalDateTime(Date dataTime, Integer intervalMinutes) {
        Date intervalTime = DateUtils.truncate(dataTime, Calendar.MINUTE);
        Long currentMinute = DateUtils.getFragmentInMinutes(intervalTime, Calendar.HOUR_OF_DAY);
        if (currentMinute % intervalMinutes == 0) {
            return intervalTime;
        }
        intervalTime = DateUtils.addMinutes(intervalTime, (intervalMinutes - (currentMinute.intValue() % intervalMinutes)));
        return intervalTime;
    }

    /**
     * 获取dataTime在间隔时间内的结束时间,边界值后移
     * 如dataTime=19:41,intervalMinutes=5,则19:45
     * dataTime=19:40,intervalMinutes=5,则19:45
     *
     * @param dataTime
     * @return
     */
    public static Date getLaterBorderIntervalDateTime(Date dataTime, Integer intervalMinutes) {
        Date intervalTime = DateUtils.truncate(dataTime, Calendar.MINUTE);
        Long currentMinute = DateUtils.getFragmentInMinutes(intervalTime, Calendar.HOUR_OF_DAY);
        if (currentMinute % intervalMinutes == 0) {
            return DateUtils.addMinutes(intervalTime, intervalMinutes);
        }
        intervalTime = DateUtils.addMinutes(intervalTime, (intervalMinutes - (currentMinute.intValue() % intervalMinutes)));
        return intervalTime;
    }


    public static String format(Date date) {
        return DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss");
    }

    public static String format2Ymd(Date date) {
        return DateFormatUtils.format(date, "yyyy-MM-dd");
    }

    public static String format2Hms(Date date) {
        return DateFormatUtils.format(date, "HH:mm:ss");
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
    public static Date parse(String dateStr,String pattern) {
        Date date = null;
        try {
            date = DateUtils.parseDate(dateStr, pattern);
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

    public static Date addTimeUnit(Date date, int type, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(type, amount);
        return calendar.getTime();
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

    /**
     * 判断当前时间是否在此区间内
     *
     * @param tStartStr 字符串时间形式为HH:mm
     * @param tEndStr   字符串时间形式为HH:mm
     * @return
     */
    public static Boolean isInZone(String tStartStr, String tEndStr) {
        return MonitorDateUtils.isInZone(tStartStr, tEndStr, new Date());
    }

    /**
     * 判断t时间是否在此区间内
     *
     * @param tStartStr 字符串时间形式为HH:mm
     * @param tEndStr   字符串时间形式为HH:mm
     * @param now       比较的时间
     * @return
     */
    public static Boolean isInZone(String tStartStr, String tEndStr, Date now) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        long tStart, tEnd, t;
        try {
            tStart = sdf.parse(tStartStr).getTime();
            tEnd = sdf.parse(tEndStr).getTime();
            t = sdf.parse(sdf.format(now)).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
        return tStart <= t && t < tEnd;
    }


    public static boolean isSameDay(Date dayOne, Date dayTwo) {

        Calendar calendarOne = Calendar.getInstance(Locale.CHINA);
        calendarOne.setTime(dayOne);

        Calendar calendarTwo = Calendar.getInstance(Locale.CHINA);
        calendarTwo.setTime(dayTwo);
//
//        calendarOne.set(Calendar.MINUTE, 0);
//        calendarOne.set(Calendar.HOUR, 0);
//        calendarOne.set(Calendar.SECOND, 0);
//        calendarTwo.set(Calendar.MINUTE, 0);
//        calendarTwo.set(Calendar.HOUR, 0);
//        calendarTwo.set(Calendar.SECOND, 0);

        return (calendarOne.get(Calendar.YEAR) == calendarTwo.get(Calendar.YEAR)) && (calendarOne.get(Calendar.MONTH) == calendarTwo.get(Calendar.MONTH) && calendarOne.get(Calendar.DATE) == calendarTwo.get(Calendar.DATE));

    }

}
