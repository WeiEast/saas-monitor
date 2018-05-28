package com.treefinance.saas.monitor.dao.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class SaasWorker implements Serializable {

    public static final SaasWorker DEFAULT_WORKER = new SaasWorker();
    static {
        DEFAULT_WORKER.name = "程通";
        DEFAULT_WORKER.mobile = "18258265028";
    }


    private Long id;

    private String name;

    private String mobile;
    private String dutyCorn;

    private Date createTime;

    private Date lastUpdateTime;

    private static final long serialVersionUID = 1L;

}