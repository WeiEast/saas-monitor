package com.treefinance.saas.monitor.common.enumeration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author chengtong
 * @date 18/6/5 14:08
 */
public enum EAlarmRecordStatus {
    /**
     * 未处理
     */
    UNPROCESS(0, "未处理"),
    /**
     * 已处理
     */
    PROCESSED(2, "已处理"),
    /**
     * 误报
     */
    WRONG(4, "误报"),
    /**
     * 继续观察
     */
    WAIT(6, "继续观察"),
    /**
     * 由于第三方的问题，无法解决
     */
    DISABLE(8, "无法解决"),
    /**
     * 系统判定恢复
     */
    REPAIRED(10, "系统恢复"),;

    private Integer code;
    private String desc;


    private static Map<Integer, String> map = new HashMap<>();

    static {
        for (EAlarmRecordStatus status: values()) {
            map.put(status.code, status.desc);
        }
    }

    public static String getDesc(Integer code) {
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
