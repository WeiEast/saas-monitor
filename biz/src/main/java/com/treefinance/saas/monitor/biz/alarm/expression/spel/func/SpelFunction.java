package com.treefinance.saas.monitor.biz.alarm.expression.spel.func;

import com.treefinance.saas.monitor.biz.alarm.utils.AlarmUtils;
import org.quartz.CronExpression;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by yh-treefinance on 2018/7/23.
 */
public class SpelFunction {
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

}
