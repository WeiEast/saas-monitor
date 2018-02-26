package com.treefinance.saas.monitor.biz.autostat.utils;

import org.apache.commons.lang3.time.DateUtils;
import org.quartz.CronExpression;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by yh-treefinance on 2018/2/5.
 */
public class CronUtils {
    /**
     * 获取统计时间
     *
     * @param currentTime
     * @param statCron
     * @return
     */
    public static Date getStatDate(Date currentTime, String statCron) {
        CronExpression cronExpression = null;
        try {
            cronExpression = new CronExpression(statCron);
            Date cronDate = cronExpression.getNextValidTimeAfter(currentTime);
            int timeDiff = ((Long) (cronExpression.getNextValidTimeAfter(cronDate).getTime() - cronDate.getTime())).intValue();
            while (currentTime.before(cronDate)) {
                cronDate = DateUtils.addMilliseconds(cronDate, -2 * timeDiff);
                cronDate = cronExpression.getNextValidTimeAfter(cronDate);
            }
            return cronDate;
        } catch (ParseException e) {
            throw new RuntimeException("parse cron exception: cron=" + statCron, e);
        }
    }

    /**
     * 获取时间间隔
     *
     * @param statCron
     * @return
     */
    public static Long getTimeInterval(String statCron) {
        try {
            CronExpression cronExpression = new CronExpression(statCron);

            Date currentTime = new Date();
            Date startTime = cronExpression.getNextValidTimeAfter(currentTime);
            Date endTime = cronExpression.getNextValidTimeAfter(startTime);
            System.out.println("startTime=" + startTime + "，endTime=" + endTime);
            Long expireTime = endTime.getTime() - startTime.getTime();
            if (expireTime < 10 * 60 * 1000) {
                return 10 * 60 * 1000L;
            }
            return expireTime;
        } catch (ParseException e) {
            throw new RuntimeException("parse cron exception: cron=" + statCron, e);
        }
    }

}
