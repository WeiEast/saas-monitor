package com.treefinance.saas.monitor.facade.domain.ro.autoalarm;

import com.treefinance.saas.monitor.facade.domain.base.BaseRO;

/**
 * @author haojiahong
 * @date 2018/7/19
 */
public class AsAlarmMsgRO extends BaseRO {
    private Long id;
    /**
     * 预警配置id
     */
    private Long alarmId;
    /**
     * 通知消息标题
     */
    private String titleTemplate;
    /**
     * 通知消息模板
     */
    private String bodyTemplate;

}
