package com.treefinance.saas.monitor.biz.alarm.expression;

import com.treefinance.saas.monitor.biz.alarm.model.EMessageType;

import java.lang.annotation.*;

/**
 * Created by yh-treefinance on 2018/8/27.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Analysis {

    /**
     * 解析方式
     *
     * @return
     */
    EMessageType value();
}
