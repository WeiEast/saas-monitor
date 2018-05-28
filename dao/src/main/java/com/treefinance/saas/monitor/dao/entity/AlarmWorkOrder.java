package com.treefinance.saas.monitor.dao.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * @author chengtong
 */
@Setter
@Getter
public class AlarmWorkOrder implements Serializable {

    private Long id;

    private Long recordId;

    private Integer status;

    private String dutyName;

    private String processorName;

    private String remark;

    private Date createTime;

    private Date lastUpdateTime;

    private static final long serialVersionUID = 1L;

}