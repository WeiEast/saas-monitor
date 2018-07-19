package com.treefinance.saas.monitor.facade.domain.ro.autoalarm;

import com.treefinance.saas.monitor.facade.domain.base.BaseRO;
import lombok.Getter;
import lombok.Setter;

/**
 * @author haojiahong
 * @date 2018/7/19
 */
@Getter
@Setter
public class AsAlarmConstantRO extends BaseRO {

    private Long id;
    /**
     * 预警配置id
     */
    private Long alarmId;
    /**
     * 常量名称
     */
    private String name;
    /**
     * 常量编码
     */
    private String code;
    /**
     * 常量值
     */
    private String value;
    /**
     * 常量说明
     */
    private String description;

}
