package com.treefinance.saas.monitor.dao.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class MerchantStatDayAccess implements Serializable {

    private Long id;

    private Date dataTime;

    private Byte dataType;

    private Byte saasEnv;

    private String appId;

    private Integer userCount;

    private Integer totalCount;

    private Integer successCount;

    private Integer failCount;

    private Integer cancelCount;

    private BigDecimal successRate;

    private BigDecimal failRate;

    private Integer dailyLimit;

    private Date createTime;

    private Date lastUpdateTime;

    private static final long serialVersionUID = 1L;

}