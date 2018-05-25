package com.treefinance.saas.monitor.common.constants;

/**
 * @author chengtong
 * @date 18/3/12 17:26
 */
public interface AlarmConstants {

    String SWITCH_ON = "on";
    String SWITCH_OFF = "off";

    /** 数据库表指所有商户的值*/
    String VIRTUAL_TOTAL_STAT_APP_ID = "virtual_total_stat_appId";

    /** 邮箱预警-大盘-按人数*/
    String EMAIL_ALL_USER = "EMAIL_ALL_USER";
    /** 邮箱预警-分邮箱-按人数*/
    String EMAIL_GROUP_USER = "EMAIL_GROUP_USER";

    /** 邮箱预警-大盘-按任务数*/
    String EMAIL_ALL_TASK = "EMAIL_ALL_TASK";
    /** 邮箱预警-分邮箱-按任务数*/
    String EMAIL_GROUP_TASK = "EMAIL_GROUP_TASK";

    /**所有邮箱类型*/
    String ALL_EMAIL = "virtual_total_stat_email";
    /**分邮箱类型*/
    String GROUP_EMAIL = "virtual_group_stat_email";


    /**运营商的预警类型*/
    Integer ALL_OPERATOR = 1;

    Integer GROUP_OPERATOR = 2;



}
