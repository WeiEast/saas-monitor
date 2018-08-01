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
public class AsAlarmQueryRO extends BaseRO {

    private Long id;

    private Integer queryIndex;
    /**
     * 预警配置id
     */
    private Long alarmId;
    /**
     * 查询结果编码
     */
    private String resultCode;
    /**
     * 查询语句
     */
    private String querySql;
    /**
     * 说明
     */
    private String description;

}
