package com.treefinance.saas.monitor.biz.helper;

import com.alibaba.fastjson.JSONObject;
import com.treefinance.saas.monitor.biz.config.DiamondConfig;
import com.treefinance.saas.monitor.biz.config.EmailAlarmConfig;
import com.treefinance.saas.monitor.common.domain.dto.alarmconfig.TaskSuccessRateAlarmConfigDTO;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.time.DateUtils;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by yh-treefinance on 2017/6/9.
 */
public abstract class StatHelper {


    public static DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");

    /**
     * 计算最近间隔时间
     *
     * @param date
     * @param intervalMinutes
     * @return
     */
    public static Date calculateIntervalTime(Date date, int intervalMinutes) {
        Date hourTime = DateUtils.truncate(date, Calendar.HOUR_OF_DAY);
        // 當前分鐘
        Long currentMinute = DateUtils.getFragmentInMinutes(date, Calendar.HOUR_OF_DAY);

        Date intervalStartTime = DateUtils.addMinutes(hourTime,
                (currentMinute.intValue() / intervalMinutes) * intervalMinutes);
        Date intervalEndTime = DateUtils.addMinutes(hourTime,
                (currentMinute.intValue() / intervalMinutes + 1) * intervalMinutes);
        if ((date.after(intervalStartTime) || date.equals(intervalStartTime)) && date.before(intervalEndTime)) {
            return intervalStartTime;
        }
        return intervalEndTime;
    }


    /**
     * 计算日期
     *
     * @param date
     * @return
     */
    public static Date calculateDayTime(Date date) {
        return DateUtils.truncate(date, Calendar.DAY_OF_MONTH);
    }


    /**
     * redis中存入的时间段key,如设置60为:7:00,8:00,9:00
     * 若dataTime=2017-11-06 07:05:55 ,intervalMinutes = 60 则返回 2017-11-06 07:00:00
     *
     * @param dataTime
     * @return
     */
    public static Date getRedisStatDateTime(Date dataTime, Integer intervalMinutes) {
        if (intervalMinutes == null) {
            intervalMinutes = 5;
        }
        Date intervalTime = DateUtils.truncate(dataTime, Calendar.MINUTE);
        Long currentMinute = DateUtils.getFragmentInMinutes(intervalTime, Calendar.HOUR_OF_DAY);
        if (currentMinute % intervalMinutes == 0) {
            return intervalTime;
        }
        intervalTime = DateUtils.addMinutes(intervalTime, (-currentMinute.intValue() % intervalMinutes));
        return intervalTime;
    }


    public static void main(String[] args) throws ParseException {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = fmt.parse("2017-11-22 19:59:59");
        System.out.println(fmt.format(StatHelper.calculateIntervalTime(date, 10)));
        date = fmt.parse("2017-11-22 20:00:00");
        System.out.println(fmt.format(StatHelper.calculateIntervalTime(date, 10)));
        date = fmt.parse("2017-11-22 20:10:59");
        System.out.println(fmt.format(StatHelper.calculateIntervalTime(date, 10)));
    }
    public static StartTimeModel getDiffDuration(Date endTime, Date startTime) {

        long endStamp = endTime.getTime();
        long startStamp = startTime.getTime();

        long realTime = endStamp - startStamp;

        StartTimeModel model = new StartTimeModel();

        double duration = Double.parseDouble(DECIMAL_FORMAT.format(realTime / (60 * 1000)));

        model.setDuration(duration);

        return model;
    }


    private static Integer calcTypedDuration(String type,DiamondConfig config) {
        String configStr = config.getTaskSuccessRateAlarmConfig();
        List<TaskSuccessRateAlarmConfigDTO> configList = JSONObject.parseArray(configStr, TaskSuccessRateAlarmConfigDTO.class);
        List<TaskSuccessRateAlarmConfigDTO> targetList = configList.stream().filter(taskSuccessRateAlarmConfigDTO -> type.equalsIgnoreCase
                (taskSuccessRateAlarmConfigDTO
                        .getType())).collect(Collectors.toList());

        TaskSuccessRateAlarmConfigDTO configDTO = targetList.get(0);

        int times = configDTO.getTimes();
        int intervals = configDTO.getIntervalMins();

        return times * intervals;
    }

    @Setter
    @Getter
    public static class StartTimeModel{

        private Date startTime;

        private Double duration;

    }


}
