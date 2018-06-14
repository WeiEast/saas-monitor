package com.treefinance.saas.monitor.facade.domain.ro;

import com.treefinance.saas.monitor.facade.domain.base.BaseRO;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author chengtong
 * @date 18/5/30 11:31
 */
@Setter
@Getter
public class SaasWorkerRO extends BaseRO{
    /**
     * 工作人员编号
     * */
    private Long id;

    /**
     * 工作人员名字
     * */
    private String name;

    /**
     * 联系手机
     * */
    private String mobile;

    /**
     * 邮箱地址
     * */
    private String email;

    /**
     * 工作值班日的corn表达式
     * */
    private String dutyCorn;

    /**
     * 下一个值班日
     * */
    private String nextOnDuty;

    /**
     * 上一个值班日
     * */
    private String preOnDuty;

    private Date createTime;

    private String createTimeStr;

    private Date lastUpdateTime;

    private String lastUpdateTimeStr;

}
