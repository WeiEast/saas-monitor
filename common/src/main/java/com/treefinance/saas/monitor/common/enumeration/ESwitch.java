package com.treefinance.saas.monitor.common.enumeration;

/**
 * 开关枚举
 * Created by yh-treefinance on 2018/7/19.
 */
public enum ESwitch {
    ON("on", "开"), OFF("off", "关");

    private String code;
    private String name;

    ESwitch(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    /**
     * 是否打开
     *
     * @param switchCode
     * @return
     */
    public static boolean isOn(String switchCode) {
        return ON.code.equalsIgnoreCase(switchCode);
    }

    /**
     * 是否关闭
     *
     * @param switchCode
     * @return
     */
    public static boolean isOff(String switchCode) {
        return OFF.code.equalsIgnoreCase(switchCode);
    }
}
