package com.treefinance.saas.monitor.facade.domain.ro;

import com.treefinance.saas.monitor.facade.domain.base.BaseRO;
import lombok.Getter;
import lombok.Setter;

/**
 * @author:guoguoyun
 * @date:Created in 2018/7/18下午7:34
 */
@Getter
@Setter
public class AsAlarmRO extends BaseRO{

    /**
     * 预警配置ID
     */
    private  Long id ;
    /**
     * 预警名称
     */
    private  String name;
    /**
     * 预警执行环境(0-所有，1-生产，2-预发布)
     */
    private Byte runEnv;
    /**
     * 预警开关
     */
    private String alarmSwitch;
    /**
     * 预警执行时间
     */
    private String runInterval;
    /**
     * 预警时间间隔
     */
    private Integer timeInterval;
}
