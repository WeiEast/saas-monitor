package com.treefinance.saas.monitor.common.enumeration;

/**
 * @author chengtong
 * @date 18/3/13 20:26
 */
public enum EAlarmChannel {

    IVR("ivr"),
    SMS("sms"),
    EMAIL("email"),
    WECHAT("wechat"),

    ;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    private String value;


    EAlarmChannel(String value) {
        this.value = value;
    }

    public static EAlarmChannel getByValue(String value){
        for(EAlarmChannel channel:values()){
            if(value.equals(channel.getValue())){
                return channel;
            }
        }
        return null;
    }



}
