package com.treefinance.saas.monitor.biz.alarm.utils;

import org.apache.commons.lang3.time.DateUtils;
import org.quartz.CronExpression;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by yh-treefinance on 2018/7/23.
 */
public class AlarmUtils {
    /**
     * 时间间隔
     *
     * @param cron
     * @return
     */
    public static long interval(String cron) {
        try {
            CronExpression cronExpression = new CronExpression(cron);
            Date currentTime = new Date();
            Date startTime = cronExpression.getNextValidTimeAfter(currentTime);
            Date endTime = cronExpression.getNextValidTimeAfter(startTime);
            Long expireTime = endTime.getTime() - startTime.getTime();
            return expireTime;
        } catch (ParseException e) {
            throw new RuntimeException("parse cron exception: cron=" + cron, e);
        }
    }

    /**
     * 本次时间
     *
     * @param cron
     * @return
     */
    public static Date current(String cron) {
        try {
            CronExpression cronExpression = new CronExpression(cron);
            Date currentTime = new Date();
            Date nextTime = cronExpression.getNextValidTimeAfter(currentTime);
            Long interval = interval(cron);
            Date current = DateUtils.addMilliseconds(nextTime, -interval.intValue());
            return current;
        } catch (ParseException e) {
            throw new RuntimeException("parse cron exception: cron=" + cron, e);
        }
    }
}
