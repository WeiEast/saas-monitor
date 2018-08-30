package com.treefinance.saas.monitor.biz.alarm.model;

/**
 * 消息类型
 * Created by yh-treefinance on 2018/8/27.
 */
public enum EMessageType {
    TEXT(1, "文本"), HTML(2, "html");

    private int code;

    private String name;

    EMessageType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static EMessageType code(Byte code) {
        for (EMessageType type : EMessageType.values()) {
            if (type.equals(code)) {
                return type;
            }
        }
        return null;
    }
}
