package com.treefinance.saas.monitor.common.enumeration;

/**
 * 预警分类
 * Created by yh-treefinance on 2017/12/6.
 */
public enum EAlarmType {
    no_task("A01"), no_success_task("A02"), conversion_rate_low("A03");

    private String code;

    EAlarmType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
