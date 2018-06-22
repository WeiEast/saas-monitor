package com.treefinance.saas.monitor.dao.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SaasWorker implements Serializable {

    public static SaasWorker DEFAULT_WORKER = new SaasWorker();

    static {
        DEFAULT_WORKER.setEmail("chengtong@treefinance.com.cn");
        DEFAULT_WORKER.setMobile("18258265028");
        DEFAULT_WORKER.setName("程通");
    }


    private Long id;

    private String name;

    private String mobile;

    private String email;

    private String dutyCorn;

    private Date createTime;

    private Date lastUpdateTime;

    private static final long serialVersionUID = 1L;

}