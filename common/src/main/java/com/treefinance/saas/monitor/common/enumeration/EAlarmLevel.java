package com.treefinance.saas.monitor.common.enumeration;

/**
 * Created by yh-treefinance on 2017/12/6.
 */
public enum EAlarmLevel {
    info, warning, error;

    public static void main(String[] args) {
        for (EAlarmLevel alarmLevel : EAlarmLevel.values()) {
            System.out.println(alarmLevel);
        }
    }


    public static EAlarmLevel getLevel(String alarmLever) {
        for (EAlarmLevel alarmLevel : values()) {
            if (alarmLevel.name().equalsIgnoreCase(alarmLever)) {
                return alarmLevel;
            }
        }
        return null;
    }
}
