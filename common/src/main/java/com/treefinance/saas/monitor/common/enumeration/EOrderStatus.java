package com.treefinance.saas.monitor.common.enumeration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author chengtong
 * @date 18/5/28 17:59
 */
public enum EOrderStatus {

    UNPROCESS(0, "未处理"),
    PROCESSED(1, "已处理"),
    WRONG(2, "误报"),;

    private Integer code;
    private String desc;


    private static Map<Integer,String> map = new HashMap<>();

    static {
        map.put(UNPROCESS.code, UNPROCESS.desc);
        map.put(PROCESSED.code, PROCESSED.desc);
        map.put(WRONG.code, WRONG.desc);
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
