package com.treefinance.saas.monitor.biz.alarm.service.handler;

import java.lang.annotation.*;

/**
 * 组件排序
 * Created by yh-treefinance on 2018/7/24.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Order {
    int value();
}
