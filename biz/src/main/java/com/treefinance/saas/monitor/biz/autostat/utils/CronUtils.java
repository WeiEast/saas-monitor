package com.treefinance.saas.monitor.biz.autostat.utils;

import com.treefinance.saas.monitor.common.utils.MonitorDateUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.quartz.CronExpression;

import java.text.ParseException;
import java.util.Calendar;
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
     * redis中存入的时间段key,如设置60为:7:00,8:00,9:00
     * 若dataTime=2017-11-06 07:05:55 ,intervalMinutes = 60 则返回 2017-11-06 07:00:00
     *
     * @param dataTime
     * @return
     */
    public static Date getStatDate(Date dataTime, Integer intervalMinutes) {
        if (intervalMinutes == null) {
            intervalMinutes = 60;//默认定时统计时间段为60分钟
        }
        Date intervalTime = org.apache.commons.lang.time.DateUtils.truncate(dataTime, Calendar.MINUTE);
        Long currentMinute = org.apache.commons.lang.time.DateUtils.getFragmentInMinutes(intervalTime, Calendar.HOUR_OF_DAY);
        if (currentMinute % intervalMinutes == 0) {
            return intervalTime;
        }
        intervalTime = org.apache.commons.lang.time.DateUtils.addMinutes(intervalTime, (-currentMinute.intValue() % intervalMinutes));
        return intervalTime;
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
            Long expireTime = endTime.getTime() - startTime.getTime();
            if (expireTime < 10 * 60 * 1000) {
                return 3 * 10 * 60 * 1000L;
            }
            return 3 * expireTime;
        } catch (ParseException e) {
            throw new RuntimeException("parse cron exception: cron=" + statCron, e);
        }
    }

    public static void main(String[] args) {
        String statCron = "* * * ? * Mon";
//        Date date = CronUtils.getStatDate(new Date(), (Integer) null);
//        System.out.println(MonitorDateUtils.format(date));

        getPreMeetDay(statCron,new Date());

    }

    public static Date getNextMeetDay(String corn,Date now){
        try {
            CronExpression cronExpression = new CronExpression(corn);
            return cronExpression.getNextValidTimeAfter(MonitorDateUtils.getDayStartTime(now));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;

    }
    public static Date getPreMeetDay(String corn,Date now){
        try {
            CronExpression cronExpression = new CronExpression(corn);
            Date after = cronExpression.getNextValidTimeAfter(MonitorDateUtils.getDayStartTime(now));
            Date afterAfter = cronExpression.getNextValidTimeAfter(new Date(after.getTime() + 24*60*60*1000));

            Long interval = afterAfter.getTime() - after.getTime();

            return new Date(after.getTime() - interval);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;

    }

}
