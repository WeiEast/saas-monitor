package com.treefinance.saas.monitor.common.enumeration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author chengtong
 * @date 18/6/5 14:08
 */
public enum  EAlarmRecordStatus {

    UNPROCESS(0, "未处理"),
    PROCESSED(2, "已处理"),
    WRONG(4, "误报"),
    WAIT(6, "继续观察"),
    DISABLE(8, "无法解决"),;

    private Integer code;
    private String desc;


    private static Map<Integer,String> map = new HashMap<>();

    static {
        map.put(UNPROCESS.code, UNPROCESS.desc);
        map.put(PROCESSED.code, PROCESSED.desc);
        map.put(WRONG.code, WRONG.desc);
        map.put(WAIT.code, WAIT.desc);
        map.put(DISABLE.code, DISABLE.desc);
    }

    public static String getDesc(Integer code){
        return map.get(code);
    }

    EAlarmRecordStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static EAlarmRecordStatus getByValue(Integer code) {
        for (EAlarmRecordStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }


}
