package com.treefinance.saas.monitor.common.domain.dto.alarmconfig;

import java.io.Serializable;
import java.time.LocalTime;

/**
 * @author chengtong
 * @date 18/5/16 11:01
 */
public class BaseTimeConfig implements Serializable{

    /** hh:mm:ss*/
    private String startTime;
    /** hh:mm:ss*/
    private String endTime;



    public boolean isInTime(){
        LocalTime start = LocalTime.parse(startTime);
        LocalTime end = LocalTime.parse(endTime);
        return LocalTime.now().isAfter(start) && LocalTime.now().isBefore(end);
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

}
