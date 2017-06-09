package com.treefinance.saas.monitor.facade.domain.base;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

/**
 * 基础查询对象
 * Created by yh-treefinance on 2017/5/27.
 */
public class BaseRequest implements Serializable{

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    @Override
    public String toString() {
        return ReflectionToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }

}
