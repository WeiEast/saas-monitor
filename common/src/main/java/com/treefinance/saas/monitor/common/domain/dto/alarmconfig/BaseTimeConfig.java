package com.treefinance.saas.monitor.common.domain.dto.alarmconfig;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.HashMap;

/**
 * @author chengtong
 * @date 18/5/16 11:01
 */
@Setter
@Getter
@ToString
public class BaseTimeConfig implements Serializable{

    /** hh:mm:ss*/
    private String startTime;
    /** hh:mm:ss*/
    private String endTime;

    /**集成的开关配置*/
    private HashMap<String, String> switches;

    public boolean isInTime(){
        LocalTime start = LocalTime.parse(startTime);
        LocalTime end = LocalTime.parse(endTime);
        return LocalTime.now().isAfter(start) && LocalTime.now().isBefore(end);
    }


}
