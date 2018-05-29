package com.treefinance.saas.monitor.dao.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class WorkOrderLog implements Serializable {
    private Long id;

    private Long orderId;

    private Long recordId;

    private String opName;

    private String opDesc;

    private Date createTime;

    private Date lastUpdateTime;

    private static final long serialVersionUID = 1L;

}