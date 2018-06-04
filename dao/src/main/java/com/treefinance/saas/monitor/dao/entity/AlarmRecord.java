package com.treefinance.saas.monitor.dao.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author chengtong
 */
@Data
public class AlarmRecord implements Serializable {

    private Long id;

    private Date dataTime;

    private Boolean isProcessed;

    private String level;

    private String alarmType;

    private String summary;

    private String content;

    private Date createTime;

    private Date lastUpdateTime;

    private static final long serialVersionUID = 1L;


}