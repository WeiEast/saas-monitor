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
public class AlarmRecord implements Serializable {

    private Long id;

    private Date dataTime;

    private Boolean isProcessed;

    private String level;

    private String summary;

    private String content;

    private Date createTime;

    private Date lastUpdateTime;

    private static final long serialVersionUID = 1L;

}