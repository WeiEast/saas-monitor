package com.treefinance.saas.monitor.common.utils;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

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
     * 获取当前日期零点时间
     *
     * @param date
     * @return
     */
    public static Date getDayTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取当前日期零点时间字符串
     *
     * @param date
     * @return
     */
    public static String getDayTimeStr(Date date) {
        return format(getDayTime(date));
    }

}
