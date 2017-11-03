package com.treefinance.saas.monitor.common.utils;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by haojiahong on 2017/11/3.
 */
public class MonitorDateUtils {

    public static String format(Date date) {
        return DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss");
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
}
