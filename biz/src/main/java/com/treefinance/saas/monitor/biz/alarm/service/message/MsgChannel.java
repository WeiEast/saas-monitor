package com.treefinance.saas.monitor.biz.alarm.service.message;

import com.treefinance.saas.monitor.common.enumeration.EAlarmChannel;

import java.lang.annotation.*;

/**
 * 消息通道
 * Created by yh-treefinance on 2018/7/30.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MsgChannel {
    EAlarmChannel value();
}
