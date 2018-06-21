package com.treefinance.saas.monitor.common.enumeration;

/**
 * @author chengtong
 * @date 18/5/28 14:12
 * <p>
 * 电商、邮箱、运营商的预警的指标的枚举
 */
public enum EAlarmAspectType {
    /**
     * 登录转化率*
     */
    LOGIN_CONVERSION("AS_LC"),
    /**
     * 登陆成功率
     */
    LOGIN_SUCCESS("AS_LS"),
    /**
     * 爬树成功率
     */
    CRAW_SUCCESS("AS_CS"),
    /**
     * 洗数成功率
     */
    PROCESS_SUCCESS("AS_PS"),
    /**
     * 回调成功率
     */
    CALLBACK_SUCCESS("AS_CBS"),
    /**
     * 总转化率
     */
    WHOLE_CONVERSION("AS_WC"),
    /**
     * 确认手机转化率
     */
    CONFIRM_MOBILE_CONVERSION("AS_CMC"),;




    EAlarmAspectType(String value) {
        this.value = value;
    }

    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
