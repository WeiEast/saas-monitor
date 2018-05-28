package com.treefinance.saas.monitor.common.enumeration;

/**
 * 预警分类
 * Created by yh-treefinance on 2017/12/6.
 */
public enum EAlarmType {

    /**
     * 无任务预警
     */
    no_task("A01"),
    /**
     * 无成功任务预警
     */
    no_success_task("A02"),

    conversion_rate_low("A03"),

    /**
     * 运营商预警
     */
    operator_alarm("operator_alarm"),

    /**
     * 邮箱预警
     */
    email_alarm("A04"),

    /**
     * 电商预警
     */
    ecommerce_alarm("A05"),;

    private String code;
    private String desc;

    EAlarmType(String code) {
        this.code = code;
    }



    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
