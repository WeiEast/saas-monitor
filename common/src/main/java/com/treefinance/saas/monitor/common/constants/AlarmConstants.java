package com.treefinance.saas.monitor.common.constants;

/**
 * @author chengtong
 * @date 18/3/12 17:26
 */
public interface AlarmConstants {

    String SWITCH_ON = "on";
    String SWITCH_OFF = "off";

    /**
     * 数据库表指所有商户的值
     */
    String VIRTUAL_TOTAL_STAT_APP_ID = "virtual_total_stat_appId";

    /**
     * 所有邮箱类型
     */
    String ALL_EMAIL = "virtual_total_stat_email";
    /**
     * 分邮箱类型
     */
    String GROUP_EMAIL = "virtual_group_stat_email";

    /**
     * 邮箱预警的类型的中文描述，也是区分业务的标志
     */
    String ALL_EMAIL_FLAG = "邮箱大盘";

    /**
     * 运营商的预警类型
     */
    Integer ALL_OPERATOR = 1;

    Integer GROUP_OPERATOR = 2;

    String OPERATOR_ALARM = "operator_alarm";
    String EMAIL_ALARM = "email_alarm";
    String TASK_SUCCESS_ALARM_OPERATOR = "task_success_alarm:OPERATOR";
    String TASK_SUCCESS_ALARM_ECOMMERCE = "task_success_alarm:ECOMMERCE";

}
