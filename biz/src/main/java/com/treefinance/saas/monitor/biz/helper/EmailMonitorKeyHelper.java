package com.treefinance.saas.monitor.biz.helper;

import com.google.common.base.Joiner;
import com.treefinance.saas.monitor.common.enumeration.ETaskStatDataType;
import org.apache.commons.lang.time.DateFormatUtils;

import java.util.Date;

/**
 * @author chengtong
 * @date 18/3/12 10:04
 */
public class EmailMonitorKeyHelper {
    private static  final String NAME_SPACE = "saas-monitor";
    private static final String ALL_DAY_ALARM_KEY = "email-all-day-alarm";

    /**
     * 邮箱大盘的监控key 会包含所有的已经预警过的时间段
     * @param appId
     * example：SAAS-MONITOR:email-all-day-alarm:2018-03-12:vertual-all:0
     * */
    public static String genEmailAllKey(Date intervalTime, String appId, ETaskStatDataType statType){
        String interval = DateFormatUtils.format(intervalTime, "yyyy-MM-dd");
        return Joiner.on(":").useForNull("null").join(NAME_SPACE, ALL_DAY_ALARM_KEY, interval, appId,statType.getCode());
    }

    public static void main(String...args){

        System.err.println(genEmailAllKey(new Date(),"vertual-all",ETaskStatDataType.TASK));

    }

}
