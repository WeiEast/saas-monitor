package com.treefinance.saas.monitor.common.enumeration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author chengtong
 * @date 18/5/28 17:59
 */
public enum EOrderStatus {

    UNPROCESS(0, "未处理"),
    PROCESSED(2, "已处理"),
    WRONG(4, "误报"),
    WAIT(6, "继续观察"),
    DISABLE(8, "无法解决"),
    REPAIRED(10, "系统恢复"),;

    private Integer code;
    private String desc;


    private static Map<Integer,String> map = new HashMap<>();

    static {
        for (EOrderStatus status: values()) {
            map.put(status.code, status.desc);
        }
    }

    public static String getDesc(Integer code){
        return map.get(code);
    }

    EOrderStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static EOrderStatus getByValue(Integer code) {
        for (EOrderStatus status : values()) {
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
