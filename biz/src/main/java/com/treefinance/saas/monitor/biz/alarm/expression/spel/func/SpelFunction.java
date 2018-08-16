package com.treefinance.saas.monitor.biz.alarm.expression.spel.func;

import com.treefinance.saas.monitor.biz.alarm.utils.AlarmUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.quartz.CronExpression;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by yh-treefinance on 2018/7/23.
 */
public class SpelFunction {

    /**
     * spel 表达式
     */
    public static final String SPEL_PATTERN = "#\\{([^\\{\\}])+\\}";

    /**
     * 为空判断
     *
     * @param val
     * @param defaultVal
     * @return
     */
    public static Object nvl(Object val, Object defaultVal) {
        return val == null ? defaultVal : val;
    }

    /**
     * 时间间隔
     *
     * @param cron
     * @return
     */
    public static long interval(String cron) {
        return AlarmUtils.interval(cron);
    }

    /**
     * 本次执行时间
     *
     * @return
     */
    public static Date current(String cron) {
        return AlarmUtils.current(cron);
    }


    /**
     * @param date
     * @return
     */
    public static String dateToString(Date date) {
        return DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss");
    }

}
